package com.example.paymentecho.repository

import com.example.paymentecho.entity.Creditor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class CreditorRepositoryTest @Autowired constructor(
    private val creditorRepository: CreditorRepository
) {

    @BeforeEach
    fun setUp() {
        creditorRepository.deleteAll()
    }

    @Test
    fun `should save creditor`() {
        val creditor = Creditor(
            name = "John Doe",
            accountNumber = "1234567890",
            bankCode = "BANK001"
        )

        val saved = creditorRepository.save(creditor)

        assertNotNull(saved.id)
        assertEquals("John Doe", saved.name)
        assertEquals("1234567890", saved.accountNumber)
        assertEquals("BANK001", saved.bankCode)
    }

    @Test
    fun `should find creditor by id`() {
        val creditor = Creditor(
            name = "Jane Smith",
            accountNumber = "0987654321",
            bankCode = "BANK002"
        )
        val saved = creditorRepository.save(creditor)

        val found = creditorRepository.findById(saved.id!!)

        assertTrue(found.isPresent)
        assertEquals(saved.id, found.get().id)
        assertEquals("Jane Smith", found.get().name)
    }

    @Test
    fun `should find all creditors`() {
        creditorRepository.save(Creditor(name = "Creditor 1", accountNumber = "ACC001", bankCode = "BANK001"))
        creditorRepository.save(Creditor(name = "Creditor 2", accountNumber = "ACC002", bankCode = "BANK002"))

        val all = creditorRepository.findAll()

        assertEquals(2, all.size)
    }
}
