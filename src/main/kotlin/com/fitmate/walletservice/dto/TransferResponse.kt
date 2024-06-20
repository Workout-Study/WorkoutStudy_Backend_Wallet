package com.fitmate.walletservice.dto

data class TransferResponse(
    val isTransferSuccess: Boolean,
    val transferId: Long?
)
