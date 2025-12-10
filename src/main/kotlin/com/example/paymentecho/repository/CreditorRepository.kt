package com.example.paymentecho.repository

import com.example.paymentecho.entity.Creditor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CreditorRepository : JpaRepository<Creditor, UUID> {
    
    @Query("SELECT c FROM Creditor c WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND (c.deletedAt IS NULL)")
    fun findByNameContainingIgnoreCase(@Param("name") name: String?): List<Creditor>
    
    @Query("SELECT c FROM Creditor c WHERE (:bankCode IS NULL OR c.bankCode = :bankCode) AND (c.deletedAt IS NULL)")
    fun findByBankCode(@Param("bankCode") bankCode: String?): List<Creditor>
    
    @Query("SELECT c FROM Creditor c WHERE c.deletedAt IS NULL")
    fun findAllActive(): List<Creditor>
    
    @Query("SELECT c FROM Creditor c WHERE c.deletedAt IS NULL")
    override fun findAll(pageable: Pageable): Page<Creditor>
    
    @Query("SELECT c FROM Creditor c WHERE c.id = :id AND c.deletedAt IS NULL")
    fun findByIdAndDeletedAtIsNull(@Param("id") id: UUID): java.util.Optional<Creditor>
}
