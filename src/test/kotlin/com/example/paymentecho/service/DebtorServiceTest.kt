package com.example.paymentecho.service

import com.example.paymentecho.dto.request.DebtorCreateRequest
import com.example.paymentecho.exception.DebtorNotFoundException
import com.example.paymentecho.repository.DebtorRepository
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
class DebtorServiceTest @Autowired constructor(
    private val debtorService: DebtorService,
    private val debtorRepository: DebtorRepository
) {

    @BeforeEach
    fun setUp() {
        debtorRepository.deleteAll()
    }

    @Test
    fun `should create debtor`() {
        val request = DebtorCreateRequest(
            name = "John Doe",
            accountNumber = "1234567890",
            bankCode = "BANK001",
            email = "john@example.com"
        )

        val response = debtorService.create(request)

        assertNotNull(response.id)
        assertEquals("John Doe", response.name)
        assertEquals("1234567890", response.accountNumber)
        assertEquals("BANK001", response.bankCode)
        assertEquals("john@example.com", response.email)
    }

    @Test
    fun `should find debtor by id`() {
        val request = DebtorCreateRequest(name = "Jane Smith", accountNumber = "ACC001", bankCode = "BANK001")
        val created = debtorService.create(request)

        val found = debtorService.findById(created.id)

        assertEquals(created.id, found.id)
        assertEquals("Jane Smith", found.name)
    }

    @Test
    fun `should throw exception when debtor not found`() {
        assertThrows(DebtorNotFoundException::class.java) {
            debtorService.findById(UUID.randomUUID())
        }
    }

    @Test
    fun `should find all debtors`() {
        debtorService.create(DebtorCreateRequest(name = "Debtor 1", accountNumber = "ACC001", bankCode = "BANK001"))
        debtorService.create(DebtorCreateRequest(name = "Debtor 2", accountNumber = "ACC002", bankCode = "BANK002"))

        val all = debtorService.findAll(0, 20, null)

        assertEquals(2, all.totalElements)
    }

    @Test
    fun `should delete debtor`() {
        val created = debtorService.create(DebtorCreateRequest(name = "To Delete", accountNumber = "ACC001", bankCode = "BANK001"))

        debtorService.delete(created.id)

        assertThrows(DebtorNotFoundException::class.java) {
            debtorService.findById(created.id)
        }
    }
}
