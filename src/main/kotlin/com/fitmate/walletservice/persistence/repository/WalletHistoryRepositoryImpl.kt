package com.fitmate.walletservice.persistence.repository

import com.fitmate.walletservice.dto.WalletFilterRequest
import com.fitmate.walletservice.dto.WalletTradeHistoryResponseDto
import com.fitmate.walletservice.persistence.entity.QDeposit.deposit
import com.fitmate.walletservice.persistence.entity.QTransfer.transfer
import com.fitmate.walletservice.persistence.entity.QUserForRead.userForRead
import com.fitmate.walletservice.persistence.entity.QWallet.wallet
import com.fitmate.walletservice.persistence.entity.QWalletTrace.walletTrace
import com.fitmate.walletservice.persistence.entity.QWithdraw
import com.fitmate.walletservice.persistence.entity.QWithdraw.withdraw
import com.fitmate.walletservice.persistence.entity.TradeType
import com.fitmate.walletservice.persistence.entity.Wallet
import com.fitmate.walletservice.utils.SliceUtil
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class WalletHistoryRepositoryImpl(jpaQueryFactory: JPAQueryFactory) :
    QuerydslRepositorySupport(Wallet::class.java), WalletHistoryRepository {

    private var factory: JPAQueryFactory = jpaQueryFactory

    override fun getWalletHistory(
        walletFilterRequest: WalletFilterRequest,
        pageable: PageRequest
    ): List<WalletTradeHistoryResponseDto> {
        val subJoinTable = QWithdraw("subJoinWithdraw")

        return factory.select(
            Projections.constructor(
                WalletTradeHistoryResponseDto::class.java,
                walletTrace.tradeType,
                CaseBuilder().`when`(deposit.isNotNull).then(deposit.amount)
                    .otherwise(withdraw.amount),
                CaseBuilder().`when`(deposit.isNotNull).then(deposit.message)
                    .otherwise(withdraw.message),
                userForRead.userId,
                userForRead.nickname,
                walletTrace.createdAt
            )
        ).from(walletTrace)
            .leftJoin(wallet)
            .on(walletTrace.walletId.eq(wallet))
            .leftJoin(deposit)
            .on(walletTrace.tradeType.eq(TradeType.DEPOSIT).and(walletTrace.tradeId.eq(deposit.id)))
            .leftJoin(transfer)
            .on(deposit.transferId.isNotNull.and(deposit.transferId.eq(transfer)))
            .leftJoin(subJoinTable)
            .on(subJoinTable.transferId.isNotNull.and(subJoinTable.transferId.eq(subJoinTable.transferId)))
            .leftJoin(userForRead)
            .on(subJoinTable.walletId.ownerId.eq(userForRead.userId))
            .leftJoin(withdraw)
            .on(walletTrace.tradeType.eq(TradeType.WITHDRAW).and(walletTrace.tradeId.eq(withdraw.id)))
            .where(
                wallet.ownerId.eq(walletFilterRequest.walletOwnerId),
                wallet.ownerType.eq(walletFilterRequest.walletOwnerType),
                dateCondition(walletFilterRequest.historyStartDateInstant, walletFilterRequest.historyEndDateInstant),
                tradeTypeCondition(walletFilterRequest.tradeType)
            )
            .offset(pageable.offset)
            .limit(SliceUtil.getSliceLimit(pageable.pageSize))
            .orderBy(walletTrace.createdAt.desc())
            .fetch()
    }

    private fun tradeTypeCondition(tradeType: TradeType?): BooleanExpression? =
        if (tradeType != null) walletTrace.tradeType.eq(tradeType)
        else null

    private fun dateCondition(startDate: Instant, endDate: Instant): BooleanBuilder {
        val booleanBuilder = BooleanBuilder()

        booleanBuilder.and(walletTrace.createdAt.goe(startDate))
        booleanBuilder.and(walletTrace.createdAt.loe(endDate))

        return booleanBuilder
    }
}