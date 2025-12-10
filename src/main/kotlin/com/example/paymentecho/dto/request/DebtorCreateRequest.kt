package com.example.paymentecho.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "Request to create a new debtor")
data class DebtorCreateRequest(
    @field:NotBlank(message = "Name is required")
    @Schema(description = "Debtor name", example = "Jane Smith", required = true)
    val name: String,

    @field:NotBlank(message = "Account number is required")
    @Schema(description = "Account number", example = "0987654321", required = true)
    val accountNumber: String,

    @field:NotBlank(message = "Bank code is required")
    @Schema(description = "Bank code", example = "BANK002", required = true)
    val bankCode: String,

    @Schema(description = "Address", example = "456 Oak Ave, City, Country")
    val address: String? = null,

    @field:Email(message = "Email must be valid")
    @Schema(description = "Email address", example = "jane.smith@example.com")
    val email: String? = null
)
