package com.example.paymentecho.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(
    description = "Request to create a new creditor",
    example = """{
  "name": "Acme Corporation",
  "accountNumber": "ACC001234567",
  "bankCode": "BANK001",
  "email": "finance@acme.com",
  "address": "123 Business Street, New York, NY 10001"
}"""
)
data class CreditorCreateRequest(
    @field:NotBlank(message = "Name is required")
    @Schema(description = "Creditor name", example = "Acme Corporation", required = true)
    val name: String,

    @field:NotBlank(message = "Account number is required")
    @Schema(description = "Account number", example = "ACC001234567", required = true)
    val accountNumber: String,

    @field:NotBlank(message = "Bank code is required")
    @Schema(description = "Bank code", example = "BANK001", required = true)
    val bankCode: String,

    @Schema(description = "Address (optional)", example = "123 Business Street, New York, NY 10001")
    val address: String? = null,

    @field:Email(message = "Email must be valid")
    @Schema(description = "Email address (optional)", example = "finance@acme.com")
    val email: String? = null
)
