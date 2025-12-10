package com.example.paymentecho.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

/**
 * JSON:API compliant response wrapper
 * Follows JSON:API specification: https://jsonapi.org/
 */
@Schema(description = "JSON:API compliant response")
data class JsonApiResponse<T>(
    @Schema(description = "Response data (can be single object or array)")
    val data: T? = null,
    
    @Schema(description = "Response metadata")
    val meta: JsonApiMeta? = null,
    
    @Schema(description = "Response links")
    val links: JsonApiLinks? = null
)

@Schema(description = "JSON:API data object")
data class JsonApiData<T>(
    @Schema(description = "Resource ID", example = "42")
    val id: String,
    
    @Schema(description = "Resource type", example = "payment")
    val type: String,
    
    @Schema(description = "Resource attributes")
    val attributes: T,
    
    @Schema(description = "Resource links")
    val links: JsonApiResourceLinks? = null
)

@Schema(description = "JSON:API metadata")
data class JsonApiMeta(
    @Schema(description = "Request ID", example = "req_abc123")
    val requestId: String? = null,
    
    @Schema(description = "API version", example = "v1")
    val version: String = "v1",
    
    @Schema(description = "Timestamp when response was generated")
    val generatedAt: Instant = Instant.now(),
    
    @Schema(description = "Total count (for list responses)")
    val total: Int? = null
)

@Schema(description = "JSON:API links")
data class JsonApiLinks(
    @Schema(description = "Link to the resource itself", example = "/api/payments/42")
    val self: String? = null,
    
    @Schema(description = "Link to the first page")
    val first: String? = null,
    
    @Schema(description = "Link to the last page")
    val last: String? = null,
    
    @Schema(description = "Link to the next page")
    val next: String? = null,
    
    @Schema(description = "Link to the previous page")
    val prev: String? = null
)

@Schema(description = "JSON:API resource links")
data class JsonApiResourceLinks(
    @Schema(description = "Link to the resource itself", example = "/api/payments/42")
    val self: String
)
