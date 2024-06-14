package com.fitmate.walletservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fitmate.walletservice.common.GlobalURI
import com.fitmate.walletservice.dto.WalletDetailResponse
import com.fitmate.walletservice.module.WalletModuleService
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
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

@WebMvcTest(WalletController::class)
@AutoConfigureRestDocs
class WalletControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var walletModuleService: WalletModuleService

    private val walletId = 341L
    private val walletOwnerId = 63
    private val walletOwnerType = WalletOwnerType.USER
    private val balance = 65000

    @Test
    @DisplayName("[단위][Controller] Get Balance - 성공 테스트")
    @Throws(Exception::class)
    fun `get wallet balance`() {
        //given
        val walletDetailResponse = WalletDetailResponse(walletId, walletOwnerId, walletOwnerType, balance)

        whenever(walletModuleService.getWalletDetail(any<Int>(), any<WalletOwnerType>()))
            .thenReturn(walletDetailResponse)

        //when
        val resultActions = mockMvc.perform(
            get(
                GlobalURI.WALLET_ROOT
                        + GlobalURI.PATH_VARIABLE_WALLET_OWNER_ID_WITH_BRACE
                        + GlobalURI.PATH_VARIABLE_WALLET_OWNER_TYPE_WITH_BRACE, walletOwnerId, walletOwnerType
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        //then
        resultActions.andExpect(status().isOk())
            .andDo(print())
            .andDo(
                document(
                    "get-wallet-detail",
                    pathParameters(
                        parameterWithName(GlobalURI.PATH_VARIABLE_WALLET_OWNER_ID)
                            .description("조회할 지갑 주인 id ( walletOwnerType이 GROUP일 경우 fit-group-id, USER일 경우 user-id ) 필수값!!"),
                        parameterWithName(GlobalURI.PATH_VARIABLE_WALLET_OWNER_TYPE)
                            .description("조회할 지갑 주인 타입 ( GROUP: fit-group, USER: user ) 필수값!!")
                    ),
                    responseFields(
                        fieldWithPath("walletId").type(JsonFieldType.NUMBER)
                            .description("지갑 고유 번호"),
                        fieldWithPath("walletOwnerId").type(JsonFieldType.NUMBER)
                            .description("지갑 주인 id"),
                        fieldWithPath("walletOwnerType").type(JsonFieldType.STRING)
                            .description("지갑 주인 타입 ( GROUP: fit-group, USER: user )"),
                        fieldWithPath("balance").type(JsonFieldType.NUMBER)
                            .description("지갑 잔액")
                    )
                )
            )
    }
}