package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.persistence.entity.UserForRead
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserForReadRepository : JpaRepository<UserForRead, Long> {
    fun findByUserIdAndState(userId: Int, state: Boolean): Optional<UserForRead>

    fun findByUserId(userId: Int): Optional<UserForRead>
}