package com.example.paymentecho.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.*

/**
 * Configuration for internationalization (i18n) support.
 * 
 * Configures message source for localized messages and locale resolver for Accept-Language header support.
 * 
 * Supported languages:
 * - hi (Hindi) - Default
 * - en (English)
 * - es (Spanish)
 * - fr (French)
 * - de (German)
 * - bn (Bengali)
 * - ta (Tamil)
 * - te (Telugu)
 * - kn (Kannada)
 * - ru (Russian)
 * - zh (Chinese)
 */
@Configuration
class MessageSourceConfig : WebMvcConfigurer {

    /**
     * Configures the message source for loading localized messages from properties files.
     * 
     * Message files are located at: src/main/resources/messages*.properties
     * Cache is disabled (0 seconds) for development/testing to allow immediate updates.
     * 
     * @return Configured MessageSource bean
     */
    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("classpath:messages")
        messageSource.setDefaultEncoding("UTF-8")
        messageSource.setCacheSeconds(0) // Disable cache for testing (set to 3600 for production)
        messageSource.setFallbackToSystemLocale(false)
        messageSource.setUseCodeAsDefaultMessage(false)
        return messageSource
    }

    /**
     * Configures the locale resolver to read Accept-Language header from HTTP requests.
     * 
     * The bean name "localeResolver" ensures Spring MVC automatically uses it.
     * Default locale is set to Hindi (hi).
     * 
     * Works in conjunction with LocaleFilter to set locale in LocaleContextHolder.
     * 
     * @return Configured LocaleResolver bean
     */
    @Bean(name = ["localeResolver"])
    fun localeResolver(): LocaleResolver {
        val resolver = AcceptHeaderLocaleResolver()
        resolver.setDefaultLocale(Locale.forLanguageTag("hi")) // Default to Hindi
        return resolver
    }
}
