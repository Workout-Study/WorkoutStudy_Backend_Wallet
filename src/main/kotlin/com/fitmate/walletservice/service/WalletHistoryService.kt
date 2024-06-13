package com.fitmate.walletservice.service

import com.fitmate.walletservice.dto.WalletFilterRequest
import com.fitmate.walletservice.dto.WalletTradeHistoryResponse

interface WalletHistoryService {
    fun getWalletHistory(walletFilterRequest: WalletFilterRequest): WalletTradeHistoryResponse
}