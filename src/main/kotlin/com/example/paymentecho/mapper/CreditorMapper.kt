package com.example.paymentecho.mapper

import com.example.paymentecho.dto.request.CreditorCreateRequest
import com.example.paymentecho.dto.response.CreditorAttributes
import com.example.paymentecho.dto.response.CreditorResponse
import com.example.paymentecho.entity.Creditor
import org.springframework.stereotype.Component

@Component
class CreditorMapper {

    fun toEntity(request: CreditorCreateRequest): Creditor {
        return Creditor(
            name = request.name,
            accountNumber = request.accountNumber,
            bankCode = request.bankCode,
            address = request.address,
            email = request.email
        )
    }

    fun toResponse(creditor: Creditor): CreditorResponse {
        return CreditorResponse(
            id = creditor.id!!,
            name = creditor.name,
            accountNumber = creditor.accountNumber,
            bankCode = creditor.bankCode,
            address = creditor.address,
            email = creditor.email,
            createdAt = creditor.createdAt,
            updatedAt = creditor.updatedAt
        )
    }

    fun toAttributes(creditor: Creditor): CreditorAttributes {
        return CreditorAttributes(
            name = creditor.name,
            accountNumber = creditor.accountNumber,
            bankCode = creditor.bankCode,
            address = creditor.address,
            email = creditor.email,
            createdAt = creditor.createdAt,
            updatedAt = creditor.updatedAt
        )
    }

    fun toResponseList(creditors: List<Creditor>): List<CreditorResponse> {
        return creditors.map { toResponse(it) }
    }
}
