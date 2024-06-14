package com.fitmate.walletservice.dto

import com.fitmate.walletservice.persistence.entity.TradeType
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

data class WalletFilterRequest(
    val walletOwnerId: Int,
    val walletOwnerType: WalletOwnerType,
    val historyStartDate: Instant =
        LocalDate.now().withDayOfMonth(1)
            .atStartOfDay().toInstant(ZoneOffset.UTC),
    val historyEndDate: Instant =
        LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
            .atStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59).toInstant(ZoneOffset.UTC),
    val tradeType: TradeType? = null,
    val pageNumber: Int = 0,
    val pageSize: Int = 5
)
