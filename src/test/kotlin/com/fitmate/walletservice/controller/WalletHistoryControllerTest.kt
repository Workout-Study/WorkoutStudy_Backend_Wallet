package com.fitmate.walletservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fitmate.walletservice.common.GlobalURI
import com.fitmate.walletservice.dto.WalletFilterRequest
import com.fitmate.walletservice.dto.WalletTradeHistoryResponse
import com.fitmate.walletservice.dto.WalletTradeHistoryResponseDto
import com.fitmate.walletservice.persistence.entity.TradeType
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import com.fitmate.walletservice.service.WalletHistoryService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.util.UriComponentsBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@WebMvcTest(WalletHistoryController::class)
@AutoConfigureRestDocs
class WalletHistoryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var walletHistoryService: WalletHistoryService

    private val walletOwnerId = 723
    private val walletOwnerType = WalletOwnerType.GROUP
    private val historyStartDate = LocalDate.now().withDayOfMonth(1)
        .atStartOfDay().toInstant(ZoneOffset.UTC)
    private val historyEndDate: Instant =
        LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
            .atStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59).toInstant(ZoneOffset.UTC)
    private val pageNumber: Int = 0
    private val pageSize: Int = 5
    private val hasNext = true
    private val walletBalance = 50000

    @Test
    @DisplayName("[단위][Controller] Get Wallet History - 성공 테스트")
    @Throws(Exception::class)
    fun `get wallet history`() {
        //given
        val content = mutableListOf<WalletTradeHistoryResponseDto>()

        for (i in 1..5) {
            val tradeType = if (i % 2 == 0) TradeType.DEPOSIT else TradeType.WITHDRAW

            content.add(
                WalletTradeHistoryResponseDto(tradeType, i * 1000, "Test", i, "Test" + i, Instant.now())
            )
        }

        val walletTradeHistoryResponse =
            WalletTradeHistoryResponse(pageNumber, pageSize, hasNext, walletBalance, content)

        whenever(walletHistoryService.getWalletHistory(any<WalletFilterRequest>()))
            .thenReturn(walletTradeHistoryResponse)

        val queryString = UriComponentsBuilder.newInstance()
            .queryParam("walletOwnerId", walletOwnerId)
            .queryParam("walletOwnerType", walletOwnerType)
            .queryParam("historyStartDate", historyStartDate)
            .queryParam("historyEndDate", historyEndDate)
            .queryParam("pageNumber", pageNumber)
            .queryParam("pageSize", pageSize)
            .queryParam("tradeType", TradeType.DEPOSIT)
            .build()
            .encode()
            .toUriString()

        //when
        val resultActions = mockMvc.perform(
            get(GlobalURI.WALLET_HISTORY + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        //then
        resultActions.andExpect(status().isOk())
            .andDo(print())
            .andDo(
                document(
                    "get-wallet-history",
                    queryParameters(
                        parameterWithName("pageNumber")
                            .description("거래내역 조회할 wallet history 페이지 번호 ( null일 경우 기본값 0 )"),
                        parameterWithName("pageSize")
                            .description("거래내역 조회할 wallet history slice size ( null일 경우 기본값 5 )"),
                        parameterWithName("walletOwnerId")
                            .description("거래내역 조회할 지갑 주인 id ( walletOwnerType이 GROUP일 경우 fit-group-id, USER일 경우 user-id ) 필수값!!"),
                        parameterWithName("walletOwnerType")
                            .description("거래내역 조회할 지갑 주인 타입 ( GROUP: fit-group, USER: user ) 필수값!!"),
                        parameterWithName("historyStartDate")
                            .description("조회할 거래내역 날짜 시작일"),
                        parameterWithName("historyEndDate")
                            .description("조회할 거래내역 날짜 시작일"),
                        parameterWithName("tradeType")
                            .description("조회할 거래내역 종류 ( null 혹은 미전송: 전부 조회, DEPOSIT: 입금만 조회, WITHDRAW : 출금만 조회 )")
                    ),
                    responseFields(
                        fieldWithPath("pageNumber").type(JsonFieldType.NUMBER).description("조회한 페이지 번호"),
                        fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("조회 한 페이지 size"),
                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 Slice가 있는지"),
                        fieldWithPath("walletBalance").type(JsonFieldType.NUMBER).description("현재 지갑 잔액"),
                        fieldWithPath("content[]").type(JsonFieldType.ARRAY).description("지갑 거래내역 데이터"),
                        fieldWithPath("content[].tradeType").type(JsonFieldType.STRING)
                            .description("거래종류 ( DEPOSIT : 입금, WITHDRAW : 출금 )"),
                        fieldWithPath("content[].amount").type(JsonFieldType.NUMBER)
                            .description("거래 금액"),
                        fieldWithPath("content[].message").type(JsonFieldType.STRING)
                            .description("거래 내용"),
                        fieldWithPath("content[].depositUserId").type(JsonFieldType.NUMBER)
                            .description("입금일때 입금자 user id ( 출금일때는 null )"),
                        fieldWithPath("content[].depositUserNickname").type(JsonFieldType.STRING)
                            .description("입금일때 입금자 user nickname ( 출금일때는 null )"),
                        fieldWithPath("content[].createdAt").type(JsonFieldType.STRING)
                            .description("거래 내역 발생 시간")
                    )
                )
            )
    }
}