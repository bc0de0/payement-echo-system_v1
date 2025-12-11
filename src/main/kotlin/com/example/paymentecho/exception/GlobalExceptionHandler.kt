package com.example.paymentecho.exception

import com.example.paymentecho.dto.response.ErrorResponse
import com.example.paymentecho.dto.response.ValidationErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.LocaleResolver
import java.time.Instant
import java.util.*

/**
 * Global exception handler that provides localized error messages for all exceptions.
 * 
 * This handler intercepts all exceptions thrown by controllers and returns standardized,
 * localized error responses based on the Accept-Language header.
 * 
 * Supported languages: hi (default), en, es, fr, de, bn, ta, te, kn, ru, zh
 * 
 * @param messageSource The message source for retrieving localized messages
 * @param localeResolver The locale resolver for determining the request locale
 */
@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: MessageSource,
    private val localeResolver: LocaleResolver
) {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    /**
     * Resolves the locale from the request, falling back to LocaleContextHolder or default (Hindi).
     * 
     * @param request The web request
     * @return The resolved locale, defaulting to Hindi if resolution fails
     */
    private fun getLocale(request: WebRequest): Locale {
        return if (request is ServletWebRequest) {
            try {
                localeResolver.resolveLocale(request.request)
            } catch (e: Exception) {
                logger.warn("Failed to resolve locale from request, using default: {}", e.message)
                Locale.forLanguageTag("hi") // Default to Hindi
            }
        } else {
            // Try to get from LocaleContextHolder as fallback
            try {
                org.springframework.context.i18n.LocaleContextHolder.getLocale()
            } catch (e: Exception) {
                Locale.forLanguageTag("hi") // Default to Hindi
            }
        }
    }

    @ExceptionHandler(PaymentNotFoundException::class)
    fun handlePaymentNotFoundException(ex: PaymentNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Payment not found: {}", ex.message)
        val locale = getLocale(request)
        logger.debug("Resolved locale: {} for request: {}", locale, request.getDescription(false))
        val message = messageSource.getMessage(
            "payment.not.found",
            arrayOf(ex.id.toString()),
            "Payment not found with id: ${ex.id}",
            locale
        ) ?: "Payment not found with id: ${ex.id}"
        val errorMessage = messageSource.getMessage("error.not.found", null, "Not Found", locale) ?: "Not Found"
        
        val errorResponse = ErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = errorMessage,
            message = message,
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(CreditorNotFoundException::class)
    fun handleCreditorNotFoundException(ex: CreditorNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Creditor not found: {}", ex.message)
        val locale = getLocale(request)
        val message = messageSource.getMessage(
            "creditor.not.found",
            arrayOf(ex.id.toString()),
            "Creditor not found with id: ${ex.id}",
            locale
        ) ?: "Creditor not found with id: ${ex.id}"
        val errorMessage = messageSource.getMessage("error.not.found", null, "Not Found", locale) ?: "Not Found"
        
        val errorResponse = ErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = errorMessage,
            message = message,
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(DebtorNotFoundException::class)
    fun handleDebtorNotFoundException(ex: DebtorNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Debtor not found: {}", ex.message)
        val locale = getLocale(request)
        val message = messageSource.getMessage(
            "debtor.not.found",
            arrayOf(ex.id.toString()),
            "Debtor not found with id: ${ex.id}",
            locale
        ) ?: "Debtor not found with id: ${ex.id}"
        val errorMessage = messageSource.getMessage("error.not.found", null, "Not Found", locale) ?: "Not Found"
        
        val errorResponse = ErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = errorMessage,
            message = message,
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(InvalidPaymentException::class)
    fun handleInvalidPaymentException(ex: InvalidPaymentException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Invalid payment: {}", ex.message)
        val errorResponse = ErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid payment",
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ValidationErrorResponse> {
        logger.error("Validation failed: {}", ex.bindingResult)
        val locale = getLocale(request)
        
        val fieldErrors = ex.bindingResult.fieldErrors.associate { fieldError ->
            val localizedMessage = try {
                // Try to get localized message using the code
                val code = fieldError.code
                val fieldName = fieldError.field
                
                // Try common validation message keys
                val messageKey = when {
                    code == "NotNull" || code == "NotBlank" -> {
                        when {
                            fieldName.contains("amount", ignoreCase = true) -> "payment.invalid.amount"
                            fieldName.contains("currency", ignoreCase = true) -> "payment.invalid.currency"
                            fieldName.contains("status", ignoreCase = true) -> "payment.invalid.status"
                            fieldName.contains("name", ignoreCase = true) -> "creditor.name.required"
                            fieldName.contains("account", ignoreCase = true) -> "creditor.account.required"
                            fieldName.contains("bank", ignoreCase = true) -> "creditor.bank.code.required"
                            else -> "validation.required"
                        }
                    }
                    code == "DecimalMin" || code == "Min" -> "payment.invalid.amount"
                    code == "Pattern" || code == "Size" -> {
                        when {
                            fieldName.contains("currency", ignoreCase = true) -> "payment.invalid.currency"
                            else -> "validation.required"
                        }
                    }
                    else -> fieldError.defaultMessage ?: "validation.required"
                }
                
                messageSource.getMessage(messageKey, null, fieldError.defaultMessage ?: "Invalid value", locale) 
                    ?: (fieldError.defaultMessage ?: "Invalid value")
            } catch (e: Exception) {
                logger.debug("Failed to localize validation message for field ${fieldError.field}: {}", e.message)
                fieldError.defaultMessage ?: "Invalid value"
            }
            
            fieldError.field to localizedMessage
        }
        
        val errorMessage = messageSource.getMessage("error.validation.failed", null, "Validation Failed", locale) ?: "Validation Failed"
        val message = messageSource.getMessage("error.validation.failed", null, "Request validation failed", locale) ?: "Request validation failed"
        
        val errorResponse = ValidationErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = errorMessage,
            message = message,
            path = request.getDescription(false).replace("uri=", ""),
            fieldErrors = fieldErrors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(ex: MethodArgumentTypeMismatchException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Type mismatch: {}", ex.message)
        val errorResponse = ErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = "Invalid parameter type: ${ex.name}",
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Illegal argument: {}", ex.message)
        val errorResponse = ErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid argument",
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error: ", ex)
        val locale = getLocale(request)
        val errorMessage = messageSource.getMessage("error.internal.server", null, "Internal Server Error", locale) ?: "Internal Server Error"
        val message = messageSource.getMessage("error.internal.server", null, "An unexpected error occurred while processing your request", locale) ?: "An unexpected error occurred while processing your request"
        
        val errorResponse = ErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = errorMessage,
            message = message,
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
