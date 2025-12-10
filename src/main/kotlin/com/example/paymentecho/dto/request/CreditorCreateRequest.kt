package com.example.paymentecho.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "Request to create a new creditor")
data class CreditorCreateRequest(
    @field:NotBlank(message = "Name is required")
    @Schema(description = "Creditor name", example = "John Doe", required = true)
    val name: String,

    @field:NotBlank(message = "Account number is required")
    @Schema(description = "Account number", example = "1234567890", required = true)
    val accountNumber: String,

    @field:NotBlank(message = "Bank code is required")
    @Schema(description = "Bank code", example = "BANK001", required = true)
    val bankCode: String,

    @Schema(description = "Address", example = "123 Main St, City, Country")
    val address: String? = null,

    @field:Email(message = "Email must be valid")
    @Schema(description = "Email address", example = "john.doe@example.com")
    val email: String? = null
)
