package com.example.paymentecho.controller

import com.example.paymentecho.entity.Payment
import com.example.paymentecho.service.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(private val service: PaymentService) {

    @GetMapping
    fun all() = service.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<Payment> {
        val p = service.findById(id)
        return if (p != null) ResponseEntity.ok(p) else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun create(@RequestBody payment: Payment): ResponseEntity.BodyBuilder {
        val created = service.create(payment)
        return ResponseEntity.status(HttpStatus.CREATED)
    }

    @PostMapping("/echo")
    fun echo(@RequestBody payment: Payment): ResponseEntity<Payment> {
        return ResponseEntity.ok(service.echo(payment))
    }
}
