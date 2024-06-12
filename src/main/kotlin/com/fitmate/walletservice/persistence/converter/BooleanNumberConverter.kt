package com.fitmate.walletservice.persistence.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class BooleanNumberConverter : AttributeConverter<Boolean, Int> {

    /**
     * Boolean 값을 1 또는 0 으로 컨버트
     *
     * @param attribute boolean 값
     * @return Int true 인 경우 1 또는 false 인 경우 0
     */
    override fun convertToDatabaseColumn(attribute: Boolean?): Int {
        return if (attribute != null && attribute) 1 else 0
    }

    /**
     * 1 또는 0 을 Boolean 으로 컨버트
     *
     * @param yn 1 또는 0
     * @return Boolean 1 인 경우 true, 0 인 경우 false
     */
    override fun convertToEntityAttribute(yn: Int?): Boolean {
        return yn == 1
    }
}