package com.fitmate.walletservice.persistence.entity

import jakarta.persistence.*
import java.time.Instant

@Entity(name = "transfer_list")
class Transfer(
    @Column(nullable = false) val penaltyId: Int,
    @Column(nullable = false) val amount: Int,
    @Column val message: String,
    tradeState: TradeState,
    createUser: String
) : BaseListEntity(tradeState, Instant.now(), createUser) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}