package com.example.paymentecho.integration

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.service.CreditorService
import com.example.paymentecho.service.DebtorService
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
import org.hamcrest.Matchers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Sample Data Integration Tests")
class SampleDataIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val paymentService: PaymentService,
    private val creditorService: CreditorService,
    private val debtorService: DebtorService,
    private val objectMapper: ObjectMapper
) {

    private var sampleCreditorId: String? = null
    private var sampleDebtorId: String? = null
    private var samplePaymentId: String? = null

    @BeforeEach
    fun setUp() {
        // Create sample data for testing
        val creditor = creditorService.create(
            com.example.paymentecho.dto.request.CreditorCreateRequest(
                name = "Test Creditor",
                accountNumber = "TEST001",
                bankCode = "TESTBANK",
                email = "test@creditor.com"
            )
        )
        sampleCreditorId = creditor.id.toString()

        val debtor = debtorService.create(
            com.example.paymentecho.dto.request.DebtorCreateRequest(
                name = "Test Debtor",
                accountNumber = "TEST002",
                bankCode = "TESTBANK",
                email = "test@debtor.com"
            )
        )
        sampleDebtorId = debtor.id.toString()

        val payment = paymentService.create(
            PaymentCreateRequest(
                amount = 1000.0,
                currency = "USD",
                status = "RECEIVED",
                creditorId = creditor.id,
                debtorId = debtor.id
            )
        )
        samplePaymentId = payment.id.toString()
    }

    @Test
    @DisplayName("Should retrieve sample payment via REST API")
    fun `should retrieve sample payment via rest api`() {
        mockMvc.perform(get("/api/v1/payments/{id}", samplePaymentId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(samplePaymentId))
            .andExpect(jsonPath("$.amount").value(1000.0))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andExpect(jsonPath("$.status").value("RECEIVED"))
            .andExpect(jsonPath("$.creditorId").value(sampleCreditorId))
            .andExpect(jsonPath("$.debtorId").value(sampleDebtorId))
            .andExpect(jsonPath("$.createdAt").exists())
    }

    @Test
    @DisplayName("Should list all payments with sample data")
    fun `should list all payments with sample data`() {
        mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.payments").isArray)
            .andExpect(jsonPath("$.payments[0].id").exists())
            .andExpect(jsonPath("$.payments[0].amount").exists())
            .andExpect(jsonPath("$.payments[0].currency").exists())
    }

    @Test
    @DisplayName("Should filter payments by status")
    fun `should filter payments by status`() {
        // Create another payment with different status
        paymentService.create(
            PaymentCreateRequest(
                amount = 500.0,
                currency = "USD",
                status = "PROCESSING"
            )
        )

        mockMvc.perform(get("/api/v1/payments?status=RECEIVED"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.payments").isArray)
            .andExpect(jsonPath("$.payments[*].status").value(Matchers.everyItem(Matchers.equalTo("RECEIVED"))))
    }

    @Test
    @DisplayName("Should filter payments by currency")
    fun `should filter payments by currency`() {
        mockMvc.perform(get("/api/v1/payments?currency=USD"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.payments").isArray)
            .andExpect(jsonPath("$.payments[*].currency").value(Matchers.everyItem(Matchers.equalTo("USD"))))
    }

    @Test
    @DisplayName("Should retrieve sample creditor via REST API")
    fun `should retrieve sample creditor via rest api`() {
        mockMvc.perform(get("/api/v1/creditors/{id}", sampleCreditorId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(sampleCreditorId))
            .andExpect(jsonPath("$.name").value("Test Creditor"))
            .andExpect(jsonPath("$.accountNumber").value("TEST001"))
            .andExpect(jsonPath("$.bankCode").value("TESTBANK"))
    }

    @Test
    @DisplayName("Should list all creditors with sample data")
    fun `should list all creditors with sample data`() {
        mockMvc.perform(get("/api/v1/creditors"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.creditors").isArray)
            .andExpect(jsonPath("$.creditors[0].id").exists())
            .andExpect(jsonPath("$.creditors[0].name").exists())
    }

    @Test
    @DisplayName("Should filter creditors by name")
    fun `should filter creditors by name`() {
        mockMvc.perform(get("/api/v1/creditors?name=Test"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.creditors").isArray)
            .andExpect(jsonPath("$.creditors[0].name").value(org.hamcrest.Matchers.containsStringIgnoringCase("Test")))
    }

    @Test
    @DisplayName("Should retrieve sample debtor via REST API")
    fun `should retrieve sample debtor via rest api`() {
        mockMvc.perform(get("/api/v1/debtors/{id}", sampleDebtorId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(sampleDebtorId))
            .andExpect(jsonPath("$.name").value("Test Debtor"))
            .andExpect(jsonPath("$.accountNumber").value("TEST002"))
            .andExpect(jsonPath("$.bankCode").value("TESTBANK"))
    }

    @Test
    @DisplayName("Should list all debtors with sample data")
    fun `should list all debtors with sample data`() {
        mockMvc.perform(get("/api/v1/debtors"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").exists())
            .andExpect(jsonPath("$.debtors").isArray)
            .andExpect(jsonPath("$.debtors[0].id").exists())
            .andExpect(jsonPath("$.debtors[0].name").exists())
    }

    @Test
    @DisplayName("Should create new payment with sample creditor and debtor")
    fun `should create new payment with sample creditor and debtor`() {
        val request = PaymentCreateRequest(
            amount = 2000.0,
            currency = "EUR",
            status = "PROCESSING",
            creditorId = java.util.UUID.fromString(sampleCreditorId!!),
            debtorId = java.util.UUID.fromString(sampleDebtorId!!)
        )

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.amount").value(2000.0))
            .andExpect(jsonPath("$.currency").value("EUR"))
            .andExpect(jsonPath("$.status").value("PROCESSING"))
            .andExpect(jsonPath("$.creditorId").value(sampleCreditorId))
            .andExpect(jsonPath("$.debtorId").value(sampleDebtorId))
    }

    @Test
    @DisplayName("Should verify pagination works with sample data")
    fun `should verify pagination works with sample data`() {
        // Create multiple payments
        repeat(5) {
            paymentService.create(
                PaymentCreateRequest(
                    amount = (it + 1) * 100.0,
                    currency = "USD",
                    status = "RECEIVED"
                )
            )
        }

        mockMvc.perform(get("/api/v1/payments?page=0&size=3"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").value(Matchers.greaterThanOrEqualTo(3)))
            .andExpect(jsonPath("$.payments.length()").value(Matchers.lessThanOrEqualTo(3)))
    }

    @Test
    @DisplayName("Should verify response includes X-Request-ID header")
    fun `should verify response includes X-Request-ID header`() {
        val result = mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk)
            .andExpect(header().exists("X-Request-ID"))
            .andReturn()

        assertNotNull(result.response.getHeader("X-Request-ID"))
    }

    @Test
    @DisplayName("Should verify response includes X-Response-Time header")
    fun `should verify response includes X-Response-Time header`() {
        val result = mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk)
            .andExpect(header().exists("X-Response-Time"))
            .andReturn()

        assertNotNull(result.response.getHeader("X-Response-Time"))
    }

    @Test
    @DisplayName("Should verify soft delete works - deleted creditor not in list")
    fun `should verify soft delete works deleted creditor not in list`() {
        val creditor = creditorService.create(
            com.example.paymentecho.dto.request.CreditorCreateRequest(
                name = "To Delete",
                accountNumber = "DELETE001",
                bankCode = "DELBANK"
            )
        )

        // Verify it exists
        mockMvc.perform(get("/api/v1/creditors/{id}", creditor.id))
            .andExpect(status().isOk)

        // Delete it
        mockMvc.perform(delete("/api/v1/creditors/{id}", creditor.id))
            .andExpect(status().isNoContent)

        // Verify it's not in the list
        val result = mockMvc.perform(get("/api/v1/creditors"))
            .andExpect(status().isOk)
            .andReturn()

        val response = objectMapper.readTree(result.response.contentAsString)
        val creditors = response.get("creditors")
        assertFalse(creditors.any { it.get("id").asText() == creditor.id.toString() })
    }
}
