package com.fitmate.walletservice.dto

import com.fitmate.walletservice.persistence.entity.TradeType
import java.time.Instant

data class WalletTradeHistoryResponseDto(
    val tradeType: TradeType,
    val amount: Int,
    val message: String,
    val userId: Int?,
    val userNickname: String?,
    val createdAt: Instant
)
