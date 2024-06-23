package com.fitmate.walletservice.service

interface PushMessageService {
    fun pushTransferSuccessMessage(transferId: Long)

    fun pushDepositSuccessMessage(depositId: Long)
}