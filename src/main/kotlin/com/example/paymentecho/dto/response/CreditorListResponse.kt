package com.example.paymentecho.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "List of creditors response")
data class CreditorListResponse(
    @Schema(description = "List of creditors")
    val creditors: List<CreditorResponse>,
    
    @Schema(description = "Total count of creditors")
    val total: Int
)
