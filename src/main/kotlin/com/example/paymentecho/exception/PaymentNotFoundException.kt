package com.example.paymentecho.exception

import java.util.*

class PaymentNotFoundException(val id: UUID) : RuntimeException("Payment not found with id: $id")
