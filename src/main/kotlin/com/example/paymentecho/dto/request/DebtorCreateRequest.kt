package com.example.paymentecho.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(
    description = "Request to create a new debtor",
    example = """{
  "name": "John Doe",
  "accountNumber": "DEB001111111",
  "bankCode": "BANK001",
  "email": "john.doe@email.com",
  "address": "100 Main Street, Boston, MA 02101"
}"""
)
data class DebtorCreateRequest(
    @field:NotBlank(message = "Name is required")
    @Schema(description = "Debtor name", example = "John Doe", required = true)
    val name: String,

    @field:NotBlank(message = "Account number is required")
    @Schema(description = "Account number", example = "DEB001111111", required = true)
    val accountNumber: String,

    @field:NotBlank(message = "Bank code is required")
    @Schema(description = "Bank code", example = "BANK001", required = true)
    val bankCode: String,

    @Schema(description = "Address (optional)", example = "100 Main Street, Boston, MA 02101")
    val address: String? = null,

    @field:Email(message = "Email must be valid")
    @Schema(description = "Email address (optional)", example = "john.doe@email.com")
    val email: String? = null
)
