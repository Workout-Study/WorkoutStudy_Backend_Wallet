package com.fitmate.walletservice.exception

class ResourceNotFoundException(override val message: String) : RuntimeException(message) {
}