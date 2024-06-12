package com.fitmate.walletservice.persistence.entity

import jakarta.persistence.*
import java.time.Instant

@Entity(name = "deposit_list")
class Deposit(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    val wallet: Wallet,
    @Column(nullable = false) val amount: Int,
    @Column val message: String,
    walletState: WalletState,
    createUser: String,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id", nullable = true)
    val transferId: Transfer? = null
) : BaseListEntity(walletState, Instant.now(), createUser) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}