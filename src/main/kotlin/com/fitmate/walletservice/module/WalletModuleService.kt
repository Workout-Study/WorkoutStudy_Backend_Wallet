package com.fitmate.walletservice.module

import com.fitmate.walletservice.dto.*
import com.fitmate.walletservice.persistence.entity.Deposit
import com.fitmate.walletservice.persistence.entity.Wallet
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import com.fitmate.walletservice.persistence.entity.Withdraw

interface WalletModuleService {
    fun deposit(depositRequestDto: DepositRequestDto): Deposit
    fun withdraw(withdrawRequestDto: WithdrawRequestDto): Withdraw
    fun transfer(transferRequest: TransferRequest): TransferResponse
    fun getWalletDetail(walletOwnerId: Int, walletOwnerType: WalletOwnerType): WalletDetailResponse
    fun getWalletRegisterIfNotExist(
        walletOwnerId: Int,
        walletOwnerType: WalletOwnerType,
        requester: String
    ): Wallet
}