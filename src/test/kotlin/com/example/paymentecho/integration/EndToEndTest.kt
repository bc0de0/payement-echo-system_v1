package com.example.paymentecho.integration

import com.example.paymentecho.dto.request.CreditorCreateRequest
import com.example.paymentecho.dto.request.DebtorCreateRequest
import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
import com.example.paymentecho.dto.response.CreditorListResponse
import com.example.paymentecho.dto.response.CreditorResponse
import com.example.paymentecho.dto.response.DebtorListResponse
import com.example.paymentecho.dto.response.DebtorResponse
import com.example.paymentecho.dto.response.PaymentListResponse
import com.example.paymentecho.dto.response.PaymentResponse
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
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("End-to-End API Tests")
class EndToEndTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @BeforeEach
    fun setUp() {
        // Clean up is handled by @Transactional
    }

    @Test
    @DisplayName("Complete flow: Create Creditor -> Create Debtor -> Create Payment -> Retrieve -> Verify")
    fun `complete payment flow with creditor and debtor`() {
        // Step 1: Create Creditor
        val creditorRequest = CreditorCreateRequest(
            name = "Test Creditor",
            accountNumber = "CRED001",
            bankCode = "BANK001",
            email = "creditor@test.com"
        )

        val creditorResult = mockMvc.perform(
            post("/api/v1/creditors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creditorRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Test Creditor"))
            .andExpect(jsonPath("$.accountNumber").value("CRED001"))
            .andReturn()

        val creditorResponse = objectMapper.readValue(
            creditorResult.response.contentAsString,
            CreditorResponse::class.java
        )
        assertNotNull(creditorResponse.id)
        val creditorId = creditorResponse.id

        // Step 2: Create Debtor
        val debtorRequest = DebtorCreateRequest(
            name = "Test Debtor",
            accountNumber = "DEBT001",
            bankCode = "BANK002",
            email = "debtor@test.com"
        )

        val debtorResult = mockMvc.perform(
            post("/api/v1/debtors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debtorRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Test Debtor"))
            .andReturn()

        val debtorResponse = objectMapper.readValue(
            debtorResult.response.contentAsString,
            DebtorResponse::class.java
        )
        assertNotNull(debtorResponse.id)
        val debtorId = debtorResponse.id

        // Step 3: Create Payment with Creditor and Debtor
        val paymentRequest = PaymentCreateRequest(
            amount = 500.75,
            currency = "USD",
            status = "RECEIVED",
            creditorId = creditorId,
            debtorId = debtorId
        )

        val paymentResult = mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.amount").value(500.75))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andExpect(jsonPath("$.status").value("RECEIVED"))
            .andExpect(jsonPath("$.creditorId").value(creditorId.toString()))
            .andExpect(jsonPath("$.debtorId").value(debtorId.toString()))
            .andReturn()

        val paymentResponse = objectMapper.readValue(
            paymentResult.response.contentAsString,
            PaymentResponse::class.java
        )
        assertNotNull(paymentResponse.id)
        val paymentId = paymentResponse.id

        // Step 4: Retrieve Payment by ID
        mockMvc.perform(get("/api/v1/payments/{id}", paymentId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(paymentId.toString()))
            .andExpect(jsonPath("$.amount").value(500.75))
            .andExpect(jsonPath("$.creditorId").value(creditorId.toString()))
            .andExpect(jsonPath("$.debtorId").value(debtorId.toString()))

        // Step 5: Verify Creditor exists
        mockMvc.perform(get("/api/v1/creditors/{id}", creditorId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(creditorId.toString()))
            .andExpect(jsonPath("$.name").value("Test Creditor"))

        // Step 6: Verify Debtor exists
        mockMvc.perform(get("/api/v1/debtors/{id}", debtorId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(debtorId.toString()))
            .andExpect(jsonPath("$.name").value("Test Debtor"))

        // Step 7: Verify all payments list includes our payment
        val allPaymentsResult = mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andReturn()

        val allPayments = objectMapper.readValue(
            allPaymentsResult.response.contentAsString,
            PaymentListResponse::class.java
        )
        assertTrue(allPayments.payments.any { it.id == paymentId })
    }

    @Test
    @DisplayName("Test consistent list response format for Creditors")
    fun `test creditors list response format`() {
        // Create multiple creditors
        val creditor1 = CreditorCreateRequest("Creditor 1", "ACC001", "BANK001")
        val creditor2 = CreditorCreateRequest("Creditor 2", "ACC002", "BANK002")

        mockMvc.perform(
            post("/api/v1/creditors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creditor1))
        ).andExpect(status().isCreated)

        mockMvc.perform(
            post("/api/v1/creditors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creditor2))
        ).andExpect(status().isCreated)

        // Verify list response format
        val result = mockMvc.perform(get("/api/v1/creditors"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.creditors").isArray())
            .andExpect(jsonPath("$.creditors[0].id").exists())
            .andExpect(jsonPath("$.creditors[0].name").exists())
            .andReturn()

        val response = objectMapper.readValue(
            result.response.contentAsString,
            CreditorListResponse::class.java
        )
        assertTrue(response.total >= 2)
        assertTrue(response.creditors.size >= 2)
    }

    @Test
    @DisplayName("Test consistent list response format for Debtors")
    fun `test debtors list response format`() {
        // Create multiple debtors
        val debtor1 = DebtorCreateRequest("Debtor 1", "ACC001", "BANK001")
        val debtor2 = DebtorCreateRequest("Debtor 2", "ACC002", "BANK002")

        mockMvc.perform(
            post("/api/v1/debtors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debtor1))
        ).andExpect(status().isCreated)

        mockMvc.perform(
            post("/api/v1/debtors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debtor2))
        ).andExpect(status().isCreated)

        // Verify list response format
        val result = mockMvc.perform(get("/api/v1/debtors"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.debtors").isArray())
            .andExpect(jsonPath("$.debtors[0].id").exists())
            .andExpect(jsonPath("$.debtors[0].name").exists())
            .andReturn()

        val response = objectMapper.readValue(
            result.response.contentAsString,
            DebtorListResponse::class.java
        )
        assertTrue(response.total >= 2)
        assertTrue(response.debtors.size >= 2)
    }

    @Test
    @DisplayName("Test consistent list response format for Payments")
    fun `test payments list response format`() {
        // Create multiple payments
        val payment1 = PaymentCreateRequest(100.0, "USD", "RECEIVED")
        val payment2 = PaymentCreateRequest(200.0, "EUR", "PROCESSING")

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment1))
        ).andExpect(status().isCreated)

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment2))
        ).andExpect(status().isCreated)

        // Verify list response format
        val result = mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.payments").isArray())
            .andExpect(jsonPath("$.payments[0].id").exists())
            .andExpect(jsonPath("$.payments[0].amount").exists())
            .andReturn()

        val response = objectMapper.readValue(
            result.response.contentAsString,
            PaymentListResponse::class.java
        )
        assertTrue(response.total >= 2)
        assertTrue(response.payments.size >= 2)
    }

    @Test
    @DisplayName("Test Payment Echo endpoint")
    fun `test payment echo`() {
        val echoRequest = PaymentEchoRequest(
            amount = 999.99,
            currency = "GBP",
            status = "COMPLETED"
        )

        mockMvc.perform(
            post("/api/v1/payments/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(echoRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.amount").value(999.99))
            .andExpect(jsonPath("$.currency").value("GBP"))
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andExpect(jsonPath("$.id").exists())
    }

    @Test
    @DisplayName("Test validation errors return proper format")
    fun `test validation error format`() {
        val invalidRequest = PaymentCreateRequest(
            amount = -10.0,
            currency = "INVALID",
            status = "BAD_STATUS"
        )

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
            .andExpect(jsonPath("$.fieldErrors.amount").exists())
            .andExpect(jsonPath("$.fieldErrors.currency").exists())
            .andExpect(jsonPath("$.fieldErrors.status").exists())
    }

    @Test
    @DisplayName("Test 404 error format")
    fun `test 404 error format`() {
        val nonExistentId = UUID.randomUUID()

        mockMvc.perform(get("/api/v1/payments/{id}", nonExistentId))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").exists()) // Error message may be localized
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.path").exists())
    }

    @Test
    @DisplayName("Test CRUD operations for Creditor")
    fun `test creditor crud operations`() {
        // Create
        val createRequest = CreditorCreateRequest(
            name = "CRUD Creditor",
            accountNumber = "CRUD001",
            bankCode = "BANK001"
        )

        val createResult = mockMvc.perform(
            post("/api/v1/creditors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val created = objectMapper.readValue(
            createResult.response.contentAsString,
            CreditorResponse::class.java
        )
        val creditorId = created.id

        // Read
        mockMvc.perform(get("/api/v1/creditors/{id}", creditorId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(creditorId.toString()))
            .andExpect(jsonPath("$.name").value("CRUD Creditor"))

        // List
        mockMvc.perform(get("/api/v1/creditors"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.creditors").isArray())

        // Delete
        mockMvc.perform(delete("/api/v1/creditors/{id}", creditorId))
            .andExpect(status().isNoContent)

        // Verify deleted
        mockMvc.perform(get("/api/v1/creditors/{id}", creditorId))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("Test CRUD operations for Debtor")
    fun `test debtor crud operations`() {
        // Create
        val createRequest = DebtorCreateRequest(
            name = "CRUD Debtor",
            accountNumber = "CRUD002",
            bankCode = "BANK002"
        )

        val createResult = mockMvc.perform(
            post("/api/v1/debtors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val created = objectMapper.readValue(
            createResult.response.contentAsString,
            DebtorResponse::class.java
        )
        val debtorId = created.id

        // Read
        mockMvc.perform(get("/api/v1/debtors/{id}", debtorId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(debtorId.toString()))
            .andExpect(jsonPath("$.name").value("CRUD Debtor"))

        // List
        mockMvc.perform(get("/api/v1/debtors"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.debtors").isArray())

        // Delete
        mockMvc.perform(delete("/api/v1/debtors/{id}", debtorId))
            .andExpect(status().isNoContent)

        // Verify deleted
        mockMvc.perform(get("/api/v1/debtors/{id}", debtorId))
            .andExpect(status().isNotFound)
    }
}
