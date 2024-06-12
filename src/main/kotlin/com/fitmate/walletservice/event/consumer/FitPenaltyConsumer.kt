package com.fitmate.walletservice.event.consumer

import com.fitmate.walletservice.common.GlobalStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class FitPenaltyConsumer(
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

        //TODO 패널티 생성시 포인트 입출금 로직 필요
    }
}