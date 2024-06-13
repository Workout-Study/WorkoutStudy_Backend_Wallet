package com.fitmate.walletservice.service

import com.fitmate.walletservice.dto.DepositRequestDto
import com.fitmate.walletservice.dto.TransferRequest
import com.fitmate.walletservice.dto.TransferResponse
import com.fitmate.walletservice.dto.WithdrawRequestDto
import com.fitmate.walletservice.persistence.entity.Deposit
import com.fitmate.walletservice.persistence.entity.Withdraw

interface WalletService {
    fun deposit(depositRequestDto: DepositRequestDto): Deposit
    fun withdraw(withdrawRequestDto: WithdrawRequestDto): Withdraw
    fun transfer(transferRequest: TransferRequest): TransferResponse
}