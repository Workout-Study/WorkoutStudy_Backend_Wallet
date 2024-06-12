package com.fitmate.walletservice.component

import org.redisson.api.RLock

interface WalletLockComponent {
    fun getWalletLock(walletId: Long): RLock
}