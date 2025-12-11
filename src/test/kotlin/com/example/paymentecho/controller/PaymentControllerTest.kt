package com.example.paymentecho.controller

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
import com.example.paymentecho.dto.response.PaymentResponse
import com.example.paymentecho.service.PaymentService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Instant
import java.util.*

@WebMvcTest(PaymentController::class)
@ActiveProfiles("test")
class PaymentControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var paymentService: PaymentService

    @Test
    fun `should get all payments`() {
        val payments = listOf(
            PaymentResponse(UUID.randomUUID(), 100.0, "USD", "RECEIVED", Instant.now()),
            PaymentResponse(UUID.randomUUID(), 200.0, "EUR", "PROCESSING", Instant.now())
        )
        val page = PageImpl(payments, PageRequest.of(0, 20), payments.size.toLong())
        whenever(paymentService.findAll(0, 20, null, null, null, null, null, null, null)).thenReturn(page)

        mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.total").value(2))
            .andExpect(jsonPath("$.payments[0].amount").value(100.0))
    }

    @Test
    fun `should get payment by id`() {
        val id = UUID.randomUUID()
        val payment = PaymentResponse(id, 100.0, "USD", "RECEIVED", Instant.now())
        whenever(paymentService.findById(id)).thenReturn(payment)

        mockMvc.perform(get("/api/v1/payments/{id}", id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.amount").value(100.0))
    }

    @Test
    fun `should create payment`() {
        val request = PaymentCreateRequest(amount = 100.0, currency = "USD", status = "RECEIVED")
        val response = PaymentResponse(UUID.randomUUID(), 100.0, "USD", "RECEIVED", Instant.now())
        whenever(paymentService.create(any())).thenReturn(response)

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.amount").value(100.0))
    }

    @Test
    fun `should return bad request when validation fails`() {
        val request = PaymentCreateRequest(amount = -10.0, currency = "INVALID", status = "RECEIVED")

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should echo payment`() {
        val request = PaymentEchoRequest(amount = 150.0, currency = "EUR", status = "COMPLETED")
        val response = PaymentResponse(UUID.randomUUID(), 150.0, "EUR", "COMPLETED", Instant.now())
        whenever(paymentService.echo(any())).thenReturn(response)

        mockMvc.perform(
            post("/api/v1/payments/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.amount").value(150.0))
            .andExpect(jsonPath("$.currency").value("EUR"))
    }
}
