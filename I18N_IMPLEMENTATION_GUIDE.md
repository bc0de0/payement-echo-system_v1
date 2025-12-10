# Internationalization (i18n) Implementation Guide

## Overview

This guide explains how to implement multi-language support in the Payment Echo System.

## Step 1: Add Dependencies

No additional dependencies needed - Spring Boot includes MessageSource support.

## Step 2: Create Message Files

Create message property files in `src/main/resources/`:

### messages.properties (English - Default)

```properties
# Payment Messages
payment.not.found=Payment not found with id: {0}
payment.created=Payment created successfully
payment.deleted=Payment deleted successfully
payment.invalid.amount=Amount must be greater than 0
payment.invalid.currency=Currency must be a valid 3-letter ISO code
payment.invalid.status=Status must be one of: RECEIVED, PROCESSING, COMPLETED, FAILED

# Creditor Messages
creditor.not.found=Creditor not found with id: {0}
creditor.created=Creditor created successfully
creditor.deleted=Creditor deleted successfully
creditor.name.required=Name is required
creditor.account.required=Account number is required

# Debtor Messages
debtor.not.found=Debtor not found with id: {0}
debtor.created=Debtor created successfully
debtor.deleted=Debtor deleted successfully
debtor.name.required=Name is required
debtor.account.required=Account number is required

# Validation Messages
validation.required=This field is required
validation.email.invalid=Email must be valid
validation.amount.min=Amount must be at least {0}
validation.amount.max=Amount must not exceed {1}

# Error Messages
error.bad.request=Bad Request
error.not.found=Not Found
error.internal.server=Internal Server Error
error.validation.failed=Validation failed
```

### messages_es.properties (Spanish)

```properties
# Payment Messages
payment.not.found=Pago no encontrado con id: {0}
payment.created=Pago creado exitosamente
payment.deleted=Pago eliminado exitosamente
payment.invalid.amount=El monto debe ser mayor que 0
payment.invalid.currency=La moneda debe ser un código ISO de 3 letras válido
payment.invalid.status=El estado debe ser uno de: RECEIVED, PROCESSING, COMPLETED, FAILED

# Creditor Messages
creditor.not.found=Acreedor no encontrado con id: {0}
creditor.created=Acreedor creado exitosamente
creditor.deleted=Acreedor eliminado exitosamente
creditor.name.required=El nombre es requerido
creditor.account.required=El número de cuenta es requerido

# Debtor Messages
debtor.not.found=Deudor no encontrado con id: {0}
debtor.created=Deudor creado exitosamente
debtor.deleted=Deudor eliminado exitosamente
debtor.name.required=El nombre es requerido
debtor.account.required=El número de cuenta es requerido

# Validation Messages
validation.required=Este campo es requerido
validation.email.invalid=El correo electrónico debe ser válido
validation.amount.min=El monto debe ser al menos {0}
validation.amount.max=El monto no debe exceder {1}

# Error Messages
error.bad.request=Solicitud Incorrecta
error.not.found=No Encontrado
error.internal.server=Error Interno del Servidor
error.validation.failed=Validación fallida
```

### messages_fr.properties (French)

```properties
# Payment Messages
payment.not.found=Paiement introuvable avec l'id: {0}
payment.created=Paiement créé avec succès
payment.deleted=Paiement supprimé avec succès
payment.invalid.amount=Le montant doit être supérieur à 0
payment.invalid.currency=La devise doit être un code ISO à 3 lettres valide
payment.invalid.status=Le statut doit être l'un des suivants: RECEIVED, PROCESSING, COMPLETED, FAILED

# Error Messages
error.bad.request=Mauvaise Demande
error.not.found=Non Trouvé
error.internal.server=Erreur Interne du Serveur
error.validation.failed=Échec de la validation
```

### messages_hi.properties (Hindi)

