package com.fitmate.walletservice.dto

data class WithdrawRequest(
    val requestUserId: Int,
    val amount: Int
)
