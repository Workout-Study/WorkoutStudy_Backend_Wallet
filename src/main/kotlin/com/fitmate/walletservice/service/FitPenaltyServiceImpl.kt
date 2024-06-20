package com.fitmate.walletservice.service

import com.fitmate.walletservice.common.BatchServiceURI
import com.fitmate.walletservice.dto.FitPenaltyDetailResponseDto
import com.fitmate.walletservice.dto.TransferRequest
import com.fitmate.walletservice.event.event.TransferSuccessEvent
import com.fitmate.walletservice.exception.NotExpectResultException
import com.fitmate.walletservice.module.WalletModuleServiceImpl
import com.fitmate.walletservice.persistence.entity.TradeState
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import com.fitmate.walletservice.persistence.repository.TransferRepository
import com.fitmate.walletservice.utils.SenderUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FitPenaltyServiceImpl(
    private val transferRepository: TransferRepository,
    private val senderUtils: SenderUtils,
    private val walletModuleServiceImpl: WalletModuleServiceImpl,
    private val applicationEventPublisher: ApplicationEventPublisher
) : FitPenaltyService {

    @Value("\${user.penalty.message}")
    private val penaltyMessage = ""

    @Transactional
    override fun transferPenalty(fitPenaltyId: Long) {
        val transferOpt = transferRepository.findByPenaltyIdAndState(fitPenaltyId, TradeState.COMPLETED)

        if (transferOpt.isPresent) return

        val uriEndPoint = "${BatchServiceURI.FIT_PENALTY_ROOT}/${fitPenaltyId}"

        val response: ResponseEntity<FitPenaltyDetailResponseDto> =
            senderUtils.send(
                HttpMethod.GET,
                uriEndPoint,
                null,
                null,
                object : ParameterizedTypeReference<FitPenaltyDetailResponseDto>() {
                })

        val penaltyDetail = response.body ?: throw NotExpectResultException("penalty detail response is null")

        val transferRequest = TransferRequest(
            penaltyDetail.userId,
            WalletOwnerType.USER,
            penaltyDetail.fitGroupId.toInt(),
            WalletOwnerType.GROUP,
            penaltyDetail.amount,
            penaltyMessage,
            fitPenaltyId,
            "penalty event"
        )

        val transferResponse = walletModuleServiceImpl.transfer(transferRequest)

        if (transferResponse.isTransferSuccess)
            applicationEventPublisher.publishEvent(TransferSuccessEvent(transferResponse.transferId!!))
    }
}