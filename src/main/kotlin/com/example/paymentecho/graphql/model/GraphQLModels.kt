package com.example.paymentecho.graphql.model

data class Payment(
    val id: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val createdAt: String,
    val creditorId: String?,
    val debtorId: String?,
    val creditor: Creditor? = null,
    val debtor: Debtor? = null
)

data class Creditor(
    val id: String,
    val name: String,
    val accountNumber: String,
    val bankCode: String,
    val address: String?,
    val email: String?,
    val createdAt: String,
    val updatedAt: String
)

data class Debtor(
    val id: String,
    val name: String,
    val accountNumber: String,
    val bankCode: String,
    val address: String?,
    val email: String?,
    val createdAt: String,
    val updatedAt: String
)

data class PaymentPage(
    val payments: List<Payment>,
    val total: Int,
    val page: Int,
    val size: Int,
    val totalPages: Int
)

data class CreditorPage(
    val creditors: List<Creditor>,
    val total: Int,
    val page: Int,
    val size: Int,
    val totalPages: Int
)

data class DebtorPage(
    val debtors: List<Debtor>,
    val total: Int,
    val page: Int,
    val size: Int,
    val totalPages: Int
)
