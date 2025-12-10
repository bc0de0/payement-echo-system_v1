package com.example.paymentecho.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.servlet.LocaleResolver
import java.util.*

@Component
@Order(0) // Run before other filters to set locale early
class LocaleFilter(
    private val localeResolver: LocaleResolver
) : Filter {

    private val logger = LoggerFactory.getLogger(LocaleFilter::class.java)

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val acceptLanguage = httpRequest.getHeader("Accept-Language")
        val locale = localeResolver.resolveLocale(httpRequest)
        
        logger.debug("LocaleFilter: Accept-Language header: {}, Resolved locale: {}", acceptLanguage, locale)
        LocaleContextHolder.setLocale(locale)
        
        try {
            chain.doFilter(request, response)
        } finally {
            LocaleContextHolder.resetLocaleContext()
        }
    }
}
