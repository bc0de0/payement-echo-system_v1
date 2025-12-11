package com.example.paymentecho.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Validation error response")
data class ValidationErrorResponse(
    @Schema(description = "Timestamp when error occurred")
    val timestamp: Instant,

    @Schema(description = "HTTP status code", example = "400")
    val status: Int,

    @Schema(description = "Error type", example = "Validation Failed")
    val error: String,

    @Schema(description = "Error message", example = "Validation failed")
    val message: String,

    @Schema(description = "Request path", example = "/api/payments")
    val path: String,

    @Schema(description = "Field validation errors")
    val fieldErrors: Map<String, String>
)
