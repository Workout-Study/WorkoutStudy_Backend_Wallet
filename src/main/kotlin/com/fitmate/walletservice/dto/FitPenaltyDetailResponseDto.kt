package com.fitmate.walletservice.dto

import com.fitmate.walletservice.utils.DateParseUtils
import java.time.Instant

data class FitPenaltyDetailResponseDto(
    val fitPenaltyId: Long,
    val fitGroupId: Long,
    val userId: Int,
    val amount: Int,
    private val createdAtInstant: Instant
) {
    val createdAt = DateParseUtils.instantToString(createdAtInstant)
}
