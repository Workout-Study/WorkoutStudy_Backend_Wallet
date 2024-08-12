package com.fitmate.walletservice.dto

import com.fitmate.walletservice.persistence.entity.TradeType
import com.fitmate.walletservice.utils.DateParseUtils
import java.time.Instant

data class WalletTradeHistoryResponseDto(
    val tradeType: TradeType,
    val amount: Int,
    val message: String,
    val depositUserId: Int?,
    val depositUserNickname: String?,
    private val createdAtInstant: Instant
) {
    val createdAt = DateParseUtils.instantToString(createdAtInstant)
}
