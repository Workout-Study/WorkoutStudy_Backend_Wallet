package com.fitmate.walletservice.service

import com.fitmate.walletservice.component.WalletLockComponent
import com.fitmate.walletservice.dto.DepositRequest
import com.fitmate.walletservice.dto.DepositResponse
import com.fitmate.walletservice.exception.NotExpectResultException
import org.redisson.api.RLock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class WalletServiceImpl(
    private val walletLockComponent: WalletLockComponent
) : WalletService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(WalletServiceImpl::class.java)
    }

    override fun deposit(depositRequest: DepositRequest): DepositResponse {
        val lock: RLock = walletLockComponent.getWalletLock(depositRequest.requestUserId)

        try {
            val isLocked: Boolean = lock.tryLock(4, 4, TimeUnit.SECONDS)

            if (!isLocked) {
                // 락 획득에 실패했으므로 예외 처리
                logger.error("fail to get wallet Lock : user-id = {}", depositRequest.requestUserId)
                throw NotExpectResultException("fail to get wallet lock.")
            }

            return DepositResponse(true)
        } catch (e: InterruptedException) {
            // 쓰레드가 인터럽트 될 경우의 예외 처리

            throw NotExpectResultException(e.message!!)
        } finally {
            if (lock.isLocked && lock.isHeldByCurrentThread) {
                // 락 해제
                lock.unlock()
            }

        }
    }
}