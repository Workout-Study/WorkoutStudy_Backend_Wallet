package com.fitmate.walletservice.service

import com.fitmate.walletservice.common.GlobalStatus
import com.fitmate.walletservice.component.WalletLockComponent
import com.fitmate.walletservice.dto.DepositRequest
import com.fitmate.walletservice.dto.DepositResponse
import com.fitmate.walletservice.dto.WithdrawRequest
import com.fitmate.walletservice.dto.WithdrawResponse
import com.fitmate.walletservice.exception.NotExpectResultException
import com.fitmate.walletservice.exception.ResourceNotFoundException
import com.fitmate.walletservice.persistence.entity.*
import com.fitmate.walletservice.persistence.repository.DepositRepository
import com.fitmate.walletservice.persistence.repository.WalletRepository
import com.fitmate.walletservice.persistence.repository.WithdrawRepository
import org.redisson.api.RLock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class WalletServiceImpl(
    private val walletLockComponent: WalletLockComponent,
    private val walletRepository: WalletRepository,
    private val depositRepository: DepositRepository,
    private val withdrawRepository: WithdrawRepository
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

        val lock: RLock = walletLockComponent.getWalletLock(wallet.id!!)

        try {
            val isLocked: Boolean = lock.tryLock(4, 4, TimeUnit.SECONDS)

            if (!isLocked) {
                // 락 획득에 실패했으므로 예외 처리
                logger.error("fail to get wallet Lock deposit : wallet-id = {}", wallet.id)
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

    override fun withdraw(withdrawRequest: WithdrawRequest): WithdrawResponse {
        val wallet = walletRepository.findByOwnerIdAndOwnerTypeAndState(
            withdrawRequest.requestUserId,
            WalletOwnerType.USER,
            GlobalStatus.PERSISTENCE_NOT_DELETED
        ).orElseGet {
            val newWallet =
                Wallet(
                    withdrawRequest.requestUserId,
                    WalletOwnerType.USER,
                    withdrawRequest.requestUserId.toString()
                )

            walletRepository.save(newWallet)
        }

        val withdraw =
            Withdraw(
                wallet,
                withdrawRequest.amount,
                "출금",
                WalletState.REQUESTED,
                withdrawRequest.requestUserId.toString()
            )
        val savedWithdraw = withdrawRepository.save(withdraw)

        val lock: RLock = walletLockComponent.getWalletLock(wallet.id!!)

        try {
            val isLocked: Boolean = lock.tryLock(4, 4, TimeUnit.SECONDS)

            if (!isLocked) {
                // 락 획득에 실패했으므로 예외 처리
                logger.error("fail to get wallet Lock withdraw : wallet-id = {}", wallet.id)
                savedWithdraw.state = WalletState.FAILED
                throw NotExpectResultException("fail to get wallet lock.")
            }

            val lastWallet = walletRepository.findById(wallet.id!!)
                .orElseThrow { ResourceNotFoundException("wallet does not exist") }

            lastWallet.balance -= savedWithdraw.amount
            walletRepository.save(lastWallet)

            savedWithdraw.state = WalletState.COMPLETED

            return WithdrawResponse(true)
        } catch (e: Exception) {
            // 쓰레드가 인터럽트 될 경우의 예외 처리
            savedWithdraw.state = WalletState.FAILED

            throw NotExpectResultException(e.message!!)
        } finally {
            if (lock.isLocked && lock.isHeldByCurrentThread) {
                // 락 해제
                lock.unlock()
            }

            withdrawRepository.save(savedWithdraw)
        }
    }
}