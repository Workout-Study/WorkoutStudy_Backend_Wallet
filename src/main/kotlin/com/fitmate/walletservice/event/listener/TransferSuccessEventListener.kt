package com.fitmate.walletservice.event.listener

import com.fitmate.walletservice.event.event.TransferSuccessEvent
import com.fitmate.walletservice.service.PushMessageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class TransferSuccessEventListener(
    private val pushMessageService: PushMessageService
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(TransferSuccessEventListener::class.java)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun transferSuccessEvent(transferSuccessEvent: TransferSuccessEvent) {
        logger.info(
            "TransferSuccessEvent with transferSuccessEvent start - transfer id = {}",
            transferSuccessEvent.transferId
        )
        
        pushMessageService.pushTransferSuccessMessage(transferSuccessEvent.transferId)
    }
}