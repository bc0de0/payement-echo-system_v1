package com.example.paymentecho.repository

import com.example.paymentecho.entity.Debtor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class DebtorRepositoryTest @Autowired constructor(
    private val debtorRepository: DebtorRepository
) {

    @BeforeEach
    fun setUp() {
        debtorRepository.deleteAll()
    }

    @Test
    fun `should save debtor`() {
        val debtor = Debtor(
            name = "John Doe",
            accountNumber = "1234567890",
            bankCode = "BANK001"
        )

        val saved = debtorRepository.save(debtor)

        assertNotNull(saved.id)
        assertEquals("John Doe", saved.name)
        assertEquals("1234567890", saved.accountNumber)
        assertEquals("BANK001", saved.bankCode)
    }

    @Test
    fun `should find debtor by id`() {
        val debtor = Debtor(
            name = "Jane Smith",
            accountNumber = "0987654321",
            bankCode = "BANK002"
        )
        val saved = debtorRepository.save(debtor)

        val found = debtorRepository.findById(saved.id!!)

        assertTrue(found.isPresent)
        assertEquals(saved.id, found.get().id)
        assertEquals("Jane Smith", found.get().name)
    }

    @Test
    fun `should find all debtors`() {
        debtorRepository.save(Debtor(name = "Debtor 1", accountNumber = "ACC001", bankCode = "BANK001"))
        debtorRepository.save(Debtor(name = "Debtor 2", accountNumber = "ACC002", bankCode = "BANK002"))

        val all = debtorRepository.findAll()

        assertEquals(2, all.size)
    }
}
