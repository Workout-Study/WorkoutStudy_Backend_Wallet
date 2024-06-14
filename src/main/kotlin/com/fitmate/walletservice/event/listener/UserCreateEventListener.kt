package com.fitmate.walletservice.event.listener

import com.fitmate.walletservice.event.event.UserCreateEvent
import com.fitmate.walletservice.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserCreateEventListener(
    private val userService: UserService
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(UserCreateEventListener::class.java)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun createUserDefaultPoint(userCreateEvent: UserCreateEvent) {
        logger.info(
            "CreateUserEvent with createUserDefaultPoint start - user id = {}",
            userCreateEvent.userId
        )
        userService.createUserDefaultPoint(userCreateEvent.userId)
    }
}