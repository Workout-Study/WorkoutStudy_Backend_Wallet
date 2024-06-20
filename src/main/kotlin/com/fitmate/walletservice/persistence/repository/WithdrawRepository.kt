package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.persistence.entity.Transfer
import com.fitmate.walletservice.persistence.entity.Withdraw
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WithdrawRepository : JpaRepository<Withdraw, Long> {
    fun findByTransferId(transfer: Transfer): Optional<Withdraw>
}