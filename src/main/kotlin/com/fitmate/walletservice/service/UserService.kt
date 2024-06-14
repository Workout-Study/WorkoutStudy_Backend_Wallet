package com.fitmate.walletservice.service

import com.fitmate.walletservice.dto.UserCreateMessageDto

interface UserService {
    fun saveUser(userIdInt: Int, eventPublisher: String)
    fun createUser(userCreateMessageDto: UserCreateMessageDto, eventPublisher: String)
    fun createUserDefaultPoint(userId: Int)
}