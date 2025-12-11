package com.example.paymentecho.graphql

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.service.PaymentService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
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
@DisplayName("GraphQL Integration Tests")
class GraphQLIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val paymentService: PaymentService,
    private val objectMapper: ObjectMapper
) {

    @BeforeEach
    fun setUp() {
        // Cleanup handled by @Transactional
    }

    @Test
    @DisplayName("Should query all payments via GraphQL")
    fun `should query all payments via graphql`() {
        // Create test payment
        paymentService.create(PaymentCreateRequest(100.0, "USD", "RECEIVED"))

        val query = "query { payments { payments { id amount currency status } total } }"
        val requestBody = objectMapper.writeValueAsString(mapOf("query" to query))

        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.payments.payments").isArray)
            .andExpect(jsonPath("$.data.payments.total").exists())
    }

    @Test
    @DisplayName("Should query payment by ID via GraphQL")
    fun `should query payment by id via graphql`() {
        val payment = paymentService.create(PaymentCreateRequest(200.0, "EUR", "PROCESSING"))

        val query = "query { payment(id: \"${payment.id}\") { id amount currency status } }"
        val requestBody = objectMapper.writeValueAsString(mapOf("query" to query))

        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.payment.id").value(payment.id.toString()))
            .andExpect(jsonPath("$.data.payment.amount").value(200.0))
            .andExpect(jsonPath("$.data.payment.currency").value("EUR"))
    }

    @Test
    @DisplayName("Should filter payments by status via GraphQL")
    fun `should filter payments by status via graphql`() {
        paymentService.create(PaymentCreateRequest(100.0, "USD", "RECEIVED"))
        paymentService.create(PaymentCreateRequest(200.0, "USD", "PROCESSING"))

        val query = "query { paymentsByStatus(status: \"RECEIVED\") { payments { status } total } }"
        val requestBody = objectMapper.writeValueAsString(mapOf("query" to query))

        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.paymentsByStatus.total").value(1))
            .andExpect(jsonPath("$.data.paymentsByStatus.payments[0].status").value("RECEIVED"))
    }

    @Test
    @DisplayName("Should create payment via GraphQL mutation")
    fun `should create payment via graphql mutation`() {
        val mutation = "mutation { createPayment(input: { amount: 150.0 currency: \"USD\" status: \"RECEIVED\" }) { id amount currency status } }"
        val requestBody = objectMapper.writeValueAsString(mapOf("query" to mutation))

        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.createPayment.id").exists())
            .andExpect(jsonPath("$.data.createPayment.amount").value(150.0))
            .andExpect(jsonPath("$.data.createPayment.currency").value("USD"))
            .andExpect(jsonPath("$.data.createPayment.status").value("RECEIVED"))
    }

    @Test
    @DisplayName("Should query creditors via GraphQL")
    fun `should query creditors via graphql`() {
        val query = "query { creditors { creditors { id name accountNumber bankCode } total } }"
        val requestBody = objectMapper.writeValueAsString(mapOf("query" to query))

        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.creditors").exists())
            .andExpect(jsonPath("$.data.creditors.creditors").isArray)
    }

    @Test
    @DisplayName("Should query debtors via GraphQL")
    fun `should query debtors via graphql`() {
        val query = "query { debtors { debtors { id name accountNumber bankCode } total } }"
        val requestBody = objectMapper.writeValueAsString(mapOf("query" to query))

        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.debtors").exists())
            .andExpect(jsonPath("$.data.debtors.debtors").isArray)
    }
}
