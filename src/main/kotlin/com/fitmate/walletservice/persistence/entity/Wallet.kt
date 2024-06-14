package com.fitmate.walletservice.persistence.entity

import com.fitmate.walletservice.common.GlobalStatus
import jakarta.persistence.*
import java.time.Instant

@Entity
class Wallet(
    val ownerId: Int,
    @Enumerated(EnumType.STRING) val ownerType: WalletOwnerType,
    createUser: String,
    @Column var balance: Int = 0,
    state: Boolean = GlobalStatus.PERSISTENCE_NOT_DELETED
) : BaseEntity(state, Instant.now(), createUser) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}