package com.fitmate.walletservice.dto

import com.fitmate.walletservice.persistence.entity.WalletOwnerType

data class WithdrawRequest(
    val walletOwnerId: Int,
    val walletOwnerType: WalletOwnerType,
    val amount: Int,
    val requester: String,
    val message: String? = null
)
