package com.example.paymentecho.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import java.util.*

@Schema(description = "Request to echo a payment (returns the same payment data)")
data class PaymentEchoRequest(
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @field:DecimalMax(value = "999999999.99", message = "Amount exceeds maximum allowed")
    @Schema(description = "Payment amount", example = "100.50", required = true)
    val amount: Double,

    @field:NotBlank(message = "Currency is required")
    @field:Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code")
    @Schema(description = "Currency code (ISO 4217)", example = "USD", required = true)
    val currency: String,

    @field:NotBlank(message = "Status is required")
    @field:Pattern(regexp = "^(RECEIVED|PROCESSING|COMPLETED|FAILED)$", message = "Status must be one of: RECEIVED, PROCESSING, COMPLETED, FAILED")
    @Schema(description = "Payment status", example = "RECEIVED", required = true)
    val status: String,

    @Schema(description = "Creditor ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val creditorId: UUID? = null,

    @Schema(description = "Debtor ID", example = "123e4567-e89b-12d3-a456-426614174001")
    val debtorId: UUID? = null
)
