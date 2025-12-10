package com.example.paymentecho.service

import com.example.paymentecho.dto.request.CreditorCreateRequest
import com.example.paymentecho.dto.response.CreditorResponse
import com.example.paymentecho.exception.CreditorNotFoundException
import com.example.paymentecho.mapper.CreditorMapper
import com.example.paymentecho.repository.CreditorRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CreditorService(
    private val repo: CreditorRepository,
    private val mapper: CreditorMapper
) {

    private val logger = LoggerFactory.getLogger(CreditorService::class.java)

    fun findAll(
        page: Int, 
        size: Int, 
        sort: String?,
        name: String? = null,
        bankCode: String? = null
    ): Page<CreditorResponse> {
        logger.debug("Finding all creditors with page: {}, size: {}, sort: {}, filters: name={}, bankCode={}", page, size, sort, name, bankCode)
        val pageable = createPageable(page, size, sort)
        
        val creditors = when {
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
        
        return creditors.map { mapper.toResponse(it) }
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
    fun findById(id: UUID): CreditorResponse {
        logger.debug("Finding creditor with id: {}", id)
        val creditor = repo.findById(id).orElseThrow { CreditorNotFoundException(id) }
        if (creditor.deletedAt != null) {
            throw CreditorNotFoundException(id)
        }
        return mapper.toResponse(creditor)
    }

    @Transactional
    fun create(request: CreditorCreateRequest): CreditorResponse {
        logger.info("Creating creditor with name: {}", request.name)
        val creditor = mapper.toEntity(request)
        val saved = repo.save(creditor)
        logger.info("Creditor created with id: {}", saved.id)
        return mapper.toResponse(saved)
    }

    @Transactional
    fun delete(id: UUID) {
        logger.info("Soft deleting creditor with id: {}", id)
        val creditor = repo.findById(id).orElseThrow { CreditorNotFoundException(id) }
        val deletedCreditor = creditor.copy(deletedAt = java.time.Instant.now())
        repo.save(deletedCreditor)
        logger.info("Creditor soft deleted with id: {}", id)
    }
}
