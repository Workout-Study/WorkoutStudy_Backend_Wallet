package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.persistence.entity.Withdraw
import org.springframework.data.jpa.repository.JpaRepository

interface WithdrawRepository : JpaRepository<Withdraw, Long> {
}