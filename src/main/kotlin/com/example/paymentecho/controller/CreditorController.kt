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
import java.util.Locale

@RestController
@RequestMapping("/api/v1/creditors")
@Tag(name = "Creditors", description = "Creditor management API")
class CreditorController(private val service: CreditorService) {

    @GetMapping
    @Operation(
        summary = "Get all creditors",
        description = "Retrieve a paginated list of all creditors. Supports Accept-Language header for i18n (default: en)."
    )
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
        @RequestParam(required = false) bankCode: String?,
        
        @Parameter(description = "Language preference (en, es, fr, de, hi, bn, ta, te, kn, ru, zh). Default: hi", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<CreditorListResponse> {
        val creditors = service.findAll(page, size, sort, name, bankCode)
        return ResponseEntity.ok(CreditorListResponse(creditors = creditors.content, total = creditors.totalElements.toInt()))
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get creditor by ID",
        description = "Retrieve a specific creditor by its UUID. Supports Accept-Language header for i18n (default: en)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Creditor found"),
            ApiResponse(responseCode = "404", description = "Creditor not found", content = [Content()])
        ]
    )
    fun getById(
        @Parameter(description = "Creditor UUID", required = true)
        @PathVariable id: UUID,
        
        @Parameter(description = "Language preference (en, es, fr, de, hi, bn, ta, te, kn, ru, zh). Default: hi", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<CreditorResponse> {
        return ResponseEntity.ok(service.findById(id))
    }

    @PostMapping
    @Operation(
        summary = "Create a new creditor",
        description = "Create a new creditor with the provided details. Sample data examples:\n" +
                "- Acme Corporation: name=\"Acme Corporation\", accountNumber=\"ACC001234567\", bankCode=\"BANK001\"\n" +
                "- Tech Solutions Inc: name=\"Tech Solutions Inc\", accountNumber=\"ACC002345678\", bankCode=\"BANK002\"\n\n" +
                "Supports Accept-Language header for i18n (default: en)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Creditor created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    fun create(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Creditor creation request. Example with sample data:\n" +
                    "{\n" +
                    "  \"name\": \"Acme Corporation\",\n" +
                    "  \"accountNumber\": \"ACC001234567\",\n" +
                    "  \"bankCode\": \"BANK001\",\n" +
                    "  \"email\": \"finance@acme.com\",\n" +
                    "  \"address\": \"123 Business Street, New York, NY 10001\"\n" +
                    "}",
            required = true,
            content = [Content(
                mediaType = "application/json",
                examples = [
                    io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Acme Corporation",
                        value = """{"name": "Acme Corporation", "accountNumber": "ACC001234567", "bankCode": "BANK001", "email": "finance@acme.com", "address": "123 Business Street, New York, NY 10001"}""",
                        summary = "Sample creditor 1"
                    ),
                    io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Tech Solutions Inc",
                        value = """{"name": "Tech Solutions Inc", "accountNumber": "ACC002345678", "bankCode": "BANK002", "email": "payments@techsolutions.com", "address": "456 Tech Avenue, San Francisco, CA 94102"}""",
                        summary = "Sample creditor 2"
                    )
                ]
            )]
        )
        @RequestBody request: CreditorCreateRequest,
        
        @Parameter(description = "Language preference (en, es, fr, de, hi, bn, ta, te, kn, ru, zh). Default: hi", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<CreditorResponse> {
        val created = service.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a creditor",
        description = "Delete a creditor by its UUID (soft delete). Supports Accept-Language header for i18n (default: en)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Creditor deleted successfully"),
            ApiResponse(responseCode = "404", description = "Creditor not found", content = [Content()])
        ]
    )
    fun delete(
        @Parameter(description = "Creditor UUID", required = true)
        @PathVariable id: UUID,
        
        @Parameter(description = "Language preference (en, es, fr, de, hi, bn, ta, te, kn, ru, zh). Default: hi", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
