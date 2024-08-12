package com.fitmate.walletservice.dto

import com.fitmate.walletservice.utils.DateParseUtils
import org.springframework.util.StringUtils
import java.time.Instant

data class UserInfoResponse(
    val userId: Int,
    val nickname: String,
    val state: Boolean,
    private val createdAt: String,
    private val updatedAt: String?
) {
    val createdAtInstant = DateParseUtils.stringToInstant(createdAt)
    val updatedAtInstant: Instant? =
        if (StringUtils.hasText(updatedAt)) DateParseUtils.stringToInstant(updatedAt!!) else null
}
