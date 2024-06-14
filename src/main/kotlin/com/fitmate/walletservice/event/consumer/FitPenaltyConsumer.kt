package com.fitmate.walletservice.event.consumer

import com.fitmate.walletservice.common.GlobalStatus
import com.fitmate.walletservice.service.FitPenaltyService
import org.apache.coyote.BadRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class FitPenaltyConsumer(
    private val fitPenaltyService: FitPenaltyService
) {

    companion object {
        val logger: Logger? = LoggerFactory.getLogger(FitPenaltyConsumer::class.java)
    }

    /**
     * kafka fit penalty event listener inbound
     *
     * @param fitPenaltyId fit penalty id where an event occurred
     */
    @KafkaListener(topics = [GlobalStatus.KAFKA_TOPIC_FIT_PENALTY], groupId = GlobalStatus.KAFKA_GROUP_ID)
    fun fitPenaltyListener(fitPenaltyId: String) {
        logger?.info(
            "KafkaListener FitPenaltyEvent with fitPenaltyListener start - fit penalty id = {}",
            fitPenaltyId
        )

        val fitPenaltyIdLong: Long

        try {
            fitPenaltyIdLong = fitPenaltyId.toLong()
        } catch (exception: Exception) {
            throw BadRequestException("fit penalty id must be long")
        }

        fitPenaltyService.transferPenalty(fitPenaltyIdLong)
    }
}