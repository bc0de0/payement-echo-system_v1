package com.example.paymentecho.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

@Schema(description = "Debtor response")
data class DebtorResponse(
    @Schema(description = "Debtor ID", example = "123e4567-e89b-12d3-a456-426614174001")
    val id: UUID,

    @Schema(description = "Debtor name", example = "Jane Smith")
    val name: String,

    @Schema(description = "Account number", example = "0987654321")
    val accountNumber: String,

    @Schema(description = "Bank code", example = "BANK002")
    val bankCode: String,

    @Schema(description = "Address", example = "456 Oak Ave, City, Country")
    val address: String?,

    @Schema(description = "Email address", example = "jane.smith@example.com")
    val email: String?,

    @Schema(description = "Creation timestamp")
    val createdAt: Instant,

    @Schema(description = "Last update timestamp")
    val updatedAt: Instant
)
