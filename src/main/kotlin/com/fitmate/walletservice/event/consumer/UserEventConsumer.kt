package com.fitmate.walletservice.event.consumer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fitmate.walletservice.common.GlobalStatus
import com.fitmate.walletservice.dto.UserCreateMessageDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserEventConsumer(
    private val objectMapper: ObjectMapper
) {

    companion object {
        val logger: Logger? = LoggerFactory.getLogger(UserEventConsumer::class.java)
    }


    /**
     * kafka user create event listener inbound
     *
     * @param userId user id where an event occurred
     */
    @KafkaListener(
        topics = [GlobalStatus.KAFKA_TOPIC_USER_CREATE_EVENT],
        groupId = GlobalStatus.KAFKA_GROUP_ID
    )
    fun userCreateEvent(message: String) {
        logger?.info("KafkaListener userCreateEvent with userCreateEvent start - message = {}", message)

        val userCreateMessageDto: UserCreateMessageDto

        try {
            userCreateMessageDto = objectMapper.readValue(message, UserCreateMessageDto::class.java)
        } catch (e: JsonProcessingException) {
            logger?.error("JsonProcessingException on userCreateEvent ", e)

            throw e
        } catch (e: JsonMappingException) {
            logger?.error("JsonMappingException on userCreateEvent", e)

            throw e
        }

        //TODO user 생성시 생성 포인트 입금 필요
    }
}