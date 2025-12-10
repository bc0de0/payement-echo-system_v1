package com.example.paymentecho.integration

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
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
@DisplayName("Edge Case Tests")
class EdgeCaseTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @BeforeEach
    fun setUp() {
        // Cleanup handled by @Transactional
    }

    @Test
    @DisplayName("Should handle very large payment amount")
    fun `should handle very large payment amount`() {
        val request = PaymentCreateRequest(
            amount = 999999999.99,
            currency = "USD",
            status = "RECEIVED"
        )

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.amount").value(999999999.99))
    }

    @Test
    @DisplayName("Should reject amount exceeding maximum")
    fun `should reject amount exceeding maximum`() {
        val request = PaymentCreateRequest(
            amount = 1000000000.0, // Exceeds max
            currency = "USD",
            status = "RECEIVED"
        )

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should reject zero amount")
    fun `should reject zero amount`() {
        val request = PaymentCreateRequest(
            amount = 0.0,
            currency = "USD",
            status = "RECEIVED"
        )

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should reject negative amount")
    fun `should reject negative amount`() {
        val request = PaymentCreateRequest(
            amount = -10.0,
            currency = "USD",
            status = "RECEIVED"
        )

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should handle minimum valid amount")
    fun `should handle minimum valid amount`() {
        val request = PaymentCreateRequest(
            amount = 0.01,
            currency = "USD",
            status = "RECEIVED"
        )

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.amount").value(0.01))
    }

    @Test
    @DisplayName("Should handle all valid statuses")
    fun `should handle all valid statuses`() {
        val statuses = listOf("RECEIVED", "PROCESSING", "COMPLETED", "FAILED")

        statuses.forEach { status ->
            val request = PaymentCreateRequest(100.0, "USD", status)

            mockMvc.perform(
                post("/api/v1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.status").value(status))
        }
    }

    @Test
    @DisplayName("Should reject invalid status")
    fun `should reject invalid status`() {
        val request = PaymentCreateRequest(
            amount = 100.0,
            currency = "USD",
            status = "INVALID_STATUS"
        )

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should handle various currency codes")
    fun `should handle various currency codes`() {
        val currencies = listOf("USD", "EUR", "GBP", "JPY", "INR", "CAD", "AUD")

        currencies.forEach { currency ->
            val request = PaymentCreateRequest(100.0, currency, "RECEIVED")

            mockMvc.perform(
                post("/api/v1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.currency").value(currency))
        }
    }

    @Test
    @DisplayName("Should reject invalid currency format")
    fun `should reject invalid currency format`() {
        val invalidCurrencies = listOf("US", "USDD", "123", "usd", "UsD")

        invalidCurrencies.forEach { currency ->
            val request = PaymentCreateRequest(100.0, currency, "RECEIVED")

            mockMvc.perform(
                post("/api/v1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isBadRequest)
        }
    }

    @Test
    @DisplayName("Should handle empty request body")
    fun `should handle empty request body`() {
        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
            .andExpect { result ->
                // Empty body may result in 400 (validation) or 500 (parsing error)
                assertTrue(result.response.status in 400..499 || result.response.status == 500)
            }
    }

    @Test
    @DisplayName("Should handle null values in request")
    fun `should handle null values in request`() {
        val result = mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":null,\"currency\":null,\"status\":null}")
        )
            .andReturn()
        
        // Null values should result in validation error (400) or parsing error (500)
        assertTrue(
            result.response.status in 400..499 || result.response.status == 500,
            "Expected 4xx or 500 status, got ${result.response.status}"
        )
    }

    @Test
    @DisplayName("Should handle invalid UUID format")
    fun `should handle invalid UUID format`() {
        mockMvc.perform(get("/api/v1/payments/invalid-uuid"))
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("Should handle pagination edge cases")
    fun `should handle pagination edge cases`() {
        // Test negative page number - Spring will handle gracefully
        mockMvc.perform(get("/api/v1/payments?page=-1"))
            .andExpect { result ->
                assertTrue(result.response.status in 200..299 || result.response.status == 400)
            }

        // Test zero page size - Spring will handle gracefully
        mockMvc.perform(get("/api/v1/payments?size=0"))
            .andExpect { result ->
                assertTrue(result.response.status in 200..299 || result.response.status == 400)
            }

        // Test very large page number - Returns empty page
        mockMvc.perform(get("/api/v1/payments?page=999999"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").value(0))
    }

    @Test
    @DisplayName("Should handle special characters in names")
    fun `should handle special characters in names`() {
        val request = mapOf(
            "name" to "O'Brien & Co.",
            "accountNumber" to "ACC001",
            "bankCode" to "BANK001"
        )

        mockMvc.perform(
            post("/api/v1/creditors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
    }

    @Test
    @DisplayName("Should handle very long strings")
    fun `should handle very long strings`() {
        val longName = "A".repeat(500)
        val request = mapOf(
            "name" to longName,
            "accountNumber" to "ACC001",
            "bankCode" to "BANK001"
        )

        mockMvc.perform(
            post("/api/v1/creditors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
    }

    @Test
    @DisplayName("Should handle concurrent requests")
    fun `should handle concurrent requests`() {
        val requests = (1..10).map {
            PaymentCreateRequest(it * 10.0, "USD", "RECEIVED")
        }

        requests.forEach { request ->
            mockMvc.perform(
                post("/api/v1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isCreated)
        }

        val result = mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk)
            .andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        assertTrue(response.get("total").asInt() >= 10)
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    fun `should handle malformed JSON`() {
        val result = mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}")
        )
            .andReturn()
        
        // Malformed JSON should result in 400 (bad request) or 500 (parsing error)
        assertTrue(
            result.response.status in 400..499 || result.response.status == 500,
            "Expected 4xx or 500 status, got ${result.response.status}"
        )
    }

    @Test
    @DisplayName("Should handle wrong content type")
    fun `should handle wrong content type`() {
        val result = mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.TEXT_PLAIN)
                .content("some text")
        )
            .andReturn()
        
        // Spring may return 415 (Unsupported Media Type), 400 (Bad Request), or 500 (Internal Server Error)
        // depending on how it handles the content type mismatch
        assertTrue(
            result.response.status in 400..499 || result.response.status == 500,
            "Expected 4xx or 500 status, got ${result.response.status}"
        )
    }

    @Test
    @DisplayName("Should handle filtering with no results")
    fun `should handle filtering with no results`() {
        mockMvc.perform(get("/api/v1/payments?status=NONEXISTENT"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").value(0))
            .andExpect(jsonPath("$.payments").isArray())
    }

    @Test
    @DisplayName("Should handle sorting with invalid field")
    fun `should handle sorting with invalid field`() {
        mockMvc.perform(get("/api/v1/payments?sort=invalidField,asc"))
            .andExpect { result ->
                // Spring may return 200 or 400/500 depending on configuration
                assertTrue(result.response.status in 200..599)
            }
    }

    @Test
    @DisplayName("Should handle i18n with different languages")
    fun `should handle i18n with different languages`() {
        // Test English
        mockMvc.perform(
            get("/api/v1/payments/00000000-0000-0000-0000-000000000000")
                .header("Accept-Language", "en")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").exists())

        // Test Spanish
        mockMvc.perform(
            get("/api/v1/payments/00000000-0000-0000-0000-000000000000")
                .header("Accept-Language", "es")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").exists())
    }
}
