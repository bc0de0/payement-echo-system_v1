package com.example.paymentecho.controller

import com.example.paymentecho.dto.request.CreditorCreateRequest
import com.example.paymentecho.dto.response.CreditorListResponse
import com.example.paymentecho.dto.response.CreditorResponse
import com.example.paymentecho.service.CreditorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/creditors")
@Tag(name = "Creditors", description = "Creditor management API")
class CreditorController(private val service: CreditorService) {

    @GetMapping
    @Operation(summary = "Get all creditors", description = "Retrieve a paginated list of all creditors")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved creditors")
        ]
    )
    fun getAll(
        @Parameter(description = "Page number (0-indexed)", example = "0")
        @RequestParam(defaultValue = "0") page: Int,
        
        @Parameter(description = "Page size", example = "20")
        @RequestParam(defaultValue = "20") size: Int,
        
        @Parameter(description = "Sort field and direction", example = "name,asc")
        @RequestParam(required = false) sort: String?,
        
        @Parameter(description = "Filter by name (partial match, case-insensitive)", example = "John")
        @RequestParam(required = false) name: String?,
        
        @Parameter(description = "Filter by bank code", example = "BANK001")
        @RequestParam(required = false) bankCode: String?
    ): ResponseEntity<CreditorListResponse> {
        val creditors = service.findAll(page, size, sort, name, bankCode)
        return ResponseEntity.ok(CreditorListResponse(creditors = creditors.content, total = creditors.totalElements.toInt()))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get creditor by ID", description = "Retrieve a specific creditor by its UUID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Creditor found"),
            ApiResponse(responseCode = "404", description = "Creditor not found", content = [Content()])
        ]
    )
    fun getById(
        @Parameter(description = "Creditor UUID", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<CreditorResponse> {
        return ResponseEntity.ok(service.findById(id))
    }

    @PostMapping
    @Operation(summary = "Create a new creditor", description = "Create a new creditor with the provided details")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Creditor created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    fun create(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Creditor creation request", required = true)
        @RequestBody request: CreditorCreateRequest
    ): ResponseEntity<CreditorResponse> {
        val created = service.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a creditor", description = "Delete a creditor by its UUID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Creditor deleted successfully"),
            ApiResponse(responseCode = "404", description = "Creditor not found", content = [Content()])
        ]
    )
    fun delete(
        @Parameter(description = "Creditor UUID", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
