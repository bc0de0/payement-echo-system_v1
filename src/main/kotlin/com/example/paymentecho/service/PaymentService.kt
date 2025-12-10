package com.example.paymentecho.service

import com.example.paymentecho.entity.Payment
import com.example.paymentecho.repository.PaymentRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentService(private val repo: PaymentRepository) {

    fun findAll(): List<Payment> = repo.findAll()

    fun findById(id: UUID): Payment? = repo.findById(id).orElse(null)

    fun create(payment: Payment): Payment = repo.save(payment.copy(id = null))

    fun echo(payment: Payment): Payment {
        return repo.save(payment.copy(id = null))
    }
}
