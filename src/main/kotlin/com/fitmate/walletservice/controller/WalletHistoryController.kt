package com.fitmate.walletservice.controller

import com.fitmate.walletservice.common.GlobalURI
import com.fitmate.walletservice.dto.WalletFilterRequest
import com.fitmate.walletservice.dto.WalletTradeHistoryResponse
import com.fitmate.walletservice.service.WalletHistoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
class WalletHistoryController(
    private val walletHistoryService: WalletHistoryService
) {

    @GetMapping(GlobalURI.WALLET_FILTER)
    fun getWalletHistory(@ModelAttribute walletFilterRequest: WalletFilterRequest): ResponseEntity<WalletTradeHistoryResponse> {
        return ResponseEntity.ok().body(walletHistoryService.getWalletHistory(walletFilterRequest))
    }
}