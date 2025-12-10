package com.example.paymentecho.integration

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
import com.example.paymentecho.entity.Creditor
import com.example.paymentecho.entity.Debtor
import com.example.paymentecho.repository.CreditorRepository
import com.example.paymentecho.repository.DebtorRepository
import com.example.paymentecho.repository.PaymentRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val paymentRepository: PaymentRepository,
    private val creditorRepository: CreditorRepository,
    private val debtorRepository: DebtorRepository
) {

    @BeforeEach
    fun setUp() {
        paymentRepository.deleteAll()
        creditorRepository.deleteAll()
        debtorRepository.deleteAll()
    }

    @Test
    fun `should create and retrieve payment`() {
        val request = PaymentCreateRequest(amount = 100.0, currency = "USD", status = "RECEIVED")

        val createResult = mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.amount").value(100.0))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andReturn()

        val responseJson = createResult.response.contentAsString
        val paymentResponse = objectMapper.readTree(responseJson)
        val paymentId = paymentResponse.get("id").asText()

        mockMvc.perform(get("/api/v1/payments/{id}", paymentId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(paymentId))
            .andExpect(jsonPath("$.amount").value(100.0))
    }

    @Test
    fun `should create payment with creditor and debtor`() {
        val creditor = creditorRepository.save(Creditor(name = "Creditor", accountNumber = "ACC001", bankCode = "BANK001"))
        val debtor = debtorRepository.save(Debtor(name = "Debtor", accountNumber = "ACC002", bankCode = "BANK002"))

        val request = PaymentCreateRequest(
            amount = 200.0,
            currency = "EUR",
            status = "PROCESSING",
            creditorId = creditor.id,
            debtorId = debtor.id
        )

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.amount").value(200.0))
            .andExpect(jsonPath("$.creditorId").value(creditor.id.toString()))
            .andExpect(jsonPath("$.debtorId").value(debtor.id.toString()))
    }

    @Test
    fun `should echo payment`() {
        val request = PaymentEchoRequest(amount = 150.0, currency = "GBP", status = "COMPLETED")

        mockMvc.perform(
            post("/api/v1/payments/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.amount").value(150.0))
            .andExpect(jsonPath("$.currency").value("GBP"))
            .andExpect(jsonPath("$.status").value("COMPLETED"))
    }

    @Test
    fun `should return 404 when payment not found`() {
        mockMvc.perform(get("/api/v1/payments/{id}", "00000000-0000-0000-0000-000000000000"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return 400 when validation fails`() {
        val request = PaymentCreateRequest(amount = -10.0, currency = "INVALID", status = "INVALID_STATUS")

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }
}
