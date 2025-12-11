package com.example.paymentecho.exception

import java.util.*

class DebtorNotFoundException(val id: UUID) : RuntimeException("Debtor not found with id: $id")
