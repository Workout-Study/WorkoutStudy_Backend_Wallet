package com.fitmate.walletservice.exception

class ResourceAlreadyExistException(override val message: String) : RuntimeException(message) {
}