package com.example.paymentecho.exception

import java.util.*

class CreditorNotFoundException(val id: UUID) : RuntimeException("Creditor not found with id: $id")
