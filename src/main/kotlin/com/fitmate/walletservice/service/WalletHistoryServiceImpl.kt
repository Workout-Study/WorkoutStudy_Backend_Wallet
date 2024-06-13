package com.fitmate.walletservice.service

import com.fitmate.walletservice.common.GlobalStatus
import com.fitmate.walletservice.dto.WalletFilterRequest
import com.fitmate.walletservice.dto.WalletTradeHistoryResponse
import com.fitmate.walletservice.dto.WalletTradeHistoryResponseDto
import com.fitmate.walletservice.persistence.repository.WalletHistoryRepository
import com.fitmate.walletservice.persistence.repository.WalletRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WalletHistoryServiceImpl(
    private val walletHistoryRepository: WalletHistoryRepository,
    private val walletRepository: WalletRepository
) : WalletHistoryService {

    @Transactional(readOnly = true)
    override fun getWalletHistory(walletFilterRequest: WalletFilterRequest): WalletTradeHistoryResponse {
        val walletOpt = walletRepository.findByOwnerIdAndOwnerTypeAndState(
            walletFilterRequest.walletOwnerId,
            walletFilterRequest.walletOwnerType,
            GlobalStatus.PERSISTENCE_NOT_DELETED
        )

        val pageable = PageRequest.of(walletFilterRequest.pageNumber, walletFilterRequest.pageSize)

        if (walletOpt.isEmpty) return WalletTradeHistoryResponse(
            pageable.pageNumber,
            pageable.pageSize,
            false,
            0,
            emptyList()
        )

        val wallet = walletOpt.get()

        var walletHistoryList: List<WalletTradeHistoryResponseDto> =
            walletHistoryRepository.getWalletHistory(walletFilterRequest, pageable)

        val hasNext: Boolean = walletHistoryList.size > walletFilterRequest.pageSize
        if (hasNext) walletHistoryList = walletHistoryList.dropLast(1)

        return WalletTradeHistoryResponse(
            walletFilterRequest.pageNumber,
            walletFilterRequest.pageSize,
            hasNext,
            wallet.balance,
            walletHistoryList
        )
    }
}