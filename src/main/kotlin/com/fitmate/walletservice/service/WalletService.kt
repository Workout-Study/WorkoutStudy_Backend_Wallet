package com.fitmate.walletservice.service

import com.fitmate.walletservice.dto.*
import com.fitmate.walletservice.persistence.entity.Deposit
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import com.fitmate.walletservice.persistence.entity.Withdraw

interface WalletService {
    fun deposit(depositRequestDto: DepositRequestDto): Deposit
    fun withdraw(withdrawRequestDto: WithdrawRequestDto): Withdraw
    fun transfer(transferRequest: TransferRequest): TransferResponse
    fun getWalletDetail(walletOwnerId: Int, walletOwnerType: WalletOwnerType): WalletDetailResponse
}