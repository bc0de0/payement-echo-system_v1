package com.example.paymentecho.integration

import com.example.paymentecho.dto.request.PaymentCreateRequest
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
@DisplayName("API Response Format Tests")
class ApiResponseFormatTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @BeforeEach
    fun setUp() {
        // Cleanup handled by @Transactional
    }

    @Test
    @DisplayName("Payment list response should have consistent format")
    fun `payment list response should have consistent format`() {
        // Create test payments
        repeat(3) {
            mockMvc.perform(
                post("/api/v1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        PaymentCreateRequest((it + 1) * 100.0, "USD", "RECEIVED")
                    ))
            )
        }

        val result = mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.total").isNumber())
            .andExpect(jsonPath("$.payments").exists())
            .andExpect(jsonPath("$.payments").isArray())
            .andExpect(jsonPath("$.payments[0].id").exists())
            .andExpect(jsonPath("$.payments[0].amount").exists())
            .andExpect(jsonPath("$.payments[0].currency").exists())
            .andExpect(jsonPath("$.payments[0].status").exists())
            .andExpect(jsonPath("$.payments[0].createdAt").exists())
            .andReturn()

        val responseJson = result.response.contentAsString
        val response = objectMapper.readTree(responseJson)
        
        assertTrue(response.has("total"))
        assertTrue(response.has("payments"))
        assertTrue(response.get("payments").isArray)
        assertEquals(response.get("total").asInt(), response.get("payments").size())
    }

    @Test
    @DisplayName("Payment single response should have all required fields")
    fun `payment single response should have all required fields`() {
        val createResult = mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    PaymentCreateRequest(100.0, "USD", "RECEIVED")
                ))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.amount").value(100.0))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andExpect(jsonPath("$.status").value("RECEIVED"))
            .andExpect(jsonPath("$.createdAt").exists())
            .andReturn()

        val responseJson = createResult.response.contentAsString
        val response = objectMapper.readTree(responseJson)
        
        assertTrue(response.has("id"))
        assertTrue(response.has("amount"))
        assertTrue(response.has("currency"))
        assertTrue(response.has("status"))
        assertTrue(response.has("createdAt"))
    }

    @Test
    @DisplayName("Error response should have consistent format")
    fun `error response should have consistent format`() {
        mockMvc.perform(get("/api/v1/payments/00000000-0000-0000-0000-000000000000"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.path").exists())
    }

    @Test
    @DisplayName("Validation error response should have fieldErrors")
    fun `validation error response should have fieldErrors`() {
        val invalidRequest = PaymentCreateRequest(-10.0, "INVALID", "BAD_STATUS")

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.path").exists())
            .andExpect(jsonPath("$.fieldErrors").exists())
            .andExpect(jsonPath("$.fieldErrors").isMap())
    }

    @Test
    @DisplayName("Pagination response should include total count")
    fun `pagination response should include total count`() {
        // Create 5 payments
        repeat(5) {
            mockMvc.perform(
                post("/api/v1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        PaymentCreateRequest((it + 1) * 100.0, "USD", "RECEIVED")
                    ))
            )
        }

        val result = mockMvc.perform(get("/api/v1/payments?page=0&size=2"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").value(5))
            .andExpect(jsonPath("$.payments").isArray())
            .andExpect(jsonPath("$.payments.length()").value(2))
            .andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        assertEquals(5, response.get("total").asInt())
        assertEquals(2, response.get("payments").size())
    }

    @Test
    @DisplayName("Filtered response should maintain format")
    fun `filtered response should maintain format`() {
        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    PaymentCreateRequest(100.0, "USD", "RECEIVED")
                ))
        )

        mockMvc.perform(get("/api/v1/payments?status=RECEIVED"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.payments").isArray())
            .andExpect(jsonPath("$.payments[0].status").value("RECEIVED"))
    }

    @Test
    @DisplayName("Response should include X-Request-ID header")
    fun `response should include X-Request-ID header`() {
        val result = mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk)
            .andExpect(header().exists("X-Request-ID"))
            .andReturn()

        val requestId = result.response.getHeader("X-Request-ID")
        assertNotNull(requestId)
        // UUID format: 8-4-4-4-12 hex digits
        assertTrue(requestId!!.matches(Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")))
    }

    @Test
    @DisplayName("Response should include X-Response-Time header")
    fun `response should include X-Response-Time header`() {
        val result = mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk)
            .andExpect(header().exists("X-Response-Time"))
            .andReturn()

        val responseTime = result.response.getHeader("X-Response-Time")
        assertNotNull(responseTime)
        assertTrue(responseTime!!.endsWith("ms"))
    }
}
