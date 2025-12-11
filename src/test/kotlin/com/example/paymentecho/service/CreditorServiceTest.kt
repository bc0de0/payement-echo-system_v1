package com.example.paymentecho.service

import com.example.paymentecho.dto.request.CreditorCreateRequest
import com.example.paymentecho.exception.CreditorNotFoundException
import com.example.paymentecho.repository.CreditorRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CreditorServiceTest @Autowired constructor(
    private val creditorService: CreditorService,
    private val creditorRepository: CreditorRepository
) {

    @BeforeEach
    fun setUp() {
        creditorRepository.deleteAll()
    }

    @Test
    fun `should create creditor`() {
        val request = CreditorCreateRequest(
            name = "John Doe",
            accountNumber = "1234567890",
            bankCode = "BANK001",
            email = "john@example.com"
        )

        val response = creditorService.create(request)

        assertNotNull(response.id)
        assertEquals("John Doe", response.name)
        assertEquals("1234567890", response.accountNumber)
        assertEquals("BANK001", response.bankCode)
        assertEquals("john@example.com", response.email)
    }

    @Test
    fun `should find creditor by id`() {
        val request = CreditorCreateRequest(name = "Jane Smith", accountNumber = "ACC001", bankCode = "BANK001")
        val created = creditorService.create(request)

        val found = creditorService.findById(created.id)

        assertEquals(created.id, found.id)
        assertEquals("Jane Smith", found.name)
    }

    @Test
    fun `should throw exception when creditor not found`() {
        assertThrows(CreditorNotFoundException::class.java) {
            creditorService.findById(UUID.randomUUID())
        }
    }

    @Test
    fun `should find all creditors`() {
        creditorService.create(CreditorCreateRequest(name = "Creditor 1", accountNumber = "ACC001", bankCode = "BANK001"))
        creditorService.create(CreditorCreateRequest(name = "Creditor 2", accountNumber = "ACC002", bankCode = "BANK002"))

        val all = creditorService.findAll(0, 20, null)

        assertEquals(2, all.totalElements)
    }

    @Test
    fun `should delete creditor`() {
        val created = creditorService.create(CreditorCreateRequest(name = "To Delete", accountNumber = "ACC001", bankCode = "BANK001"))

        creditorService.delete(created.id)

        assertThrows(CreditorNotFoundException::class.java) {
            creditorService.findById(created.id)
        }
    }
}
