# GraphQL API Guide

## Overview

The Payment Echo System provides a complete GraphQL API alongside the REST API, allowing clients to query exactly the data they need.

## Access Points

- **GraphQL Endpoint**: `POST http://localhost:8080/graphql`
- **GraphiQL Playground**: `GET http://localhost:8080/graphiql`

## GraphQL Schema

The GraphQL schema is defined in `src/main/resources/graphql/payment.graphqls` and includes:

- **Types**: Payment, Creditor, Debtor, PaymentPage, CreditorPage, DebtorPage
- **Queries**: All read operations
- **Mutations**: All write operations

## Queries

### Get All Payments

```graphql
query {
  payments(page: 0, size: 20) {
    payments {
      id
      amount
      currency
      status
      createdAt
    }
    total
    page
    size
    totalPages
  }
}
```

### Get Payment by ID

```graphql
query {
  payment(id: "123e4567-e89b-12d3-a456-426614174000") {
    id
    amount
    currency
    status
    createdAt
  }
}
```

### Filter Payments by Status

```graphql
query {
  paymentsByStatus(status: "RECEIVED", page: 0, size: 20) {
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

### Filter Payments by Currency

```graphql
query {
  paymentsByCurrency(currency: "USD", page: 0, size: 20) {
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

### Filter Payments by Amount Range

```graphql
query {
  paymentsByAmountRange(
    minAmount: 100.0
    maxAmount: 1000.0
    page: 0
    size: 20
  ) {
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

### Get All Creditors

```graphql
query {
  creditors(page: 0, size: 20) {
    creditors {
      id
      name
      accountNumber
      bankCode
      email
    }
    total
  }
}
```

### Filter Creditors by Name

```graphql
query {
  creditorsByName(name: "John", page: 0, size: 20) {
    creditors {
      id
      name
      accountNumber
      bankCode
    }
    total
  }
}
```

### Get All Debtors

```graphql
query {
  debtors(page: 0, size: 20) {
    debtors {
      id
      name
      accountNumber
      bankCode
      email
    }
    total
  }
}
```

## Mutations

### Create Payment

```graphql
mutation {
  createPayment(input: {
    amount: 100.50
    currency: "USD"
    status: "RECEIVED"
  }) {
    id
    amount
    currency
    status
    createdAt
  }
}
```

### Create Payment with Creditor and Debtor

```graphql
mutation {
  createPayment(input: {
    amount: 200.75
    currency: "EUR"
    status: "PROCESSING"
    creditorId: "123e4567-e89b-12d3-a456-426614174000"
    debtorId: "223e4567-e89b-12d3-a456-426614174000"
  }) {
    id
    amount
    currency
    status
    creditorId
    debtorId
  }
}
```

### Echo Payment

```graphql
mutation {
  echoPayment(input: {
    amount: 150.0
    currency: "GBP"
    status: "COMPLETED"
  }) {
    id
    amount
    currency
    status
  }
}
```

### Create Creditor

```graphql
mutation {
  createCreditor(input: {
    name: "John Doe"
    accountNumber: "1234567890"
    bankCode: "BANK001"
    email: "john@example.com"
    address: "123 Main St"
  }) {
    id
    name
    accountNumber
    bankCode
    email
  }
}
```

### Delete Creditor (Soft Delete)

```graphql
mutation {
  deleteCreditor(id: "123e4567-e89b-12d3-a456-426614174000")
}
```

### Create Debtor

```graphql
mutation {
  createDebtor(input: {
    name: "Jane Smith"
    accountNumber: "0987654321"
    bankCode: "BANK002"
    email: "jane@example.com"
  }) {
    id
    name
    accountNumber
    bankCode
  }
}
```

### Delete Debtor (Soft Delete)

```graphql
mutation {
  deleteDebtor(id: "223e4567-e89b-12d3-a456-426614174000")
}
```

## Using GraphiQL

1. Start the application: `./gradlew bootRun`
2. Navigate to: `http://localhost:8080/graphiql`
3. Use the interactive playground to:
   - Explore the schema
   - Write and test queries
   - View query documentation
   - See query execution results

## Using curl

### Query Example

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { payments { payments { id amount currency status } total } }"
  }'
```

### Mutation Example

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { createPayment(input: { amount: 100.0 currency: \"USD\" status: \"RECEIVED\" }) { id amount currency status } }"
  }'
```

## Benefits of GraphQL

1. **Flexible Queries**: Request only the fields you need
2. **Single Endpoint**: One endpoint for all operations
3. **Type Safety**: Strongly typed schema
4. **Introspection**: Self-documenting API
5. **Reduced Over-fetching**: Get exactly what you need
6. **Efficient**: Combine multiple queries in one request

## Comparison with REST API

| Feature | REST API | GraphQL |
|---------|----------|---------|
| Endpoints | Multiple endpoints | Single endpoint |
| Data Fetching | Fixed response structure | Flexible field selection |
| Over-fetching | Possible | Avoided |
| Under-fetching | Possible | Avoided |
| Caching | HTTP caching | More complex |
| Versioning | URL-based | Schema-based |

## Best Practices

1. **Use GraphQL for**:
   - Complex queries with multiple related entities
   - Mobile applications needing flexible data fetching
   - Applications requiring real-time updates
   - When you need to reduce over-fetching

2. **Use REST API for**:
   - Simple CRUD operations
   - When HTTP caching is important
   - File uploads/downloads
   - When you need standard HTTP status codes

## Error Handling

GraphQL errors are returned in the `errors` field of the response:

```json
{
  "data": null,
  "errors": [
    {
      "message": "Payment not found with id: 123",
      "locations": [{"line": 2, "column": 3}],
      "path": ["payment"]
    }
  ]
}
```

## Testing

GraphQL endpoints are tested in `GraphQLIntegrationTest.kt`:

- Query tests (payments, creditors, debtors)
- Filter tests (status, currency, amount range)
- Mutation tests (create, delete)
- Error handling tests

Run tests:
```bash
./gradlew test --tests "*GraphQL*"
```
