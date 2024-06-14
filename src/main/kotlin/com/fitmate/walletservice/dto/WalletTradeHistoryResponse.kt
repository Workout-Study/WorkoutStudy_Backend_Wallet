package com.fitmate.walletservice.dto

data class WalletTradeHistoryResponse(
    val pageNumber: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val walletBalance: Int,
    val content: List<WalletTradeHistoryResponseDto>
)
