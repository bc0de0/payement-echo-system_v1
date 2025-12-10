package com.example.paymentecho.controller

import com.example.paymentecho.dto.request.CreditorCreateRequest
import com.example.paymentecho.dto.response.CreditorListResponse
import com.example.paymentecho.dto.response.CreditorResponse
import com.example.paymentecho.service.CreditorService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
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

@WebMvcTest(CreditorController::class)
@ActiveProfiles("test")
class CreditorControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var creditorService: CreditorService

    @Test
    fun `should get all creditors`() {
        val creditors = listOf(
            CreditorResponse(UUID.randomUUID(), "John Doe", "ACC001", "BANK001", null, null, Instant.now(), Instant.now()),
            CreditorResponse(UUID.randomUUID(), "Jane Smith", "ACC002", "BANK002", null, null, Instant.now(), Instant.now())
        )
        val page = PageImpl(creditors, PageRequest.of(0, 20), creditors.size.toLong())
        whenever(creditorService.findAll(0, 20, null, null, null)).thenReturn(page)

        mockMvc.perform(get("/api/v1/creditors"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").value(2))
            .andExpect(jsonPath("$.creditors[0].name").value("John Doe"))
            .andExpect(jsonPath("$.creditors[1].name").value("Jane Smith"))
    }

    @Test
    fun `should get creditor by id`() {
        val id = UUID.randomUUID()
        val creditor = CreditorResponse(id, "John Doe", "ACC001", "BANK001", null, null, Instant.now(), Instant.now())
        whenever(creditorService.findById(id)).thenReturn(creditor)

        mockMvc.perform(get("/api/v1/creditors/{id}", id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.name").value("John Doe"))
    }

    @Test
    fun `should create creditor`() {
        val request = CreditorCreateRequest(name = "John Doe", accountNumber = "ACC001", bankCode = "BANK001")
        val response = CreditorResponse(UUID.randomUUID(), "John Doe", "ACC001", "BANK001", null, null, Instant.now(), Instant.now())
        whenever(creditorService.create(any())).thenReturn(response)

        mockMvc.perform(
            post("/api/v1/creditors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("John Doe"))
    }

    @Test
    fun `should return bad request when validation fails`() {
        val request = CreditorCreateRequest(name = "", accountNumber = "", bankCode = "")

        mockMvc.perform(
            post("/api/v1/creditors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should delete creditor`() {
        val id = UUID.randomUUID()
        doNothing().whenever(creditorService).delete(id)

        mockMvc.perform(delete("/api/v1/creditors/{id}", id))
            .andExpect(status().isNoContent)
    }
}
