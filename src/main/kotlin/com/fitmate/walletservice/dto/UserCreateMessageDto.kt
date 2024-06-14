package com.fitmate.walletservice.dto

data class UserCreateMessageDto(
    val id: Int,
    val nickname: String,
    val state: Boolean,
    val createdAt: String,
    val updatedAt: String
)
