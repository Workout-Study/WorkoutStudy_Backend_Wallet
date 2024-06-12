package com.fitmate.walletservice.persistence.entity

import com.fitmate.walletservice.common.GlobalStatus
import jakarta.persistence.*
import java.time.Instant

@Entity
class WalletTrace(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    val walletId: Wallet,
    @Enumerated(EnumType.STRING) val transferType: TransferType,
    state: Boolean = GlobalStatus.PERSISTENCE_NOT_DELETED,
    createUser: String
) : BaseEntity(state, Instant.now(), createUser) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}