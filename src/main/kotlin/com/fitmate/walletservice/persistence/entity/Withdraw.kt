package com.fitmate.walletservice.persistence.entity

import jakarta.persistence.*
import java.time.Instant

@Entity(name = "withdraw_list")
class Withdraw(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    val walletId: Wallet,
    @Column(nullable = false) val amount: Int,
    @Column val message: String,
    tradeState: TradeState,
    createUser: String,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id", nullable = true)
    val transferId: Transfer? = null
) : BaseListEntity(tradeState, Instant.now(), createUser) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}