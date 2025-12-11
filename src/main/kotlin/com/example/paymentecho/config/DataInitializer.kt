package com.example.paymentecho.config

import com.example.paymentecho.dto.request.CreditorCreateRequest
import com.example.paymentecho.dto.request.DebtorCreateRequest
import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.repository.CreditorRepository
import com.example.paymentecho.repository.DebtorRepository
import com.example.paymentecho.repository.PaymentRepository
import com.example.paymentecho.service.CreditorService
import com.example.paymentecho.service.DebtorService
import com.example.paymentecho.service.PaymentService
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
@ConditionalOnProperty(name = ["app.data.initialize"], havingValue = "true", matchIfMissing = true)
class DataInitializer(
    private val paymentService: PaymentService,
    private val creditorService: CreditorService,
    private val debtorService: DebtorService,
    private val paymentRepository: PaymentRepository,
    private val creditorRepository: CreditorRepository,
    private val debtorRepository: DebtorRepository
) {

    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    @PostConstruct
    fun initializeSampleData() {
        // Only initialize if database is empty
        val paymentCount = paymentRepository.findAll().size
        val creditorCount = creditorRepository.findAll().size
        val debtorCount = debtorRepository.findAll().size
        
        if (paymentCount > 0 || creditorCount > 0 || debtorCount > 0) {
            logger.info("Database already contains data. Skipping sample data initialization.")
            return
        }

        logger.info("Initializing sample data...")

        try {
            // Create Sample Creditors
            val creditor1 = creditorService.create(
                CreditorCreateRequest(
                    name = "Acme Corporation",
                    accountNumber = "ACC001234567",
                    bankCode = "BANK001",
                    email = "finance@acme.com",
                    address = "123 Business Street, New York, NY 10001"
                )
            )
            logger.info("Created creditor: {}", creditor1.name)

            val creditor2 = creditorService.create(
                CreditorCreateRequest(
                    name = "Tech Solutions Inc",
                    accountNumber = "ACC002345678",
                    bankCode = "BANK002",
                    email = "payments@techsolutions.com",
                    address = "456 Tech Avenue, San Francisco, CA 94102"
                )
            )
            logger.info("Created creditor: {}", creditor2.name)

            val creditor3 = creditorService.create(
                CreditorCreateRequest(
                    name = "Global Services Ltd",
                    accountNumber = "ACC003456789",
                    bankCode = "BANK003",
                    email = "accounts@globalservices.com",
                    address = "789 Global Plaza, London, UK"
                )
            )
            logger.info("Created creditor: {}", creditor3.name)

            // Create Sample Debtors
            val debtor1 = debtorService.create(
                DebtorCreateRequest(
                    name = "John Doe",
                    accountNumber = "DEB001111111",
                    bankCode = "BANK001",
                    email = "john.doe@email.com",
                    address = "100 Main Street, Boston, MA 02101"
                )
            )
            logger.info("Created debtor: {}", debtor1.name)

            val debtor2 = debtorService.create(
                DebtorCreateRequest(
                    name = "Jane Smith",
                    accountNumber = "DEB002222222",
                    bankCode = "BANK002",
                    email = "jane.smith@email.com",
                    address = "200 Oak Avenue, Seattle, WA 98101"
                )
            )
            logger.info("Created debtor: {}", debtor2.name)

            val debtor3 = debtorService.create(
                DebtorCreateRequest(
                    name = "Robert Johnson",
                    accountNumber = "DEB003333333",
                    bankCode = "BANK003",
                    email = "robert.j@email.com",
                    address = "300 Pine Road, Austin, TX 78701"
                )
            )
            logger.info("Created debtor: {}", debtor3.name)

            // Create Sample Payments
            val payment1 = paymentService.create(
                PaymentCreateRequest(
                    amount = 1500.00,
                    currency = "USD",
                    status = "RECEIVED",
                    creditorId = creditor1.id,
                    debtorId = debtor1.id
                )
            )
            logger.info("Created payment: {} {} from {} to {}", payment1.amount, payment1.currency, debtor1.name, creditor1.name)

            val payment2 = paymentService.create(
                PaymentCreateRequest(
                    amount = 2500.50,
                    currency = "EUR",
                    status = "PROCESSING",
                    creditorId = creditor2.id,
                    debtorId = debtor2.id
                )
            )
            logger.info("Created payment: {} {} from {} to {}", payment2.amount, payment2.currency, debtor2.name, creditor2.name)

            val payment3 = paymentService.create(
                PaymentCreateRequest(
                    amount = 3200.75,
                    currency = "GBP",
                    status = "COMPLETED",
                    creditorId = creditor3.id,
                    debtorId = debtor3.id
                )
            )
            logger.info("Created payment: {} {} from {} to {}", payment3.amount, payment3.currency, debtor3.name, creditor3.name)

            val payment4 = paymentService.create(
                PaymentCreateRequest(
                    amount = 500.00,
                    currency = "USD",
                    status = "RECEIVED",
                    creditorId = creditor1.id,
                    debtorId = debtor2.id
                )
            )
            logger.info("Created payment: {} {} from {} to {}", payment4.amount, payment4.currency, debtor2.name, creditor1.name)

            val payment5 = paymentService.create(
                PaymentCreateRequest(
                    amount = 750.25,
                    currency = "USD",
                    status = "FAILED",
                    creditorId = creditor2.id,
                    debtorId = debtor1.id
                )
            )
            logger.info("Created payment: {} {} from {} to {}", payment5.amount, payment5.currency, debtor1.name, creditor2.name)

            // Create payments without creditor/debtor
            val payment6 = paymentService.create(
                PaymentCreateRequest(
                    amount = 1000.00,
                    currency = "USD",
                    status = "RECEIVED"
                )
            )
            logger.info("Created standalone payment: {} {}", payment6.amount, payment6.currency)

            val payment7 = paymentService.create(
                PaymentCreateRequest(
                    amount = 2000.00,
                    currency = "EUR",
                    status = "PROCESSING"
                )
            )
            logger.info("Created standalone payment: {} {}", payment7.amount, payment7.currency)

            logger.info("Sample data initialization completed successfully!")
            logger.info("Created: 3 creditors, 3 debtors, 7 payments")

        } catch (e: Exception) {
            logger.error("Error initializing sample data", e)
        }
    }
}
