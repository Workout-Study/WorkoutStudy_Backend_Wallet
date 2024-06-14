package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.persistence.entity.Wallet
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WalletRepository : JpaRepository<Wallet, Long> {

    fun findByOwnerIdAndOwnerTypeAndState(ownerId: Int, ownerType: WalletOwnerType, state: Boolean): Optional<Wallet>
}