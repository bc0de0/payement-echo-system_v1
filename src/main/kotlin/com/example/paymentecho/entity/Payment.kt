package com.example.paymentecho.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "payments")
data class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val amount: Double = 0.0,

    @Column(nullable = false)
    val currency: String = "USD",

    @Column(nullable = false)
    val status: String = "RECEIVED",

    @Column(nullable = false, name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(nullable = true, name = "deleted_at")
    val deletedAt: Instant? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creditor_id")
    val creditor: Creditor? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor_id")
    val debtor: Debtor? = null
)
