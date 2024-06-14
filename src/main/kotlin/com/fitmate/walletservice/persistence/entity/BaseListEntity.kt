package com.fitmate.walletservice.persistence.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
open class BaseListEntity(
    @Enumerated(EnumType.STRING)
    var state: TradeState,
    @CreatedDate
    @Column(updatable = false)
    @Convert(converter = Jsr310JpaConverters.InstantConverter::class)
    var createdAt: Instant,
    @Column(nullable = false) var createUser: String,
    @LastModifiedDate
    @Convert(converter = Jsr310JpaConverters.InstantConverter::class)
    var updatedAt: Instant? = null,
    @Column
    var updateUser: String? = null
)