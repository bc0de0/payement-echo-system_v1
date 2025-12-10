package com.example.paymentecho.util

import com.example.paymentecho.dto.response.JsonApiData
import com.example.paymentecho.dto.response.JsonApiLinks
import com.example.paymentecho.dto.response.JsonApiMeta
import com.example.paymentecho.dto.response.JsonApiResponse
import com.example.paymentecho.dto.response.JsonApiResourceLinks
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component("jsonApiResponseBuilder")
class ResponseBuilder {

    fun <T> buildSingleResponse(
        id: String,
        type: String,
        attributes: T,
        request: HttpServletRequest? = null
    ): JsonApiResponse<JsonApiData<T>> {
        val path = request?.requestURI ?: ""
        val selfLink = if (path.isNotEmpty()) path else null

        return JsonApiResponse(
            data = JsonApiData(
                id = id,
                type = type,
                attributes = attributes,
                links = JsonApiResourceLinks(self = selfLink ?: "")
            ),
            meta = JsonApiMeta(
                requestId = generateRequestId(),
                version = "v1",
                generatedAt = Instant.now()
            ),
            links = JsonApiLinks(self = selfLink)
        )
    }

    fun <T> buildListResponse(
        items: List<Pair<String, T>>,
        type: String,
        request: HttpServletRequest? = null
    ): JsonApiResponse<List<JsonApiData<T>>> {
        val path = request?.requestURI ?: ""
        val selfLink = if (path.isNotEmpty()) path else null

        val dataList = items.map { (id, attributes) ->
            JsonApiData(
                id = id,
                type = type,
                attributes = attributes,
                links = JsonApiResourceLinks(self = "$selfLink/$id")
            )
        }

        // JSON:API format: data is an array of resource objects
        return JsonApiResponse(
            data = dataList,
            meta = JsonApiMeta(
                requestId = generateRequestId(),
                version = "v1",
                generatedAt = Instant.now(),
                total = items.size
            ),
            links = JsonApiLinks(self = selfLink)
        )
    }
    
    // Helper method for single item list (for compatibility)
    fun <T> buildSingleItemListResponse(
        id: String,
        type: String,
        attributes: T,
        request: HttpServletRequest? = null
    ): JsonApiResponse<List<JsonApiData<T>>> {
        return buildListResponse(listOf(Pair(id, attributes)), type, request)
    }

    private fun generateRequestId(): String {
        return "req_${UUID.randomUUID().toString().substring(0, 8)}"
    }
}
