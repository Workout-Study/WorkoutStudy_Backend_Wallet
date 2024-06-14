package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.persistence.entity.Deposit
import com.fitmate.walletservice.persistence.entity.TradeState
import com.fitmate.walletservice.persistence.entity.Wallet
import org.springframework.data.jpa.repository.JpaRepository

interface DepositRepository : JpaRepository<Deposit, Long> {
    fun findByWalletAndState(wallet: Wallet, tradeState: TradeState): List<Deposit>
}