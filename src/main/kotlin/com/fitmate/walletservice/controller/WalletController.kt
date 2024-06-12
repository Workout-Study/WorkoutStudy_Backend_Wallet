package com.fitmate.walletservice.controller

import com.fitmate.walletservice.common.GlobalURI
import com.fitmate.walletservice.dto.DepositRequest
import com.fitmate.walletservice.dto.DepositResponse
import com.fitmate.walletservice.service.WalletService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WalletController(
    private val walletService: WalletService
) {

    @PostMapping(GlobalURI.DEPOSIT_URI)
    fun deposit(@RequestBody @Valid depositRequest: DepositRequest): ResponseEntity<DepositResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.deposit(depositRequest))
    }
}