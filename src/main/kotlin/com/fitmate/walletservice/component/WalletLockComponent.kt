package com.fitmate.walletservice.component

import org.redisson.api.RLock

interface WalletLockComponent {
    fun getWalletLock(userId: Int): RLock
}