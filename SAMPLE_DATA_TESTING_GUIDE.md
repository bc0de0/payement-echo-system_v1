# Sample Data Testing Guide

## Overview

The application now includes automatic sample data initialization and file-based H2 database persistence for testing hot reload functionality.

## Database Configuration

### File-Based H2 Database
- **Location**: `./data/paymentdb.mv.db` (created automatically)
- **Persistence**: Data persists across application restarts
- **Hot Reload**: Database persists during code changes and hot reloads
- **Auto Server**: Enabled for concurrent access

### Sample Data Initialization

Sample data is automatically created on first startup (if database is empty):

#### Creditors (3)
1. **Acme Corporation**
   - Account: ACC001234567
   - Bank: BANK001
   - Email: finance@acme.com

2. **Tech Solutions Inc**
   - Account: ACC002345678
   - Bank: BANK002
   - Email: payments@techsolutions.com

3. **Global Services Ltd**
   - Account: ACC003456789
   - Bank: BANK003
   - Email: accounts@globalservices.com

#### Debtors (3)
1. **John Doe**
   - Account: DEB001111111
   - Bank: BANK001
   - Email: john.doe@email.com

2. **Jane Smith**
   - Account: DEB002222222
   - Bank: BANK002
   - Email: jane.smith@email.com

3. **Robert Johnson**
   - Account: DEB003333333
   - Bank: BANK003
   - Email: robert.j@email.com

#### Payments (7)
1. $1,500.00 USD - RECEIVED (John Doe → Acme Corporation)
2. €2,500.50 EUR - PROCESSING (Jane Smith → Tech Solutions Inc)
3. £3,200.75 GBP - COMPLETED (Robert Johnson → Global Services Ltd)
4. $500.00 USD - RECEIVED (Jane Smith → Acme Corporation)
5. $750.25 USD - FAILED (John Doe → Tech Solutions Inc)
6. $1,000.00 USD - RECEIVED (standalone)
7. €2,000.00 EUR - PROCESSING (standalone)

## Testing with Sample Data

### 1. Start the Application

```bash
./gradlew bootRun
```

The application will:
- Create the database file at `./data/paymentdb.mv.db`
- Initialize sample data automatically
- Start on `http://localhost:8080`

### 2. Test REST API Endpoints

#### Get All Payments
```bash
curl http://localhost:8080/api/v1/payments
```

#### Get Payment by ID
```bash
# First, get a payment ID from the list above
curl http://localhost:8080/api/v1/payments/{payment-id}
```

#### Filter Payments by Status
```bash
curl "http://localhost:8080/api/v1/payments?status=RECEIVED"
```

#### Filter Payments by Currency
```bash
curl "http://localhost:8080/api/v1/payments?currency=USD"
```

#### Filter Payments by Amount Range
```bash
curl "http://localhost:8080/api/v1/payments?minAmount=1000&maxAmount=2000"
```

#### Get All Creditors
```bash
curl http://localhost:8080/api/v1/creditors
```

#### Filter Creditors by Name
```bash
curl "http://localhost:8080/api/v1/creditors?name=Acme"
```

#### Get All Debtors
```bash
curl http://localhost:8080/api/v1/debtors
```

### 3. Test GraphQL API

#### Query All Payments
```graphql
query {
  payments {
    payments {
      id
      amount
      currency
      status
    }
    total
  }
}
```

#### Query Payment by ID
```graphql
query {
  payment(id: "payment-id-here") {
    id
    amount
    currency
    status
    creditorId
    debtorId
  }
}
```

#### Filter Payments by Status
```graphql
query {
  paymentsByStatus(status: "RECEIVED") {
    payments {
      id
      amount
      currency
    }
    total
  }
}
```

### 4. Test Hot Reload

1. **Start the application**: `./gradlew bootRun`
2. **Make a code change** (e.g., modify a log message in a service)
3. **Save the file** - Application should automatically restart
4. **Verify data persists**: Query the API - sample data should still be there
5. **Check logs**: Should see "Restarting..." message

### 5. Verify Database Persistence

1. **Start application** - Sample data is created
2. **Stop application** (Ctrl+C)
3. **Restart application** - Sample data should still be present
4. **Check database file**: `./data/paymentdb.mv.db` should exist

### 6. Access H2 Console

1. Navigate to: `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:file:./data/paymentdb`
3. Username: `sa`
4. Password: (empty)
5. Click "Connect"
6. Run queries:
   ```sql
   SELECT * FROM payments;
   SELECT * FROM creditors;
   SELECT * FROM debtors;
   ```

## Testing Scenarios

### Scenario 1: CRUD Operations
1. Create a new payment with existing creditor/debtor
2. Retrieve the payment
3. List all payments (verify new one appears)
4. Filter payments (verify filtering works)

### Scenario 2: Soft Delete
1. List all creditors
2. Delete a creditor (soft delete)
3. List all creditors again (deleted one should not appear)
4. Try to retrieve deleted creditor (should return 404)
5. Try to create payment with deleted creditor (should fail)

### Scenario 3: Filtering
1. Filter payments by status (RECEIVED, PROCESSING, COMPLETED, FAILED)
2. Filter payments by currency (USD, EUR, GBP)
3. Filter payments by amount range
4. Filter creditors by name (partial match)
5. Filter debtors by bank code

### Scenario 4: Pagination
1. List payments with page=0, size=5
2. List payments with page=1, size=5
3. Verify total count is correct
4. Verify pagination works across pages

### Scenario 5: Hot Reload
1. Start application
2. Create a payment via API
3. Modify a service method (add a log statement)
4. Save the file
5. Verify application restarts
6. Verify the payment still exists
7. Create another payment (verify new code is active)

## Configuration

### Disable Sample Data Initialization

To disable automatic sample data initialization, add to `application.properties`:

```properties
app.data.initialize=false
```

### Use In-Memory Database (Test Mode)

For testing, the application uses in-memory H2 database:

```properties
spring.datasource.url=jdbc:h2:mem:paymentdb;DB_CLOSE_DELAY=-1
```

### Reset Database

To reset the database and reinitialize sample data:

1. Stop the application
2. Delete `./data/paymentdb.mv.db` file
3. Restart the application

## API Response Verification

All API responses should include:

1. **X-Request-ID** header - Unique request identifier
2. **X-Response-Time** header - Response time in milliseconds
3. **Consistent JSON structure** - Standardized response format

### Example Response

```json
{
  "total": 7,
  "payments": [
    {
      "id": "uuid-here",
      "amount": 1500.0,
      "currency": "USD",
      "status": "RECEIVED",
      "createdAt": "2025-12-10T...",
      "creditorId": "uuid-here",
      "debtorId": "uuid-here"
    }
  ]
}
```

## Troubleshooting

### Sample Data Not Created
- Check logs for initialization errors
- Verify `app.data.initialize=true` in application.properties
- Ensure database is empty (delete `./data/paymentdb.mv.db`)

### Hot Reload Not Working
- Verify DevTools is in dependencies
- Check IDE settings (auto-build enabled)
- Verify `spring.devtools.restart.enabled=true`

### Database File Not Created
- Check file permissions
- Verify `./data/` directory exists or can be created
- Check application logs for database errors

## Next Steps

1. Test all CRUD operations with sample data
2. Verify filtering and pagination
3. Test soft delete functionality
4. Verify hot reload works
5. Test GraphQL queries and mutations
6. Verify API response formats
7. Test error handling with sample data
