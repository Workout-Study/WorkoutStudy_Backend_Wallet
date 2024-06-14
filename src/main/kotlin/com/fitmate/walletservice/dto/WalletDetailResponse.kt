package com.fitmate.walletservice.dto

import com.fitmate.walletservice.persistence.entity.WalletOwnerType

data class WalletDetailResponse(
    val walletId: Long,
    val walletOwnerId: Int,
    val walletOwnerType: WalletOwnerType,
    val balance: Int
)
