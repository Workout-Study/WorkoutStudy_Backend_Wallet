package com.fitmate.walletservice.service

import com.fitmate.walletservice.common.UserServiceURI
import com.fitmate.walletservice.dto.DepositRequestDto
import com.fitmate.walletservice.dto.UserCreateMessageDto
import com.fitmate.walletservice.dto.UserInfoResponse
import com.fitmate.walletservice.exception.NotExpectResultException
import com.fitmate.walletservice.module.WalletModuleService
import com.fitmate.walletservice.persistence.entity.Deposit
import com.fitmate.walletservice.persistence.entity.TradeState
import com.fitmate.walletservice.persistence.entity.UserForRead
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import com.fitmate.walletservice.persistence.repository.DepositRepository
import com.fitmate.walletservice.persistence.repository.UserForReadRepository
import com.fitmate.walletservice.utils.SenderUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val senderUtils: SenderUtils,
    private val userForReadRepository: UserForReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val depositRepository: DepositRepository,
    private val walletModuleService: WalletModuleService
) : UserService {

    @Value("\${user.create.point}")
    private val createPoint = 0

    @Value("\${user.create.message}")
    private val createMessage = ""

    @Transactional
    override fun saveUser(userIdInt: Int, eventPublisher: String) {
        val uriEndPoint = "${UserServiceURI.USER_INFO}?userId=${userIdInt}"

        val response: ResponseEntity<UserInfoResponse> =
            senderUtils.send(
                HttpMethod.GET,
                uriEndPoint,
                null,
                null,
                object : ParameterizedTypeReference<UserInfoResponse>() {
                })

        val userInfoResponse: UserInfoResponse = response.body ?: throw NotExpectResultException("user info is null")

        val userForRead =
            userForReadRepository.findByUserId(userInfoResponse.userId)
                .orElseGet { UserForRead(userInfoResponse.userId, userInfoResponse.nickname, eventPublisher) }

        userForRead.updateByResponse(userInfoResponse, eventPublisher)

        userForReadRepository.save(userForRead)
    }

    @Transactional
    override fun createUser(userCreateMessageDto: UserCreateMessageDto, eventPublisher: String) {
        val userForRead =
            userForReadRepository.findByUserId(userCreateMessageDto.userId)
                .orElse(UserForRead(userCreateMessageDto.userId, userCreateMessageDto.nickname, eventPublisher))

        userForRead.updateByUserMessageDto(userCreateMessageDto, eventPublisher)

        userForReadRepository.save(userForRead)
        createUserDefaultPoint(userForRead.userId)
    }

    @Transactional
    override fun createUserDefaultPoint(userId: Int) {
        val wallet = walletModuleService.getWalletRegisterIfNotExist(userId, WalletOwnerType.USER, "user create event")

        val depositList: List<Deposit> = depositRepository.findByWalletAndState(wallet, TradeState.COMPLETED)
        if (depositList.isNotEmpty()) return

        val depositRequestDto = DepositRequestDto(
            wallet.ownerId,
            wallet.ownerType,
            createPoint,
            "User Create Event",
            createMessage
        )

        walletModuleService.deposit(depositRequestDto)
    }
}