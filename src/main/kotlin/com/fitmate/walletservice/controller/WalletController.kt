package com.fitmate.walletservice.controller

import com.fitmate.walletservice.common.GlobalURI
import com.fitmate.walletservice.converter.WalletDtoConverter
import com.fitmate.walletservice.dto.*
import com.fitmate.walletservice.module.WalletModuleService
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class WalletController(
    private val walletModuleService: WalletModuleService
) {

    @PostMapping(GlobalURI.DEPOSIT_URI)
    fun deposit(@RequestBody @Valid depositRequest: DepositRequest): ResponseEntity<DepositResponse> {
        val depositRequestDto = WalletDtoConverter.depositRequestToDto(depositRequest)
        val deposit = walletModuleService.deposit(depositRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(WalletDtoConverter.depositToResponse(deposit))
    }

    @PostMapping(GlobalURI.WITHDRAW_URI)
    fun withdraw(@RequestBody @Valid withdrawRequest: WithdrawRequest): ResponseEntity<WithdrawResponse> {
        val withdrawRequestDto = WalletDtoConverter.withdrawRequestToDto(withdrawRequest)
        val withdraw = walletModuleService.withdraw(withdrawRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(WalletDtoConverter.withdrawToResponse(withdraw))
    }

    @GetMapping(
        GlobalURI.WALLET_ROOT
                + GlobalURI.PATH_VARIABLE_WALLET_OWNER_ID_WITH_BRACE
                + GlobalURI.PATH_VARIABLE_WALLET_OWNER_TYPE_WITH_BRACE
    )
    fun getWalletDetail(
        @PathVariable(GlobalURI.PATH_VARIABLE_WALLET_OWNER_ID) walletOwnerId: Int,
        @PathVariable(GlobalURI.PATH_VARIABLE_WALLET_OWNER_TYPE) walletOwnerType: WalletOwnerType
    ): ResponseEntity<WalletDetailResponse> {
        return ResponseEntity.ok().body(walletModuleService.getWalletDetail(walletOwnerId, walletOwnerType))
    }
}