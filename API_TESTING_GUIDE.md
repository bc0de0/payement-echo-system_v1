# API Testing Guide - Payment Echo System

## Overview

This document provides a comprehensive guide for testing all APIs in the Payment Echo System.

## Base URL

- Local: `http://localhost:8080`

## Endpoints

### 1. Actuator Health Check

```bash
curl http://localhost:8080/actuator/health
```

### 2. Actuator Info

```bash
curl http://localhost:8080/actuator/info
```

### 3. Swagger UI

- URL: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Creditor APIs

### Create Creditor

```bash
curl -X POST http://localhost:8080/api/creditors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "accountNumber": "1234567890",
    "bankCode": "BANK001",
    "email": "john@example.com",
    "address": "123 Main St"
  }'
```

### Get All Creditors

```bash
curl http://localhost:8080/api/creditors
```

### Get Creditor by ID

```bash
curl http://localhost:8080/api/creditors/{creditorId}
```

### Delete Creditor

```bash
curl -X DELETE http://localhost:8080/api/creditors/{creditorId}
```

## Debtor APIs

### Create Debtor

```bash
curl -X POST http://localhost:8080/api/debtors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "accountNumber": "0987654321",
    "bankCode": "BANK002",
    "email": "jane@example.com",
    "address": "456 Oak Ave"
  }'
```

### Get All Debtors

```bash
curl http://localhost:8080/api/debtors
```

### Get Debtor by ID

```bash
curl http://localhost:8080/api/debtors/{debtorId}
```

### Delete Debtor

```bash
curl -X DELETE http://localhost:8080/api/debtors/{debtorId}
```

## Payment APIs

### Create Payment

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "currency": "USD",
    "status": "RECEIVED",
    "creditorId": "creditor-uuid-here",
    "debtorId": "debtor-uuid-here"
  }'
```

### Create Payment (without creditor/debtor)

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "currency": "USD",
    "status": "RECEIVED"
  }'
```

### Get All Payments

```bash
curl http://localhost:8080/api/payments
```

### Get Payment by ID

```bash
curl http://localhost:8080/api/payments/{paymentId}
```

### Echo Payment

```bash
curl -X POST http://localhost:8080/api/payments/echo \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 200.75,
    "currency": "EUR",
    "status": "PROCESSING"
  }'
```

## Validation Examples

### Invalid Payment (should return 400)

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": -10,
    "currency": "INVALID",
    "status": "BAD_STATUS"
  }'
```

### Invalid Creditor (should return 400)

```bash
curl -X POST http://localhost:8080/api/creditors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "accountNumber": "",
    "bankCode": ""
  }'
```

## Error Responses

### 404 Not Found

```bash
curl http://localhost:8080/api/payments/00000000-0000-0000-0000-000000000000
```

### 400 Bad Request (Validation Error)

Returns validation errors with field names and messages.

## Test Status Codes

All endpoints have been tested and verified:

- ✅ Health endpoint: 200 OK
- ✅ Creditor CRUD: 201 Created, 200 OK, 204 No Content
- ✅ Debtor CRUD: 201 Created, 200 OK, 204 No Content
- ✅ Payment CRUD: 201 Created, 200 OK
- ✅ Payment Echo: 200 OK
- ✅ Validation: 400 Bad Request
- ✅ Not Found: 404 Not Found
- ✅ Swagger UI: Accessible
- ✅ OpenAPI Docs: Available

## Running Tests

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test Class

```bash
./gradlew test --tests PaymentServiceTest
```

### Build Project

```bash
./gradlew build
```

### Run Application

```bash
./gradlew bootRun
```

## Test Coverage

The project includes comprehensive test coverage:

- Repository tests (Payment, Creditor, Debtor)
- Service tests (Payment, Creditor, Debtor)
- Controller tests (Payment, Creditor, Debtor)
- Integration tests

All tests pass successfully ✅
