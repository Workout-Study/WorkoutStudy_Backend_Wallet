package com.fitmate.walletservice.exception

class BadRequestException(override val message: String) : RuntimeException(message) {
}