```properties
# Payment Messages
payment.not.found=आईडी के साथ भुगतान नहीं मिला: {0}
payment.created=भुगतान सफलतापूर्वक बनाया गया
payment.deleted=भुगतान सफलतापूर्वक हटा दिया गया
payment.invalid.amount=राशि 0 से अधिक होनी चाहिए
payment.invalid.currency=मुद्रा एक वैध 3-अक्षर ISO कोड होना चाहिए

# Error Messages
error.bad.request=खराब अनुरोध
error.not.found=नहीं मिला
error.internal.server=आंतरिक सर्वर त्रुटि
error.validation.failed=सत्यापन विफल
```

## Step 3: Configure MessageSource

Create `src/main/kotlin/com/example/paymentecho/config/MessageSourceConfig.kt`:

```kotlin
package com.example.paymentecho.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.*

@Configuration
class MessageSourceConfig {

    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("classpath:messages")
        messageSource.setDefaultEncoding("UTF-8")
        messageSource.setCacheSeconds(3600)
        return messageSource
    }

    @Bean
    fun localeResolver(): LocaleResolver {
        val resolver = AcceptHeaderLocaleResolver()
        resolver.defaultLocale = Locale.ENGLISH
        resolver.supportedLocales = listOf(
            Locale.ENGLISH,
            Locale("es"), // Spanish
            Locale("fr"), // French
            Locale("de"), // German
            Locale("hi"), // Hindi
            Locale("zh")  // Chinese
        )
        return resolver
    }
}
```

## Step 4: Update Exception Handler

Update `GlobalExceptionHandler.kt`:

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: MessageSource,
    private val localeResolver: LocaleResolver
) {

    @ExceptionHandler(PaymentNotFoundException::class)
    fun handlePaymentNotFoundException(
        ex: PaymentNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val locale = localeResolver.resolveLocale(request as HttpServletRequest)
        val message = messageSource.getMessage(
            "payment.not.found",
            arrayOf(ex.id.toString()),
            locale
        )

        val errorResponse = ErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = messageSource.getMessage("error.not.found", null, locale),
            message = message,
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    // Similar updates for other exceptions...
}
```

## Step 5: Update Validation Messages

Create custom validation messages in `messages.properties`:

```properties
jakarta.validation.constraints.NotNull.message={validation.required}
jakarta.validation.constraints.NotBlank.message={validation.required}
jakarta.validation.constraints.Email.message={validation.email.invalid}
jakarta.validation.constraints.DecimalMin.message={validation.amount.min}
jakarta.validation.constraints.DecimalMax.message={validation.amount.max}
```

## Step 6: Add Locale to Controllers

Update controllers to accept `Accept-Language` header:

```kotlin
@GetMapping("/{id}")
fun getById(
    @PathVariable id: UUID,
    @RequestHeader(value = "Accept-Language", defaultValue = "en") locale: String
): ResponseEntity<JsonApiResponse<PaymentAttributes>> {
    // Implementation
}
```

## Step 7: Test i18n

### Test with curl:

```bash
# English (default)
curl -H "Accept-Language: en" http://localhost:8080/api/payments/123

# Spanish
curl -H "Accept-Language: es" http://localhost:8080/api/payments/123

# French
curl -H "Accept-Language: fr" http://localhost:8080/api/payments/123

# Hindi
curl -H "Accept-Language: hi" http://localhost:8080/api/payments/123
```

## Step 8: Currency Formatting by Locale

Add currency formatting utility:

```kotlin
@Component
class CurrencyFormatter {
    fun format(amount: Double, currency: String, locale: Locale): String {
        val formatter = NumberFormat.getCurrencyInstance(locale)
        return formatter.format(amount)
    }
}
```

## Benefits

1. **Global Reach**: Support users worldwide
2. **Better UX**: Users see messages in their language
3. **Compliance**: Meet international requirements
4. **Professional**: Shows attention to detail

## Supported Languages

- English (en) - Default
- Spanish (es)
- French (fr)
- German (de)
- Hindi (hi)
- Chinese (zh)

## Adding New Languages

1. Create `messages_XX.properties` file
2. Add locale to `MessageSourceConfig`
3. Translate all message keys
4. Test with `Accept-Language` header

## Best Practices

1. Use message keys, not hardcoded strings
2. Keep messages concise
3. Use placeholders for dynamic values: `{0}`, `{1}`
4. Test all languages
5. Consider RTL languages (Arabic, Hebrew) for future
