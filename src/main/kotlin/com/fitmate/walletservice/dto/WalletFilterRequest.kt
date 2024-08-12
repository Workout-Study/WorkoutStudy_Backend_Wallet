package com.fitmate.walletservice.dto

import com.fitmate.walletservice.persistence.entity.TradeType
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import com.fitmate.walletservice.utils.DateParseUtils
import org.springframework.util.StringUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

data class WalletFilterRequest(
    val walletOwnerId: Int,
    val walletOwnerType: WalletOwnerType,
    private val historyStartDate: String? = null,
    private val historyEndDate: String? = null,
    val tradeType: TradeType? = null,
    val pageNumber: Int = 0,
    val pageSize: Int = 5
) {
    val historyStartDateInstant: Instant =
        if (StringUtils.hasText(historyStartDate)) DateParseUtils.stringToInstant(historyStartDate!!)
        else LocalDate.now().withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC)

    val historyEndDateInstant: Instant =
        if (StringUtils.hasText(historyEndDate)) DateParseUtils.stringToInstant(historyEndDate!!)
        else LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
            .atStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59).toInstant(ZoneOffset.UTC)
}
