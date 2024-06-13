package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.persistence.entity.WalletTrace
import org.springframework.data.jpa.repository.JpaRepository

interface WalletTraceRepository : JpaRepository<WalletTrace, Long> {
}