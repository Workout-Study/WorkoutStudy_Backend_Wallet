package com.fitmate.walletservice.controller

import com.fitmate.walletservice.common.GlobalURI
import com.fitmate.walletservice.dto.DepositRequestDto
import com.fitmate.walletservice.module.WalletModuleService
import com.fitmate.walletservice.persistence.entity.WalletOwnerType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WalletControllerBootTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var walletModuleService: WalletModuleService

    private val walletId = 341L
    private val walletOwnerId = 63
    private val walletOwnerType = WalletOwnerType.USER

    //@Test
    @DisplayName("[통합][Controller] Get Balance - 성공 테스트")
    @Throws(Exception::class)
    fun `get wallet balance`() {
        //given
        val depositRequestDto = DepositRequestDto(walletOwnerId, walletOwnerType, 50000, "test", "test")

        walletModuleService.deposit(depositRequestDto)

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
    }
}
