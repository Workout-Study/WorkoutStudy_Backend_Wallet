package com.fitmate.walletservice.event.listener

import com.fitmate.walletservice.event.event.DepositSuccessEvent
import com.fitmate.walletservice.event.event.TransferSuccessEvent
import com.fitmate.walletservice.service.PushMessageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DepositSuccessEventListener(
    private val pushMessageService: PushMessageService
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DepositSuccessEventListener::class.java)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun depositSuccessEvent(depositSuccessEvent: DepositSuccessEvent) {
        logger.info(
            "DepositSuccessEvent with depositSuccessEvent start - deposit id = {}",
            depositSuccessEvent.depositId
        )
        
        pushMessageService.pushDepositSuccessMessage(depositSuccessEvent.depositId)
    }
}