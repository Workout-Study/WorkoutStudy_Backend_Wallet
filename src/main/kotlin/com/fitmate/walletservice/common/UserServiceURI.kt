package com.fitmate.walletservice.common

class UserServiceURI {

    companion object {
        const val ROOT_URI = "http://auth-service:8084"

        const val USER_ROOT = "$ROOT_URI/user"

        const val USER_INFO = "$USER_ROOT/user-info"
    }
}