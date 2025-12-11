package com.example.paymentecho.repository

import com.example.paymentecho.entity.Payment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@DataJpaTest
@ActiveProfiles("test")
class PaymentRepositoryTest @Autowired constructor(
    private val paymentRepository: PaymentRepository
) {

    @BeforeEach
    fun setUp() {
        paymentRepository.deleteAll()
    }

    @Test
    fun `should save payment`() {
        val payment = Payment(
            amount = 100.50,
            currency = "USD",
            status = "RECEIVED",
            createdAt = Instant.now()
        )

        val saved = paymentRepository.save(payment)

        assertNotNull(saved.id)
        assertEquals(100.50, saved.amount)
        assertEquals("USD", saved.currency)
        assertEquals("RECEIVED", saved.status)
    }

    @Test
    fun `should find payment by id`() {
        val payment = Payment(
            amount = 200.75,
            currency = "EUR",
            status = "PROCESSING",
            createdAt = Instant.now()
        )
        val saved = paymentRepository.save(payment)

        val found = paymentRepository.findById(saved.id!!)

        assertTrue(found.isPresent)
        assertEquals(saved.id, found.get().id)
        assertEquals(200.75, found.get().amount)
    }

    @Test
    fun `should find all payments`() {
        paymentRepository.save(Payment(amount = 100.0, currency = "USD", status = "RECEIVED", createdAt = Instant.now()))
        paymentRepository.save(Payment(amount = 200.0, currency = "EUR", status = "PROCESSING", createdAt = Instant.now()))

        val all = paymentRepository.findAll()

        assertEquals(2, all.size)
    }

    @Test
    fun `should delete payment`() {
        val payment = paymentRepository.save(Payment(amount = 100.0, currency = "USD", status = "RECEIVED", createdAt = Instant.now()))
        val id = payment.id!!

        paymentRepository.deleteById(id)

        assertFalse(paymentRepository.existsById(id))
    }
}
