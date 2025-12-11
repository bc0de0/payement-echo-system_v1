package com.example.paymentecho.service

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
import com.example.paymentecho.entity.Creditor
import com.example.paymentecho.entity.Debtor
import com.example.paymentecho.exception.CreditorNotFoundException
import com.example.paymentecho.exception.DebtorNotFoundException
import com.example.paymentecho.exception.PaymentNotFoundException
import com.example.paymentecho.mapper.PaymentMapper
import com.example.paymentecho.repository.CreditorRepository
import com.example.paymentecho.repository.DebtorRepository
import com.example.paymentecho.repository.PaymentRepository
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
class PaymentServiceTest @Autowired constructor(
    private val paymentService: PaymentService,
    private val paymentRepository: PaymentRepository,
    private val creditorRepository: CreditorRepository,
    private val debtorRepository: DebtorRepository
) {

    @BeforeEach
    fun setUp() {
        paymentRepository.deleteAll()
        creditorRepository.deleteAll()
        debtorRepository.deleteAll()
    }

    @Test
    fun `should create payment`() {
        val request = PaymentCreateRequest(
            amount = 100.50,
            currency = "USD",
            status = "RECEIVED"
        )

        val response = paymentService.create(request)

        assertNotNull(response.id)
        assertEquals(100.50, response.amount)
        assertEquals("USD", response.currency)
        assertEquals("RECEIVED", response.status)
    }

    @Test
    fun `should create payment with creditor and debtor`() {
        val creditor = creditorRepository.save(Creditor(name = "Creditor", accountNumber = "ACC001", bankCode = "BANK001"))
        val debtor = debtorRepository.save(Debtor(name = "Debtor", accountNumber = "ACC002", bankCode = "BANK002"))

        val request = PaymentCreateRequest(
            amount = 200.0,
            currency = "EUR",
            status = "PROCESSING",
            creditorId = creditor.id,
            debtorId = debtor.id
        )

        val response = paymentService.create(request)

        assertNotNull(response.id)
        assertEquals(creditor.id, response.creditorId)
        assertEquals(debtor.id, response.debtorId)
    }

    @Test
    fun `should throw exception when creditor not found`() {
        val request = PaymentCreateRequest(
            amount = 100.0,
            currency = "USD",
            status = "RECEIVED",
            creditorId = UUID.randomUUID()
        )

        assertThrows(CreditorNotFoundException::class.java) {
            paymentService.create(request)
        }
    }

    @Test
    fun `should throw exception when debtor not found`() {
        val request = PaymentCreateRequest(
            amount = 100.0,
            currency = "USD",
            status = "RECEIVED",
            debtorId = UUID.randomUUID()
        )

        assertThrows(DebtorNotFoundException::class.java) {
            paymentService.create(request)
        }
    }

    @Test
    fun `should find payment by id`() {
        val request = PaymentCreateRequest(amount = 100.0, currency = "USD", status = "RECEIVED")
        val created = paymentService.create(request)

        val found = paymentService.findById(created.id)

        assertEquals(created.id, found.id)
        assertEquals(100.0, found.amount)
    }

    @Test
    fun `should throw exception when payment not found`() {
        assertThrows(PaymentNotFoundException::class.java) {
            paymentService.findById(UUID.randomUUID())
        }
    }

    @Test
    fun `should find all payments`() {
        paymentService.create(PaymentCreateRequest(amount = 100.0, currency = "USD", status = "RECEIVED"))
        paymentService.create(PaymentCreateRequest(amount = 200.0, currency = "EUR", status = "PROCESSING"))

        val all = paymentService.findAll(0, 20, null)

        assertEquals(2, all.totalElements)
    }

    @Test
    fun `should echo payment`() {
        val request = PaymentEchoRequest(
            amount = 150.75,
            currency = "GBP",
            status = "COMPLETED"
        )

        val response = paymentService.echo(request)

        assertNotNull(response.id)
        assertEquals(150.75, response.amount)
        assertEquals("GBP", response.currency)
        assertEquals("COMPLETED", response.status)
    }
}
