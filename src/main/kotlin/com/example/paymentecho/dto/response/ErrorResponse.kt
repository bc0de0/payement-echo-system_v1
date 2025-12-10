package com.example.paymentecho.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Error response")
data class ErrorResponse(
    @Schema(description = "Timestamp when error occurred")
    val timestamp: Instant,

    @Schema(description = "HTTP status code", example = "400")
    val status: Int,

    @Schema(description = "Error type", example = "Bad Request")
    val error: String,

    @Schema(description = "Error message", example = "Validation failed")
    val message: String,

    @Schema(description = "Request path", example = "/api/payments")
    val path: String,

    @Schema(description = "Additional error details")
    val details: Map<String, Any>? = null
)
