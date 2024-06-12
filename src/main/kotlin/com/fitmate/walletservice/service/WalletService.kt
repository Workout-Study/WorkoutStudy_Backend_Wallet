package com.fitmate.walletservice.service

import com.fitmate.walletservice.dto.DepositRequest
import com.fitmate.walletservice.dto.DepositResponse

interface WalletService {
    fun deposit(depositRequest: DepositRequest): DepositResponse
}