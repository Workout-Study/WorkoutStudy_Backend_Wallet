package com.fitmate.walletservice.dto

import com.fitmate.walletservice.persistence.entity.WalletOwnerType

data class TransferRequest(
    val withdrawWalletOwnerId: Int,
    val withdrawWalletType: WalletOwnerType,
    val depositWalletOwnerId: Int,
    val depositWalletType: WalletOwnerType,
    val amount: Int,
    val message: String? = null,
    val penaltyId: Long,
    val requester: String
)
