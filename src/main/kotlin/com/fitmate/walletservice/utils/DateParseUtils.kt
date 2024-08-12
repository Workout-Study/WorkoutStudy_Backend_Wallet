package com.fitmate.walletservice.utils

import com.fitmate.walletservice.exception.NotExpectResultException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.DateTimeException
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateParseUtils {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DateParseUtils::class.java)

        const val DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSSXXX"

        fun instantToString(instant: Instant?): String {
            instant ?: return ""

            val zonedDateTime = instant.atZone(ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT)

            val formattedDate = zonedDateTime.format(formatter)

            return formattedDate
        }

        fun stringToInstant(stringInstantDate: String): Instant {
            val formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT)

            var result: Instant? = null

            try {
                val offsetDateTime = OffsetDateTime.parse(stringInstantDate, formatter)

                result = offsetDateTime.toInstant()
            } catch (e: DateTimeException) {
                logger.error("string to instant parse exception ", e)
            }

            if (result == null) throw NotExpectResultException("string to instant parse result null")

            return result
        }
    }
}