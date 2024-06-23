package com.fitmate.walletservice.common

class AlarmServiceURI {

    companion object {
        const val ALARM_ROOT_URI = "http://alarm-service:8080"

        const val ALARM_WALLET = "$ALARM_ROOT_URI/wallet"

        const val ALARM_PENALTY = "$ALARM_WALLET/penalty-complete"
    }
}