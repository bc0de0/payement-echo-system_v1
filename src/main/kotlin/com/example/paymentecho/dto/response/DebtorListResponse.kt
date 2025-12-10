package com.example.paymentecho.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "List of debtors response")
data class DebtorListResponse(
    @Schema(description = "List of debtors")
    val debtors: List<DebtorResponse>,
    
    @Schema(description = "Total count of debtors")
    val total: Int
)
