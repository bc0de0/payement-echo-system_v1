package com.example.paymentecho.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.util.*

@Component
@Order(1)
class RequestResponseLoggingFilter : Filter {

    private val logger = LoggerFactory.getLogger(RequestResponseLoggingFilter::class.java)

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val requestId = UUID.randomUUID().toString()
        MDC.put("requestId", requestId)
        httpResponse.setHeader("X-Request-ID", requestId)

        val startTime = System.currentTimeMillis()

        val wrappedRequest = ContentCachingRequestWrapper(httpRequest)
        val wrappedResponse = ContentCachingResponseWrapper(httpResponse)

        try {
            logRequest(wrappedRequest)
            chain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            val duration = System.currentTimeMillis() - startTime
            logResponse(wrappedRequest, wrappedResponse, duration)
            wrappedResponse.copyBodyToResponse()
            MDC.clear()
        }
    }

    private fun logRequest(request: ContentCachingRequestWrapper) {
        val requestBody = String(request.contentAsByteArray)
        logger.info(
            "Incoming Request - Method: {}, URI: {}, Headers: {}, Body: {}",
            request.method,
            request.requestURI,
            getHeaders(request),
            maskSensitiveData(requestBody)
        )
    }

    private fun logResponse(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        duration: Long
    ) {
        val responseBody = String(response.contentAsByteArray)
        response.setHeader("X-Response-Time", "${duration}ms")
        
        logger.info(
            "Outgoing Response - Status: {}, Duration: {}ms, URI: {}, Body: {}",
            response.status,
            duration,
            request.requestURI,
            maskSensitiveData(responseBody)
        )
    }

    private fun getHeaders(request: HttpServletRequest): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        request.headerNames.asIterator().forEach { headerName ->
            headers[headerName] = request.getHeader(headerName) ?: ""
        }
        return headers
    }

    private fun maskSensitiveData(data: String): String {
        if (data.isEmpty()) return data
        
        return data
            .replace(Regex("""("password"\s*:\s*")[^"]*(")"""), "$1***$2")
            .replace(Regex("""("email"\s*:\s*")[^"]*(")"""), "$1***$2")
            .replace(Regex("""("accountNumber"\s*:\s*")[^"]*(")"""), "$1***$2")
            .replace(Regex("""("bankCode"\s*:\s*")[^"]*(")"""), "$1***$2")
    }
}
