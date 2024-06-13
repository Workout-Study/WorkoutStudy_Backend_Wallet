package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.dto.WalletFilterRequest
import com.fitmate.walletservice.dto.WalletTradeHistoryResponseDto
import org.springframework.data.domain.PageRequest

interface WalletHistoryRepository {
    fun getWalletHistory(
        walletFilterRequest: WalletFilterRequest,
        pageable: PageRequest
    ): List<WalletTradeHistoryResponseDto>
}