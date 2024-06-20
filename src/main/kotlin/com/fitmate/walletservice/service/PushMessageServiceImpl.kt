package com.fitmate.walletservice.service

import com.fitmate.walletservice.common.AlarmServiceURI
import com.fitmate.walletservice.dto.PushMessageDto
import com.fitmate.walletservice.dto.PushMessageResponseDto
import com.fitmate.walletservice.exception.ResourceNotFoundException
import com.fitmate.walletservice.persistence.repository.TransferRepository
import com.fitmate.walletservice.persistence.repository.WithdrawRepository
import com.fitmate.walletservice.utils.SenderUtils
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PushMessageServiceImpl(
    private val withdrawRepository: WithdrawRepository,
    private val transferRepository: TransferRepository,
    private val senderUtils: SenderUtils
) : PushMessageService {

    @Transactional(readOnly = true)
    override fun pushTransferSuccessMessage(transferId: Long) {
        val transfer = transferRepository.findById(transferId)
            .orElseThrow { ResourceNotFoundException("pushTransferSuccessMessage Transfer does not exist") }

        val withdraw = withdrawRepository.findByTransferId(transfer)
            .orElseThrow { ResourceNotFoundException("pushTransferSuccessMessage Withdraw does not exist") }

        val withdrawUserId = withdraw.walletId.ownerId
        val withdrawAmount = withdraw.amount

        val message = "패널티로 $withdrawAmount 출금됐습니다."

        val pushMessageDto = PushMessageDto(withdrawUserId, message)

        val uriEndPoint = AlarmServiceURI.ALARM_PENALTY

        senderUtils.send(
            HttpMethod.GET,
            uriEndPoint,
            null,
            pushMessageDto,
            object : ParameterizedTypeReference<PushMessageResponseDto>() {
            })
    }
}