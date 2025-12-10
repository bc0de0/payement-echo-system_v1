package com.example.paymentecho.service

import com.example.paymentecho.dto.request.DebtorCreateRequest
import com.example.paymentecho.dto.response.DebtorResponse
import com.example.paymentecho.exception.DebtorNotFoundException
import com.example.paymentecho.mapper.DebtorMapper
import com.example.paymentecho.repository.DebtorRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DebtorService(
    private val repo: DebtorRepository,
    private val mapper: DebtorMapper
) {

    private val logger = LoggerFactory.getLogger(DebtorService::class.java)

    fun findAll(
        page: Int, 
        size: Int, 
        sort: String?,
        name: String? = null,
        bankCode: String? = null
    ): Page<DebtorResponse> {
        logger.debug("Finding all debtors with page: {}, size: {}, sort: {}, filters: name={}, bankCode={}", page, size, sort, name, bankCode)
        val pageable = createPageable(page, size, sort)
        
        val debtors = when {
            name != null || bankCode != null -> {
                val filtered = when {
                    name != null && bankCode != null -> {
                        repo.findByNameContainingIgnoreCase(name).filter { it.bankCode == bankCode }
                    }
                    name != null -> repo.findByNameContainingIgnoreCase(name)
                    bankCode != null -> repo.findByBankCode(bankCode)
                    else -> repo.findAllActive()
                }
                val start = (page * size).toLong().toInt().coerceAtLeast(0)
                val end = (start + size).coerceAtMost(filtered.size)
                val pagedList = if (start < filtered.size) {
                    filtered.subList(start, end)
                } else {
                    emptyList()
                }
                org.springframework.data.domain.PageImpl(pagedList, pageable, filtered.size.toLong())
            }
            else -> repo.findAll(pageable)
        }
        
        return debtors.map { mapper.toResponse(it) }
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
    fun findById(id: UUID): DebtorResponse {
        logger.debug("Finding debtor with id: {}", id)
        val debtor = repo.findById(id).orElseThrow { DebtorNotFoundException(id) }
        if (debtor.deletedAt != null) {
            throw DebtorNotFoundException(id)
        }
        return mapper.toResponse(debtor)
    }

    @Transactional
    fun create(request: DebtorCreateRequest): DebtorResponse {
        logger.info("Creating debtor with name: {}", request.name)
        val debtor = mapper.toEntity(request)
        val saved = repo.save(debtor)
        logger.info("Debtor created with id: {}", saved.id)
        return mapper.toResponse(saved)
    }

    @Transactional
    fun delete(id: UUID) {
        logger.info("Soft deleting debtor with id: {}", id)
        val debtor = repo.findById(id).orElseThrow { DebtorNotFoundException(id) }
        val deletedDebtor = debtor.copy(deletedAt = java.time.Instant.now())
        repo.save(deletedDebtor)
        logger.info("Debtor soft deleted with id: {}", id)
    }
}
