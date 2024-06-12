package com.fitmate.walletservice.service

import com.fitmate.walletservice.dto.DepositRequest
import com.fitmate.walletservice.dto.DepositResponse
import com.fitmate.walletservice.dto.WithdrawRequest
import com.fitmate.walletservice.dto.WithdrawResponse

interface WalletService {
    fun deposit(depositRequest: DepositRequest): DepositResponse
    fun withdraw(withdrawRequest: WithdrawRequest): WithdrawResponse
}