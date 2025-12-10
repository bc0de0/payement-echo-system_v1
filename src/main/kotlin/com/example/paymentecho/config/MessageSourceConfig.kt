package com.example.paymentecho.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import java.util.*

@Configuration
class MessageSourceConfig : WebMvcConfigurer {

    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("classpath:messages")
        messageSource.setDefaultEncoding("UTF-8")
        messageSource.setCacheSeconds(0) // Disable cache for testing
        messageSource.setFallbackToSystemLocale(false)
        messageSource.setUseCodeAsDefaultMessage(false)
        return messageSource
    }

    @Bean(name = ["localeResolver"])
    fun localeResolver(): LocaleResolver {
        val resolver = AcceptHeaderLocaleResolver()
        resolver.setDefaultLocale(Locale.forLanguageTag("hi")) // Default to Hindi
        // AcceptHeaderLocaleResolver automatically reads Accept-Language header from HTTP requests
        // Supported locales: hi (default), en, es, fr, de, bn, ta, te, kn, ru, zh
        // Spring Boot will automatically use this bean for locale resolution
        // The bean name "localeResolver" ensures Spring MVC uses it
        // The LocaleFilter ensures the locale is set in LocaleContextHolder for all requests
        return resolver
    }
}
