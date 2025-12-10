package com.example.paymentecho.controller

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
import com.example.paymentecho.dto.response.PaymentListResponse
import com.example.paymentecho.dto.response.PaymentResponse
import com.example.paymentecho.service.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment management API")
class PaymentController(private val service: PaymentService) {

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieve a paginated list of all payments")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved payments")
        ]
    )
    fun getAll(
        @Parameter(description = "Page number (0-indexed)", example = "0")
        @RequestParam(defaultValue = "0") page: Int,
        
        @Parameter(description = "Page size", example = "20")
        @RequestParam(defaultValue = "20") size: Int,
        
        @Parameter(description = "Sort field and direction", example = "createdAt,desc")
        @RequestParam(required = false) sort: String?,
        
        @Parameter(description = "Filter by payment status", example = "RECEIVED")
        @RequestParam(required = false) status: String?,
        
        @Parameter(description = "Filter by currency", example = "USD")
        @RequestParam(required = false) currency: String?,
        
        @Parameter(description = "Minimum amount filter", example = "100.0")
        @RequestParam(required = false) minAmount: Double?,
        
        @Parameter(description = "Maximum amount filter", example = "1000.0")
        @RequestParam(required = false) maxAmount: Double?,
        
        @Parameter(description = "Start date filter (ISO format)", example = "2025-01-01T00:00:00Z")
        @RequestParam(required = false) startDate: java.time.Instant?,
        
        @Parameter(description = "End date filter (ISO format)", example = "2025-12-31T23:59:59Z")
        @RequestParam(required = false) endDate: java.time.Instant?
    ): ResponseEntity<PaymentListResponse> {
        val payments = service.findAll(page, size, sort, status, currency, minAmount, maxAmount, startDate, endDate)
        return ResponseEntity.ok(PaymentListResponse(payments = payments.content, total = payments.totalElements.toInt()))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieve a specific payment by its UUID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Payment found"),
            ApiResponse(responseCode = "404", description = "Payment not found", content = [Content()])
        ]
    )
    fun getById(
        @Parameter(description = "Payment UUID", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<PaymentResponse> {
        return ResponseEntity.ok(service.findById(id))
    }

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Create a new payment with the provided details")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Payment created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    fun create(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payment creation request", required = true)
        @RequestBody request: PaymentCreateRequest
    ): ResponseEntity<PaymentResponse> {
        val created = service.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PostMapping("/echo")
    @Operation(summary = "Echo a payment", description = "Echo a payment (returns the same payment data and saves it)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Payment echoed successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    fun echo(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payment echo request", required = true)
        @RequestBody request: PaymentEchoRequest
    ): ResponseEntity<PaymentResponse> {
        return ResponseEntity.ok(service.echo(request))
    }
}

