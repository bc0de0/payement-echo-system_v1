# Quick Start Guide - Testing with Sample Data

## 🚀 Quick Test (No Setup Required)

### Option 1: Standalone Payment (Easiest - No IDs Needed)

```bash
curl -X 'POST' \
  'http://localhost:8080/api/v1/payments' \
  -H 'Content-Type: application/json' \
  -d '{
  "amount": 1000.00,
  "currency": "USD",
  "status": "RECEIVED"
}'
```

This works immediately - no creditor/debtor IDs needed!

---

## 📋 Full Test Flow (With Creditor/Debtor)

### Step 1: Get Creditor IDs

```bash
curl http://localhost:8080/api/v1/creditors
```

**Response example:**

```json
{
  "total": 3,
  "creditors": [
    {
      "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",  ← Copy this ID
      "name": "Acme Corporation",
      "accountNumber": "ACC001234567",
      ...
    },
    ...
  ]
}
```

### Step 2: Get Debtor IDs

```bash
curl http://localhost:8080/api/v1/debtors
```

**Response example:**

```json
{
  "total": 3,
  "debtors": [
    {
      "id": "f1e2d3c4-b5a6-9876-5432-109876543210",  ← Copy this ID
      "name": "John Doe",
      "accountNumber": "DEB001111111",
      ...
    },
    ...
  ]
}
```

### Step 3: Create Payment with IDs

Replace `YOUR_CREDITOR_ID` and `YOUR_DEBTOR_ID` with the IDs from steps 1 and 2:

```bash
curl -X 'POST' \
  'http://localhost:8080/api/v1/payments' \
  -H 'Content-Type: application/json' \
  -d '{
  "amount": 1500.00,
  "currency": "USD",
  "status": "RECEIVED",
  "creditorId": "YOUR_CREDITOR_ID",
  "debtorId": "YOUR_DEBTOR_ID"
}'
```

---

## 🎯 Using Swagger UI

1. **Open Swagger UI**: http://localhost:8080/swagger-ui.html

2. **Get IDs First**:

   - Expand `GET /api/v1/creditors`
   - Click "Try it out" → "Execute"
   - Copy an `id` from the response
   - Repeat for `GET /api/v1/debtors`

3. **Create Payment**:
   - Expand `POST /api/v1/payments`
   - Click "Try it out"
   - Select "Standalone Payment" example (no IDs needed) OR
   - Select "Payment with Creditor/Debtor" and replace placeholder IDs with actual IDs
   - Click "Execute"

---

## 📮 Using Postman

### Method 1: Use Variables (Recommended)

1. **Set Variables**:

   - Run "Get All Creditors" request
   - Copy an `id` from response
   - Go to Collection Variables → Set `creditorId` = `<copied-id>`
   - Repeat for "Get All Debtors" → Set `debtorId` = `<copied-id>`

2. **Create Payment**:
   - Run "Create Payment (Sample: USD RECEIVED)"
   - It will automatically use the variables

### Method 2: Use Standalone Payment

- Run "Create Payment (Standalone - No Creditor/Debtor)"
- Works immediately without any setup!

---

## 📊 Sample Data in Database

The application automatically creates sample data on first startup:

### Creditors (3)

- **Acme Corporation** - Account: ACC001234567, Bank: BANK001
- **Tech Solutions Inc** - Account: ACC002345678, Bank: BANK002
- **Global Services Ltd** - Account: ACC003456789, Bank: BANK003

### Debtors (3)

- **John Doe** - Account: DEB001111111, Bank: BANK001
- **Jane Smith** - Account: DEB002222222, Bank: BANK002
- **Robert Johnson** - Account: DEB003333333, Bank: BANK003

### Payments (7)

- Various amounts, currencies (USD, EUR, GBP), and statuses

---

## ⚠️ Common Issues

### Error: "Creditor not found with id: ..."

**Solution**: The ID you're using doesn't exist.

1. Run `GET /api/v1/creditors` to get valid IDs
2. Use one of those IDs, or
3. Create a standalone payment without creditorId/debtorId

### Error: "Debtor not found with id: ..."

**Solution**: Same as above - get valid IDs from `GET /api/v1/debtors`

### Quick Fix: Use Standalone Payment

If you just want to test quickly, use a payment without creditorId/debtorId:

```json
{
  "amount": 1000.0,
  "currency": "USD",
  "status": "RECEIVED"
}
```

This always works! ✅

---

## 🔗 Useful Endpoints

- **Get All Payments**: `GET http://localhost:8080/api/v1/payments`
- **Get All Creditors**: `GET http://localhost:8080/api/v1/creditors`
- **Get All Debtors**: `GET http://localhost:8080/api/v1/debtors`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/paymentdb`)
