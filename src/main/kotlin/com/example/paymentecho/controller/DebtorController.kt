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
import java.util.Locale

@RestController
@RequestMapping("/api/v1/debtors")
@Tag(name = "Debtors", description = "Debtor management API")
class DebtorController(private val service: DebtorService) {

    @GetMapping
    @Operation(
        summary = "Get all debtors",
        description = "Retrieve a paginated list of all debtors. Supports Accept-Language header for i18n (default: en)."
    )
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
        @RequestParam(required = false) bankCode: String?,
        
        @Parameter(description = "Language preference (en, es, fr, de, hi, bn, ta, te, kn, ru, zh). Default: hi", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<DebtorListResponse> {
        val debtors = service.findAll(page, size, sort, name, bankCode)
        return ResponseEntity.ok(DebtorListResponse(debtors = debtors.content, total = debtors.totalElements.toInt()))
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get debtor by ID",
        description = "Retrieve a specific debtor by its UUID. Supports Accept-Language header for i18n (default: en)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Debtor found"),
            ApiResponse(responseCode = "404", description = "Debtor not found", content = [Content()])
        ]
    )
    fun getById(
        @Parameter(description = "Debtor UUID", required = true)
        @PathVariable id: UUID,
        
        @Parameter(description = "Language preference (en, es, fr, de, hi, bn, ta, te, kn, ru, zh). Default: hi", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<DebtorResponse> {
        return ResponseEntity.ok(service.findById(id))
    }

    @PostMapping
    @Operation(
        summary = "Create a new debtor",
        description = "Create a new debtor with the provided details. Sample data examples:\n" +
                "- John Doe: name=\"John Doe\", accountNumber=\"DEB001111111\", bankCode=\"BANK001\"\n" +
                "- Jane Smith: name=\"Jane Smith\", accountNumber=\"DEB002222222\", bankCode=\"BANK002\"\n\n" +
                "Supports Accept-Language header for i18n (default: en)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Debtor created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    fun create(
        @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Debtor creation request. Example with sample data:\n" +
                    "{\n" +
                    "  \"name\": \"John Doe\",\n" +
                    "  \"accountNumber\": \"DEB001111111\",\n" +
                    "  \"bankCode\": \"BANK001\",\n" +
                    "  \"email\": \"john.doe@email.com\",\n" +
                    "  \"address\": \"100 Main Street, Boston, MA 02101\"\n" +
                    "}",
            required = true,
            content = [Content(
                mediaType = "application/json",
                examples = [
                    io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "John Doe",
                        value = """{"name": "John Doe", "accountNumber": "DEB001111111", "bankCode": "BANK001", "email": "john.doe@email.com", "address": "100 Main Street, Boston, MA 02101"}""",
                        summary = "Sample debtor 1"
                    ),
                    io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Jane Smith",
                        value = """{"name": "Jane Smith", "accountNumber": "DEB002222222", "bankCode": "BANK002", "email": "jane.smith@email.com", "address": "200 Oak Avenue, Seattle, WA 98101"}""",
                        summary = "Sample debtor 2"
                    )
                ]
            )]
        )
        @RequestBody request: DebtorCreateRequest,
        
        @Parameter(description = "Language preference (en, es, fr, de, hi, bn, ta, te, kn, ru, zh). Default: hi", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<DebtorResponse> {
        val created = service.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a debtor",
        description = "Delete a debtor by its UUID (soft delete). Supports Accept-Language header for i18n (default: en)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Debtor deleted successfully"),
            ApiResponse(responseCode = "404", description = "Debtor not found", content = [Content()])
        ]
    )
    fun delete(
        @Parameter(description = "Debtor UUID", required = true)
        @PathVariable id: UUID,
        
        @Parameter(description = "Language preference (en, es, fr, de, hi, bn, ta, te, kn, ru, zh). Default: hi", example = "hi")
        @RequestHeader(name = "Accept-Language", required = false, defaultValue = "hi") acceptLanguage: String
    ): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
