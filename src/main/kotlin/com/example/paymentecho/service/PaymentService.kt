package com.example.paymentecho.service

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
import com.example.paymentecho.dto.response.PaymentResponse
import com.example.paymentecho.entity.Payment
import com.example.paymentecho.exception.CreditorNotFoundException
import com.example.paymentecho.exception.DebtorNotFoundException
import com.example.paymentecho.exception.PaymentNotFoundException
import com.example.paymentecho.mapper.PaymentMapper
import com.example.paymentecho.repository.CreditorRepository
import com.example.paymentecho.repository.DebtorRepository
import com.example.paymentecho.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class PaymentService(
    private val repo: PaymentRepository,
    private val mapper: PaymentMapper,
    private val creditorRepository: CreditorRepository,
    private val debtorRepository: DebtorRepository
) {

    private val logger = LoggerFactory.getLogger(PaymentService::class.java)

    fun findAll(
        page: Int, 
        size: Int, 
        sort: String?,
        status: String? = null,
        currency: String? = null,
        minAmount: Double? = null,
        maxAmount: Double? = null,
        startDate: Instant? = null,
        endDate: Instant? = null
    ): Page<PaymentResponse> {
        logger.debug("Finding all payments with page: {}, size: {}, sort: {}, filters: status={}, currency={}", page, size, sort, status, currency)
        val pageable = createPageable(page, size, sort)
        
        // Apply filters - use Spring Data's pagination when possible
        val payments = when {
            status != null || currency != null -> {
                // For status/currency filtering, we need custom query
                val filtered = repo.findByStatusAndCurrency(status, currency)
                val start = (page * size).toLong().toInt().coerceAtLeast(0)
                val end = (start + size).coerceAtMost(filtered.size)
                val pagedList = if (start < filtered.size) {
                    filtered.subList(start, end)
                } else {
                    emptyList()
                }
                org.springframework.data.domain.PageImpl(pagedList, pageable, filtered.size.toLong())
            }
            minAmount != null && maxAmount != null -> {
                val filtered = repo.findByAmountRange(minAmount, maxAmount)
                val start = (page * size).toLong().toInt().coerceAtLeast(0)
                val end = (start + size).coerceAtMost(filtered.size)
                val pagedList = if (start < filtered.size) {
                    filtered.subList(start, end)
                } else {
                    emptyList()
                }
                org.springframework.data.domain.PageImpl(pagedList, pageable, filtered.size.toLong())
            }
            startDate != null && endDate != null -> {
                val filtered = repo.findByDateRange(startDate, endDate)
                val start = (page * size).toLong().toInt().coerceAtLeast(0)
                val end = (start + size).coerceAtMost(filtered.size)
                val pagedList = if (start < filtered.size) {
                    filtered.subList(start, end)
                } else {
                    emptyList()
                }
                org.springframework.data.domain.PageImpl(pagedList, pageable, filtered.size.toLong())
            }
            else -> {
                // No filters - use standard pagination
                repo.findAll(pageable)
            }
        }
        
        return payments.map { mapper.toResponse(it) }
    }

    private fun createPageable(page: Int, size: Int, sort: String?): Pageable {
        return if (sort != null && sort.contains(",")) {
            val parts = sort.split(",")
            val direction = if (parts.size > 1 && parts[1].trim().equals("desc", ignoreCase = true)) {
                Sort.Direction.DESC
            } else {
                Sort.Direction.ASC
            }
            PageRequest.of(page, size, Sort.by(direction, parts[0].trim()))
        } else {
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        }
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): PaymentResponse {
        logger.debug("Finding payment with id: {}", id)
        val payment = repo.findById(id).orElseThrow { PaymentNotFoundException(id) }
        if (payment.deletedAt != null) {
            throw PaymentNotFoundException(id)
        }
        return mapper.toResponse(payment)
    }

    @Transactional
    fun create(request: PaymentCreateRequest): PaymentResponse {
        logger.info("Creating payment with amount: {}, currency: {}, status: {}", request.amount, request.currency, request.status)
        
        val creditor = request.creditorId?.let {
            creditorRepository.findById(it).orElseThrow { CreditorNotFoundException(it) }
        }
        
        val debtor = request.debtorId?.let {
            debtorRepository.findById(it).orElseThrow { DebtorNotFoundException(it) }
        }
        
        val payment = mapper.toEntity(request, creditor, debtor)
        val saved = repo.save(payment)
        logger.info("Payment created with id: {}", saved.id)
        return mapper.toResponse(saved)
    }

    @Transactional
    fun echo(request: PaymentEchoRequest): PaymentResponse {
        logger.info("Echoing payment with amount: {}, currency: {}, status: {}", request.amount, request.currency, request.status)
        
        val creditor = request.creditorId?.let {
            creditorRepository.findById(it).orElseThrow { CreditorNotFoundException(it) }
        }
        
        val debtor = request.debtorId?.let {
            debtorRepository.findById(it).orElseThrow { DebtorNotFoundException(it) }
        }
        
        val payment = mapper.toEntity(request, creditor, debtor)
        val saved = repo.save(payment)
        logger.info("Payment echoed and saved with id: {}", saved.id)
        return mapper.toResponse(saved)
    }
}
