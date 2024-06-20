package com.fitmate.walletservice.module

import com.fitmate.walletservice.common.GlobalStatus
import com.fitmate.walletservice.component.WalletLockComponent
import com.fitmate.walletservice.dto.*
import com.fitmate.walletservice.exception.NotExpectResultException
import com.fitmate.walletservice.exception.ResourceNotFoundException
import com.fitmate.walletservice.persistence.entity.*
import com.fitmate.walletservice.persistence.repository.*
import org.redisson.api.RLock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class WalletModuleServiceImpl(
    private val walletLockComponent: WalletLockComponent,
    private val walletRepository: WalletRepository,
    private val depositRepository: DepositRepository,
    private val withdrawRepository: WithdrawRepository,
    private val transferRepository: TransferRepository,
    private val walletTraceRepository: WalletTraceRepository
) : WalletModuleService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(WalletModuleServiceImpl::class.java)
    }

    override fun deposit(depositRequestDto: DepositRequestDto): Deposit {
        val wallet = getWalletRegisterIfNotExist(
            depositRequestDto.walletOwnerId,
            depositRequestDto.walletOwnerType,
            depositRequestDto.requester
        )

        val deposit =
            Deposit(
                wallet,
                depositRequestDto.amount,
                depositRequestDto.message ?: "입금",
                TradeState.REQUESTED,
                depositRequestDto.requester,
                depositRequestDto.transfer
            )
        val savedDeposit = depositRepository.save(deposit)

        val lock: RLock = walletLockComponent.getWalletLock(wallet.id!!)

        try {
            val isLocked: Boolean = lock.tryLock(4, 4, TimeUnit.SECONDS)

            if (!isLocked) {
                // 락 획득에 실패했으므로 예외 처리
                logger.error("fail to get wallet Lock deposit : wallet-id = {}", wallet.id)
                savedDeposit.state = TradeState.FAILED
                throw NotExpectResultException("fail to get wallet lock.")
            }

            val lastWallet = walletRepository.findById(wallet.id!!)
                .orElseThrow { ResourceNotFoundException("wallet does not exist") }

            lastWallet.balance += savedDeposit.amount
            walletRepository.save(lastWallet)

            savedDeposit.state = TradeState.COMPLETED

            val walletTrace = WalletTrace(
                lastWallet,
                TradeType.DEPOSIT,
                savedDeposit.id!!,
                createUser = depositRequestDto.requester
            )
            walletTraceRepository.save(walletTrace)

            return savedDeposit
        } catch (e: Exception) {
            // 쓰레드가 인터럽트 될 경우의 예외 처리
            savedDeposit.state = TradeState.FAILED

            logger.error("fail to withdraw with exception : wallet-id = {}, exception = {}", wallet.id, e.stackTrace)

            return savedDeposit
        } finally {
            if (lock.isLocked && lock.isHeldByCurrentThread) {
                // 락 해제
                lock.unlock()
            }

            depositRepository.save(savedDeposit)
        }
    }

    override fun getWalletRegisterIfNotExist(
        walletOwnerId: Int,
        walletOwnerType: WalletOwnerType,
        requester: String
    ): Wallet {
        val wallet = walletRepository.findByOwnerIdAndOwnerTypeAndState(
            walletOwnerId,
            walletOwnerType,
            GlobalStatus.PERSISTENCE_NOT_DELETED
        ).orElseGet {
            val newWallet =
                Wallet(
                    walletOwnerId,
                    walletOwnerType,
                    requester
                )

            walletRepository.save(newWallet)
        }
        return wallet
    }

    override fun withdraw(withdrawRequestDto: WithdrawRequestDto): Withdraw {
        val wallet = getWalletRegisterIfNotExist(
            withdrawRequestDto.walletOwnerId,
            withdrawRequestDto.walletOwnerType,
            withdrawRequestDto.requester
        )

        val withdraw =
            Withdraw(
                wallet,
                withdrawRequestDto.amount,
                withdrawRequestDto.message ?: "출금",
                TradeState.REQUESTED,
                withdrawRequestDto.requester,
                withdrawRequestDto.transfer
            )
        val savedWithdraw = withdrawRepository.save(withdraw)

        val lock: RLock = walletLockComponent.getWalletLock(wallet.id!!)

        try {
            val isLocked: Boolean = lock.tryLock(4, 4, TimeUnit.SECONDS)

            if (!isLocked) {
                // 락 획득에 실패했으므로 예외 처리
                logger.error("fail to get wallet Lock withdraw : wallet-id = {}", wallet.id)
                savedWithdraw.state = TradeState.FAILED
                throw NotExpectResultException("fail to get wallet lock.")
            }

            val lastWallet = walletRepository.findById(wallet.id!!)
                .orElseThrow { ResourceNotFoundException("wallet does not exist") }

            lastWallet.balance -= savedWithdraw.amount
            walletRepository.save(lastWallet)

            savedWithdraw.state = TradeState.COMPLETED

            val walletTrace = WalletTrace(
                lastWallet,
                TradeType.WITHDRAW,
                savedWithdraw.id!!,
                createUser = withdrawRequestDto.requester
            )
            walletTraceRepository.save(walletTrace)

            return savedWithdraw;
        } catch (e: Exception) {
            // 쓰레드가 인터럽트 될 경우의 예외 처리
            savedWithdraw.state = TradeState.FAILED

            logger.error("fail to deposit with exception : wallet-id = {}, exception = {}", wallet.id, e.stackTrace)

            return savedWithdraw
        } finally {
            if (lock.isLocked && lock.isHeldByCurrentThread) {
                // 락 해제
                lock.unlock()
            }

            withdrawRepository.save(savedWithdraw)
        }
    }

    override fun transfer(transferRequest: TransferRequest): TransferResponse {
        val transfer = transferRepository.save(
            Transfer(
                transferRequest.penaltyId,
                transferRequest.amount,
                transferRequest.message ?: "이체",
                TradeState.REQUESTED,
                transferRequest.requester
            )
        )

        val withdrawRequestDto = WithdrawRequestDto(
            transferRequest.withdrawWalletOwnerId,
            transferRequest.withdrawWalletType,
            transfer.amount,
            transferRequest.requester,
            transfer.message,
            transfer
        )

        val withdraw = withdraw(withdrawRequestDto)

        if (TradeState.COMPLETED != withdraw.state) {
            logger.info("transfer request fail because withdraw state is not completed. withdraw id = {}", withdraw.id)
            transfer.state = TradeState.FAILED
            transferRepository.save(transfer)
            return TransferResponse(TradeState.COMPLETED == transfer.state, transfer.id)
        }

        val depositRequestDto = DepositRequestDto(
            transferRequest.depositWalletOwnerId,
            transferRequest.depositWalletType,
            transfer.amount,
            transferRequest.requester,
            transfer.message,
            transfer
        )

        val deposit = deposit(depositRequestDto)

        if (TradeState.COMPLETED != withdraw.state) {
            logger.info("transfer request fail because deposit state is not completed. deposit id = {}", deposit.id)
            transfer.state = TradeState.FAILED
            transferRepository.save(transfer)
            return TransferResponse(TradeState.COMPLETED == transfer.state, transfer.id)
        }

        transfer.state = TradeState.COMPLETED
        transferRepository.save(transfer)

        return TransferResponse(TradeState.COMPLETED == transfer.state, transfer.id)
    }

    override fun getWalletDetail(walletOwnerId: Int, walletOwnerType: WalletOwnerType): WalletDetailResponse {
        val wallet = getWalletRegisterIfNotExist(walletOwnerId, walletOwnerType, "getWalletLastAmount")

        val walletDetail = WalletDetailResponse(wallet.id!!, wallet.ownerId, wallet.ownerType, wallet.balance)

        return walletDetail
    }
}