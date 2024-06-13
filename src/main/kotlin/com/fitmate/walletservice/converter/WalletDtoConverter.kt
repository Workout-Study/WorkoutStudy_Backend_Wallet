package com.fitmate.walletservice.converter

import com.fitmate.walletservice.dto.*
import com.fitmate.walletservice.persistence.entity.Deposit
import com.fitmate.walletservice.persistence.entity.TradeState
import com.fitmate.walletservice.persistence.entity.Withdraw

class WalletDtoConverter {

    companion object {
        fun depositRequestToDto(depositRequest: DepositRequest): DepositRequestDto =
            DepositRequestDto(
                depositRequest.walletOwnerId,
                depositRequest.walletOwnerType,
                depositRequest.amount,
                depositRequest.requester,
                depositRequest.message
            )

        fun depositToResponse(deposit: Deposit): DepositResponse =
            DepositResponse(deposit.state == TradeState.COMPLETED)

        fun withdrawRequestToDto(withdrawRequest: WithdrawRequest): WithdrawRequestDto =
            WithdrawRequestDto(
                withdrawRequest.walletOwnerId,
                withdrawRequest.walletOwnerType,
                withdrawRequest.amount,
                withdrawRequest.requester,
                withdrawRequest.message
            )

        fun withdrawToResponse(withdraw: Withdraw): WithdrawResponse =
            WithdrawResponse(withdraw.state == TradeState.COMPLETED)
    }
}