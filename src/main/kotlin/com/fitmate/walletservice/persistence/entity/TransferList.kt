package com.fitmate.walletservice.persistence.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
class TransferList(
    @Column(nullable = false) val penaltyId: Int,
    @Column(nullable = false) val amount: Int,
    @Column val message: String,
    walletState: WalletState,
    createUser: String
) : BaseListEntity(walletState, Instant.now(), createUser) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}