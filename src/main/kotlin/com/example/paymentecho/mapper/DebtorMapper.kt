package com.example.paymentecho.mapper

import com.example.paymentecho.dto.request.DebtorCreateRequest
import com.example.paymentecho.dto.response.DebtorAttributes
import com.example.paymentecho.dto.response.DebtorResponse
import com.example.paymentecho.entity.Debtor
import org.springframework.stereotype.Component

@Component
class DebtorMapper {

    fun toEntity(request: com.example.paymentecho.dto.request.DebtorCreateRequest): Debtor {
        return Debtor(
            name = request.name,
            accountNumber = request.accountNumber,
            bankCode = request.bankCode,
            address = request.address,
            email = request.email
        )
    }

    fun toResponse(debtor: Debtor): DebtorResponse {
        return DebtorResponse(
            id = debtor.id!!,
            name = debtor.name,
            accountNumber = debtor.accountNumber,
            bankCode = debtor.bankCode,
            address = debtor.address,
            email = debtor.email,
            createdAt = debtor.createdAt,
            updatedAt = debtor.updatedAt
        )
    }

    fun toAttributes(debtor: Debtor): DebtorAttributes {
        return DebtorAttributes(
            name = debtor.name,
            accountNumber = debtor.accountNumber,
            bankCode = debtor.bankCode,
            address = debtor.address,
            email = debtor.email,
            createdAt = debtor.createdAt,
            updatedAt = debtor.updatedAt
        )
    }

    fun toResponseList(debtors: List<Debtor>): List<DebtorResponse> {
        return debtors.map { toResponse(it) }
    }
}
