package com.fitmate.walletservice.common

class GlobalURI {

    companion object {
        const val ROOT_URI = "/wallet-service"

        const val WALLET_ROOT = "$ROOT_URI/wallets"
        const val DEPOSIT_URI = "$WALLET_ROOT/deposits"
        const val WITHDRAW_URI = "$WALLET_ROOT/withdraws"
        const val WALLET_FILTER = "$WALLET_ROOT/filters"

        const val PATH_VARIABLE_WALLET_OWNER_ID = "wallet-owner-id"
        const val PATH_VARIABLE_WALLET_OWNER_ID_WITH_BRACE = "/{$PATH_VARIABLE_WALLET_OWNER_ID}"

        const val PATH_VARIABLE_WALLET_OWNER_TYPE = "wallet-owner-type"
        const val PATH_VARIABLE_WALLET_OWNER_TYPE_WITH_BRACE = "/{$PATH_VARIABLE_WALLET_OWNER_TYPE}"
    }
}