package com.example.paymentecho.repository

import com.example.paymentecho.entity.Debtor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DebtorRepository : JpaRepository<Debtor, UUID> {
    
    @Query("SELECT d FROM Debtor d WHERE (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND (d.deletedAt IS NULL)")
    fun findByNameContainingIgnoreCase(@Param("name") name: String?): List<Debtor>
    
    @Query("SELECT d FROM Debtor d WHERE (:bankCode IS NULL OR d.bankCode = :bankCode) AND (d.deletedAt IS NULL)")
    fun findByBankCode(@Param("bankCode") bankCode: String?): List<Debtor>
    
    @Query("SELECT d FROM Debtor d WHERE d.deletedAt IS NULL")
    fun findAllActive(): List<Debtor>
    
    @Query("SELECT d FROM Debtor d WHERE d.deletedAt IS NULL")
    override fun findAll(pageable: Pageable): Page<Debtor>
    
    @Query("SELECT d FROM Debtor d WHERE d.id = :id AND d.deletedAt IS NULL")
    fun findByIdAndDeletedAtIsNull(@Param("id") id: UUID): java.util.Optional<Debtor>
}
