package com.fitmate.walletservice.component

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Component
class WalletLockComponentImpl(
    private val redissonClient: RedissonClient
) : WalletLockComponent {

    override fun getWalletLock(userId: Int): RLock {
        return redissonClient.getLock(userId.toString())
    }
}