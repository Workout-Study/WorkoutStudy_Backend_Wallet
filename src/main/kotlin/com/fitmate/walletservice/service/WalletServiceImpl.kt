package com.fitmate.walletservice.service

import com.fitmate.walletservice.common.GlobalStatus
import com.fitmate.walletservice.component.WalletLockComponent
import com.fitmate.walletservice.dto.DepositRequest
import com.fitmate.walletservice.dto.DepositResponse
import com.fitmate.walletservice.exception.NotExpectResultException
import com.fitmate.walletservice.exception.ResourceNotFoundException
import com.fitmate.walletservice.persistence.entity.Deposit
import com.fitmate.walletservice.persistence.entity.Wallet
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import com.fitmate.walletservice.persistence.entity.WalletState
import com.fitmate.walletservice.persistence.repository.DepositRepository
import com.fitmate.walletservice.persistence.repository.WalletRepository
import org.redisson.api.RLock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class WalletServiceImpl(
    private val walletLockComponent: WalletLockComponent,
    private val walletRepository: WalletRepository,
    private val depositRepository: DepositRepository
) : WalletService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(WalletServiceImpl::class.java)
    }

    override fun deposit(depositRequest: DepositRequest): DepositResponse {
        val wallet = walletRepository.findByOwnerIdAndOwnerTypeAndState(
            depositRequest.requestUserId,
            WalletOwnerType.USER,
            GlobalStatus.PERSISTENCE_NOT_DELETED
        ).orElseGet {
            val newWallet =
                Wallet(
                    depositRequest.requestUserId,
                    WalletOwnerType.USER,
                    depositRequest.requestUserId.toString()
                )

            walletRepository.save(newWallet)
        }

        val deposit =
            Deposit(wallet, depositRequest.amount, "입금", WalletState.REQUESTED, depositRequest.requestUserId.toString())
        val savedDeposit = depositRepository.save(deposit)

        val lock: RLock = walletLockComponent.getWalletLock(depositRequest.requestUserId)

        try {
            val isLocked: Boolean = lock.tryLock(4, 4, TimeUnit.SECONDS)

            if (!isLocked) {
                // 락 획득에 실패했으므로 예외 처리
                logger.error("fail to get wallet Lock : user-id = {}", depositRequest.requestUserId)
                savedDeposit.state = WalletState.FAILED
                throw NotExpectResultException("fail to get wallet lock.")
            }

            val lastWallet = walletRepository.findById(wallet.id!!)
                .orElseThrow { ResourceNotFoundException("wallet does not exist") }

            lastWallet.balance += savedDeposit.amount
            walletRepository.save(lastWallet)

            savedDeposit.state = WalletState.COMPLETED

            return DepositResponse(true)
        } catch (e: Exception) {
            // 쓰레드가 인터럽트 될 경우의 예외 처리
            savedDeposit.state = WalletState.FAILED

            throw NotExpectResultException(e.message!!)
        } finally {
            if (lock.isLocked && lock.isHeldByCurrentThread) {
                // 락 해제
                lock.unlock()
            }

            depositRepository.save(savedDeposit)
        }
    }
}