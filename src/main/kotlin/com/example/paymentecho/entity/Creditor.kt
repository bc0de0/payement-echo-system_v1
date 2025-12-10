package com.example.paymentecho.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "creditors")
data class Creditor(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, name = "account_number")
    val accountNumber: String,

    @Column(nullable = false, name = "bank_code")
    val bankCode: String,

    @Column(nullable = true)
    val address: String? = null,

    @Column(nullable = true)
    val email: String? = null,

    @Column(nullable = false, name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false, name = "updated_at")
    val updatedAt: Instant = Instant.now(),

    @Column(nullable = true, name = "deleted_at")
    val deletedAt: Instant? = null
)
