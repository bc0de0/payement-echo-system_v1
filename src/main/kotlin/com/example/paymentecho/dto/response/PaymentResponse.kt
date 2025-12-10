package com.example.paymentecho.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

@Schema(description = "Payment response")
data class PaymentResponse(
    @Schema(description = "Payment ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,

    @Schema(description = "Payment amount", example = "100.50")
    val amount: Double,

    @Schema(description = "Currency code", example = "USD")
    val currency: String,

    @Schema(description = "Payment status", example = "RECEIVED")
    val status: String,

    @Schema(description = "Creation timestamp")
    val createdAt: Instant,

    @Schema(description = "Creditor ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val creditorId: UUID? = null,

    @Schema(description = "Debtor ID", example = "123e4567-e89b-12d3-a456-426614174001")
    val debtorId: UUID? = null
)
