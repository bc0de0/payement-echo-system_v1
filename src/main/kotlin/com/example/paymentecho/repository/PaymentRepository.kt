package com.example.paymentecho.repository

import com.example.paymentecho.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface PaymentRepository : JpaRepository<Payment, UUID> {
    
    @Query("SELECT p FROM Payment p WHERE (:status IS NULL OR p.status = :status) AND (:currency IS NULL OR p.currency = :currency) AND (p.deletedAt IS NULL)")
    fun findByStatusAndCurrency(@Param("status") status: String?, @Param("currency") currency: String?): List<Payment>
    
    @Query("SELECT p FROM Payment p WHERE p.deletedAt IS NULL")
    fun findAllActive(): List<Payment>
    
    @Query("SELECT p FROM Payment p WHERE p.amount >= :minAmount AND p.amount <= :maxAmount AND (p.deletedAt IS NULL)")
    fun findByAmountRange(@Param("minAmount") minAmount: Double, @Param("maxAmount") maxAmount: Double): List<Payment>
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate AND (p.deletedAt IS NULL)")
    fun findByDateRange(@Param("startDate") startDate: Instant, @Param("endDate") endDate: Instant): List<Payment>
}
