package com.example.paymentecho.dto.response

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Common API response wrapper for consistent response structure
 */
@Schema(description = "Standard API response wrapper")
data class ApiResponse<T>(
    @Schema(description = "Response status", example = "success")
    val status: String = "success",
    
    @Schema(description = "Response message", example = "Operation completed successfully")
    val message: String = "Operation completed successfully",
    
    @Schema(description = "Response data")
    val data: T? = null,
    
    @Schema(description = "Response metadata")
    val metadata: ResponseMetadata? = null
)

@Schema(description = "Response metadata")
data class ResponseMetadata(
    @Schema(description = "Total count of items", example = "10")
    val total: Int? = null,
    
    @Schema(description = "Page number (for pagination)", example = "1")
    val page: Int? = null,
    
    @Schema(description = "Page size (for pagination)", example = "20")
    val pageSize: Int? = null
)
