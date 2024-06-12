package com.fitmate.walletservice.common

class GlobalStatus {

    companion object {
        const val SPRING_PROFILES_ACTIVE = "spring.profiles.active"
        const val SPRING_PROFILES_ACTIVE_DEFAULT = "local"

        const val PERSISTENCE_NOT_DELETED: Boolean = false
        const val PERSISTENCE_DELETED: Boolean = true
    }
}