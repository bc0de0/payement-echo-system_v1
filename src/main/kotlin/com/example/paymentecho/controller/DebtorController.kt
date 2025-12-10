package com.example.paymentecho.controller

import com.example.paymentecho.dto.request.DebtorCreateRequest
import com.example.paymentecho.dto.response.DebtorListResponse
import com.example.paymentecho.dto.response.DebtorResponse
import com.example.paymentecho.service.DebtorService
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
@RequestMapping("/api/v1/debtors")
@Tag(name = "Debtors", description = "Debtor management API")
class DebtorController(private val service: DebtorService) {

    @GetMapping
    @Operation(summary = "Get all debtors", description = "Retrieve a paginated list of all debtors")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved debtors")
        ]
    )
    fun getAll(
        @Parameter(description = "Page number (0-indexed)", example = "0")
        @RequestParam(defaultValue = "0") page: Int,
        
        @Parameter(description = "Page size", example = "20")
        @RequestParam(defaultValue = "20") size: Int,
        
        @Parameter(description = "Sort field and direction", example = "name,asc")
        @RequestParam(required = false) sort: String?,
        
        @Parameter(description = "Filter by name (partial match, case-insensitive)", example = "Jane")
        @RequestParam(required = false) name: String?,
        
        @Parameter(description = "Filter by bank code", example = "BANK002")
        @RequestParam(required = false) bankCode: String?
    ): ResponseEntity<DebtorListResponse> {
        val debtors = service.findAll(page, size, sort, name, bankCode)
        return ResponseEntity.ok(DebtorListResponse(debtors = debtors.content, total = debtors.totalElements.toInt()))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get debtor by ID", description = "Retrieve a specific debtor by its UUID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Debtor found"),
            ApiResponse(responseCode = "404", description = "Debtor not found", content = [Content()])
        ]
    )
    fun getById(
        @Parameter(description = "Debtor UUID", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<DebtorResponse> {
        return ResponseEntity.ok(service.findById(id))
    }

    @PostMapping
    @Operation(summary = "Create a new debtor", description = "Create a new debtor with the provided details")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Debtor created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    fun create(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Debtor creation request", required = true)
        @RequestBody request: DebtorCreateRequest
    ): ResponseEntity<DebtorResponse> {
        val created = service.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a debtor", description = "Delete a debtor by its UUID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Debtor deleted successfully"),
            ApiResponse(responseCode = "404", description = "Debtor not found", content = [Content()])
        ]
    )
    fun delete(
        @Parameter(description = "Debtor UUID", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
