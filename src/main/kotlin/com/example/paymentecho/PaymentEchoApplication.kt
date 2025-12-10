package com.example.paymentecho

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentEchoApplication

fun main(args: Array<String>) {
    runApplication<PaymentEchoApplication>(*args)
}
