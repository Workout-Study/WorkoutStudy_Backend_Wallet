package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.persistence.entity.Deposit
import org.springframework.data.jpa.repository.JpaRepository

interface DepositRepository : JpaRepository<Deposit, Long> {
}