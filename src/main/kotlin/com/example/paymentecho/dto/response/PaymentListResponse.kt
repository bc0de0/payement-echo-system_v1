package com.example.paymentecho.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "List of payments response")
data class PaymentListResponse(
    @Schema(description = "List of payments")
    val payments: List<PaymentResponse>,

    @Schema(description = "Total count of payments")
    val total: Int
)
