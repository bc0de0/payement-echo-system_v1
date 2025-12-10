package com.example.paymentecho.mapper

import com.example.paymentecho.dto.request.PaymentCreateRequest
import com.example.paymentecho.dto.request.PaymentEchoRequest
import com.example.paymentecho.dto.response.PaymentAttributes
import com.example.paymentecho.dto.response.PaymentResponse
import com.example.paymentecho.entity.Payment
import org.springframework.stereotype.Component

@Component
class PaymentMapper {

    fun toEntity(request: PaymentCreateRequest, creditor: com.example.paymentecho.entity.Creditor? = null, debtor: com.example.paymentecho.entity.Debtor? = null): Payment {
        return Payment(
            amount = request.amount,
            currency = request.currency,
            status = request.status,
            creditor = creditor,
            debtor = debtor
        )
    }

    fun toEntity(request: PaymentEchoRequest, creditor: com.example.paymentecho.entity.Creditor? = null, debtor: com.example.paymentecho.entity.Debtor? = null): Payment {
        return Payment(
            amount = request.amount,
            currency = request.currency,
            status = request.status,
            creditor = creditor,
            debtor = debtor
        )
    }

    fun toResponse(payment: Payment): PaymentResponse {
        return PaymentResponse(
            id = payment.id!!,
            amount = payment.amount,
            currency = payment.currency,
            status = payment.status,
            createdAt = payment.createdAt,
            creditorId = payment.creditor?.id,
            debtorId = payment.debtor?.id
        )
    }

    fun toAttributes(payment: Payment): PaymentAttributes {
        return PaymentAttributes(
            amount = payment.amount,
            currency = payment.currency,
            status = payment.status,
            createdAt = payment.createdAt,
            creditorId = payment.creditor?.id,
            debtorId = payment.debtor?.id
        )
    }

    fun toResponseList(payments: List<Payment>): List<PaymentResponse> {
        return payments.map { toResponse(it) }
    }
}
