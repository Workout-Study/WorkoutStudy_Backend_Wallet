package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.persistence.entity.Transfer
import org.springframework.data.jpa.repository.JpaRepository

interface TransferRepository : JpaRepository<Transfer, Long> {
}