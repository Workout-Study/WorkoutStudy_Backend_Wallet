package com.fitmate.walletservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fitmate.walletservice.common.GlobalURI
import com.fitmate.walletservice.dto.TransferRequest
import com.fitmate.walletservice.module.WalletModuleService
import com.fitmate.walletservice.persistence.entity.UserForRead
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import com.fitmate.walletservice.persistence.repository.UserForReadRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WalletHistoryControllerBootTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var walletModuleService: WalletModuleService

    @Autowired
    private lateinit var userForReadRepository: UserForReadRepository

    private val walletOwnerId = 723
    private val walletOwnerType = WalletOwnerType.GROUP
    private val historyStartDate = LocalDate.now().withDayOfMonth(1)
        .atStartOfDay().toInstant(ZoneOffset.UTC)
    private val historyEndDate: Instant =
        LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
            .atStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59).toInstant(ZoneOffset.UTC)
    private val pageNumber: Int = 0
    private val pageSize: Int = 5

    @BeforeEach
    fun `set up`() {
        val userForRead = UserForRead(3, "testuser", "test")
        userForReadRepository.save(userForRead)

        for (i in 1..5) {
            val transferRequest = TransferRequest(
                userForRead.userId,
                WalletOwnerType.USER,
                walletOwnerId,
                walletOwnerType,
                i * 1000,
                "test" + i,
                i,
                "test"
            )

            walletModuleService.transfer(transferRequest)
        }
    }

    @Test
    @DisplayName("[통합][Controller] Get Wallet History - 성공 테스트")
    @Throws(Exception::class)
    fun `get wallet history`() {
        //given
        val queryString = UriComponentsBuilder.newInstance()
            .queryParam("walletOwnerId", walletOwnerId)
            .queryParam("walletOwnerType", walletOwnerType)
            .queryParam("historyStartDate", historyStartDate)
            .queryParam("historyEndDate", historyEndDate)
            .queryParam("pageNumber", pageNumber)
            .queryParam("pageSize", pageSize)
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
    }
}