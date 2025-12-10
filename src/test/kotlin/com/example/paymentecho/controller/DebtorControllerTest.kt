package com.example.paymentecho.controller

import com.example.paymentecho.dto.request.DebtorCreateRequest
import com.example.paymentecho.dto.response.DebtorListResponse
import com.example.paymentecho.dto.response.DebtorResponse
import com.example.paymentecho.service.DebtorService
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

@WebMvcTest(DebtorController::class)
@ActiveProfiles("test")
class DebtorControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    @MockBean
    private lateinit var debtorService: DebtorService

    @Test
    fun `should get all debtors`() {
        val debtors = listOf(
            DebtorResponse(UUID.randomUUID(), "John Doe", "ACC001", "BANK001", null, null, Instant.now(), Instant.now()),
            DebtorResponse(UUID.randomUUID(), "Jane Smith", "ACC002", "BANK002", null, null, Instant.now(), Instant.now())
        )
        val page = PageImpl(debtors, PageRequest.of(0, 20), debtors.size.toLong())
        whenever(debtorService.findAll(0, 20, null, null, null)).thenReturn(page)

        mockMvc.perform(get("/api/v1/debtors"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.total").value(2))
            .andExpect(jsonPath("$.debtors[0].name").value("John Doe"))
            .andExpect(jsonPath("$.debtors[1].name").value("Jane Smith"))
    }

    @Test
    fun `should get debtor by id`() {
        val id = UUID.randomUUID()
        val debtor = DebtorResponse(id, "John Doe", "ACC001", "BANK001", null, null, Instant.now(), Instant.now())
        whenever(debtorService.findById(id)).thenReturn(debtor)

        mockMvc.perform(get("/api/v1/debtors/{id}", id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.name").value("John Doe"))
    }

    @Test
    fun `should create debtor`() {
        val request = DebtorCreateRequest(name = "John Doe", accountNumber = "ACC001", bankCode = "BANK001")
        val response = DebtorResponse(UUID.randomUUID(), "John Doe", "ACC001", "BANK001", null, null, Instant.now(), Instant.now())
        whenever(debtorService.create(any())).thenReturn(response)

        mockMvc.perform(
            post("/api/v1/debtors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("John Doe"))
    }

    @Test
    fun `should return bad request when validation fails`() {
        val request = DebtorCreateRequest(name = "", accountNumber = "", bankCode = "")

        mockMvc.perform(
            post("/api/v1/debtors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should delete debtor`() {
        val id = UUID.randomUUID()
        doNothing().whenever(debtorService).delete(id)

        mockMvc.perform(delete("/api/v1/debtors/{id}", id))
            .andExpect(status().isNoContent)
    }
}
