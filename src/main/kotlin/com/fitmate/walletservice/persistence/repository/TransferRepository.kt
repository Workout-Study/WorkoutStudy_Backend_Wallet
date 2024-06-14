package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.persistence.entity.TradeState
import com.fitmate.walletservice.persistence.entity.Transfer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TransferRepository : JpaRepository<Transfer, Long> {
    fun findByPenaltyIdAndState(fitPenaltyId: Long, tradeState: TradeState): Optional<Transfer>
}