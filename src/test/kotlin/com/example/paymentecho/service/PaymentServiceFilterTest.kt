package com.example.paymentecho.service

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.entity.Payment
import com.example.paymentecho.repository.PaymentRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Payment Service Filter Tests")
class PaymentServiceFilterTest @Autowired constructor(
    private val paymentService: PaymentService,
    private val paymentRepository: PaymentRepository
) {

    @BeforeEach
    fun setUp() {
        paymentRepository.deleteAll()
    }

    @Test
    @DisplayName("Should filter payments by status")
    fun `should filter payments by status`() {
        paymentService.create(PaymentCreateRequest(100.0, "USD", "RECEIVED"))
        paymentService.create(PaymentCreateRequest(200.0, "USD", "PROCESSING"))
        paymentService.create(PaymentCreateRequest(300.0, "EUR", "RECEIVED"))

        val result = paymentService.findAll(0, 20, null, status = "RECEIVED")

        assertEquals(2, result.totalElements)
        assertTrue(result.content.all { it.status == "RECEIVED" })
    }

    @Test
    @DisplayName("Should filter payments by currency")
    fun `should filter payments by currency`() {
        paymentService.create(PaymentCreateRequest(100.0, "USD", "RECEIVED"))
        paymentService.create(PaymentCreateRequest(200.0, "EUR", "RECEIVED"))
        paymentService.create(PaymentCreateRequest(300.0, "USD", "PROCESSING"))

        val result = paymentService.findAll(0, 20, null, currency = "USD")

        assertEquals(2, result.totalElements)
        assertTrue(result.content.all { it.currency == "USD" })
    }

    @Test
    @DisplayName("Should filter payments by status and currency")
    fun `should filter payments by status and currency`() {
        paymentService.create(PaymentCreateRequest(100.0, "USD", "RECEIVED"))
        paymentService.create(PaymentCreateRequest(200.0, "USD", "PROCESSING"))
        paymentService.create(PaymentCreateRequest(300.0, "EUR", "RECEIVED"))

        val result = paymentService.findAll(0, 20, null, status = "RECEIVED", currency = "USD")

        assertEquals(1, result.totalElements)
        assertEquals("USD", result.content[0].currency)
        assertEquals("RECEIVED", result.content[0].status)
    }

    @Test
    @DisplayName("Should filter payments by amount range")
    fun `should filter payments by amount range`() {
        paymentService.create(PaymentCreateRequest(50.0, "USD", "RECEIVED"))
        paymentService.create(PaymentCreateRequest(150.0, "USD", "RECEIVED"))
        paymentService.create(PaymentCreateRequest(250.0, "USD", "RECEIVED"))

        val result = paymentService.findAll(0, 20, null, minAmount = 100.0, maxAmount = 200.0)

        assertEquals(1, result.totalElements)
        assertTrue(result.content[0].amount >= 100.0)
        assertTrue(result.content[0].amount <= 200.0)
    }

    @Test
    @DisplayName("Should return empty page when no payments match filter")
    fun `should return empty page when no payments match filter`() {
        paymentService.create(PaymentCreateRequest(100.0, "USD", "RECEIVED"))

        val result = paymentService.findAll(0, 20, null, status = "COMPLETED")

        assertEquals(0, result.totalElements)
        assertTrue(result.content.isEmpty())
    }

    @Test
    @DisplayName("Should handle pagination with filters")
    fun `should handle pagination with filters`() {
        // Create 5 payments with RECEIVED status
        repeat(5) {
            paymentService.create(PaymentCreateRequest((it + 1) * 100.0, "USD", "RECEIVED"))
        }

        val page1 = paymentService.findAll(0, 2, null, status = "RECEIVED")
        val page2 = paymentService.findAll(1, 2, null, status = "RECEIVED")

        assertEquals(5, page1.totalElements)
        assertEquals(2, page1.content.size)
        assertEquals(2, page2.content.size)
    }
}
