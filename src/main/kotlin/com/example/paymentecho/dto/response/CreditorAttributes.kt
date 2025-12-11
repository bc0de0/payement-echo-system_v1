package com.example.paymentecho.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Creditor attributes")
data class CreditorAttributes(
    @Schema(description = "Creditor name", example = "John Doe")
    val name: String,

    @Schema(description = "Account number", example = "1234567890")
    val accountNumber: String,

    @Schema(description = "Bank code", example = "BANK001")
    val bankCode: String,

    @Schema(description = "Address", example = "123 Main St, City, Country")
    val address: String?,

    @Schema(description = "Email address", example = "john.doe@example.com")
    val email: String?,

    @Schema(description = "Creation timestamp")
    val createdAt: Instant,

    @Schema(description = "Last update timestamp")
    val updatedAt: Instant
)
