# API Testing Guide

## Base URL
```
http://localhost:8080
```

## Testing Sequence

### 1. Create Users (Manager creates staff)

#### Create Manager
```http
POST /api/users
Content-Type: application/json

{
  "username": "mugisha_manager",
  "password": "password123",
  "fullName": "Jean Paul Mugisha",
  "role": "MANAGER",
  "phone": "+250788123456",
  "email": "mugisha@restaurant.rw"
}
```

#### Create Waiter
```http
POST /api/users
Content-Type: application/json

{
  "username": "uwase_waiter",
  "password": "password123",
  "fullName": "Marie Uwase",
  "role": "WAITER",
  "phone": "+250788234567",
  "email": "uwase@restaurant.rw"
}
```

#### Create Cashier
```http
POST /api/users
Content-Type: application/json

{
  "username": "mukamana_cashier",
  "password": "password123",
  "fullName": "Grace Mukamana",
  "role": "CASHIER",
  "phone": "+250788456789",
  "email": "mukamana@restaurant.rw"
}
```

### 2. Create Menu Items (Manager only)

#### Create Isombe
```http
POST /api/menu-items
Content-Type: application/json

{
  "name": "Isombe",
  "description": "Cassava leaves cooked with peanut sauce",
  "price": 3500,
  "category": "Main Course",
  "isAvailable": true,
  "preparationTime": 30
}
```

#### Create Brochettes
```http
POST /api/menu-items
Content-Type: application/json

{
  "name": "Brochettes",
  "description": "Grilled meat skewers",
  "price": 5000,
  "category": "Main Course",
  "isAvailable": true,
  "preparationTime": 25
}
```

#### Create Ikivuguto
```http
POST /api/menu-items
Content-Type: application/json

{
  "name": "Ikivuguto",
  "description": "Traditional fermented milk",
  "price": 1000,
  "category": "Beverage",
  "isAvailable": true,
  "preparationTime": 5
}
```

### 3. Create Meal Deal (Many-to-Many)

```http
POST /api/menu-items/meal-deals
Content-Type: application/json

{
  "name": "Lunch Special",
  "description": "Isombe, Ibirayi, and Ikivuguto",
  "dealPrice": 5000,
  "menuItemIds": [1, 5, 6],
  "isActive": true
}
```

### 4. Create Customers

#### Customer 1
```http
POST /api/customers
Content-Type: application/json

{
  "name": "Patrick Habimana",
  "phone": "+250788567890",
  "email": "habimana@email.rw",
  "villageId": 5,
  "streetAddress": "KG 15 Ave, House #25"
}
```

**Note:** `villageId` is optional. You can create customers without location data.

#### Customer 2
```http
POST /api/customers
Content-Type: application/json

{
  "name": "Claudine Uwera",
  "phone": "+250788678901",
  "email": "uwera@email.rw",
  "villageId": 5,
  "streetAddress": "KN 5 Rd, Kigali"
}
```

### 5. Create Addresses

#### Address for Customer 1 (Kigali)
```http
POST /api/addresses
Content-Type: application/json

{
  "customerId": 1,
  "province": "Kigali",
  "city": "Kigali City",
  "district": "Gasabo",
  "streetAddress": "KG 15 Ave, Kimironko",
  "postalCode": "KG001",
  "isDefault": true
}
```

#### Address for Customer 2 (Southern Province)
```http
POST /api/addresses
Content-Type: application/json

{
  "customerId": 2,
  "province": "Southern",
  "city": "Huye",
  "district": "Huye",
  "streetAddress": "Butare Town, Main Street",
  "postalCode": "HY001",
  "isDefault": true
}
```

### 6. Create Order (Waiter only)

```http
POST /api/orders
Content-Type: application/json
X-User-Id: 2

{
  "customerId": 1,
  "tableNumber": 5,
  "items": [
    {
      "menuItemId": 1,
      "quantity": 2,
      "specialInstructions": "Extra spicy"
    },
    {
      "menuItemId": 3,
      "quantity": 1,
      "specialInstructions": "No salt"
    },
    {
      "menuItemId": 6,
      "quantity": 2
    }
  ]
}
```

### 7. Query Operations

#### Get All Users (with pagination and sorting)
```http
GET /api/users?page=0&size=10&sortBy=fullName&sortDir=asc
```

#### Get All Menu Items (sorted by price)
```http
GET /api/menu-items?page=0&size=10&sortBy=price&sortDir=asc
```

#### Get Menu Items by Category
```http
GET /api/menu-items/category/Main%20Course?page=0&size=10
```

#### Get All Customers
```http
GET /api/customers?page=0&size=10&sortBy=name&sortDir=asc
```

#### Get Customers by Province (Province-based query)
```http
GET /api/customers/province/Kigali
```

#### Get Customers by Province (Paginated)
```http
GET /api/customers/province/Kigali/paginated?page=0&size=10
```

#### Get Addresses by Province
```http
GET /api/addresses/province/Kigali?page=0&size=10
```

#### Get All Orders
```http
GET /api/orders?page=0&size=10&sortBy=orderDate&sortDir=desc
```

#### Get Orders by Waiter
```http
GET /api/orders/waiter/2?page=0&size=10
```

#### Get Order by ID
```http
GET /api/orders/1
```

#### Get All Meal Deals
```http
GET /api/menu-items/meal-deals?page=0&size=10
```

### 8. Update Operations

#### Update Order Status
```http
PUT /api/orders/1/status?status=CONFIRMED
```

Status flow: PENDING → CONFIRMED → PREPARING → READY → SERVED → PAID

#### Update Menu Item
```http
PUT /api/menu-items/1
Content-Type: application/json

{
  "name": "Isombe",
  "description": "Cassava leaves cooked with peanut sauce - Updated",
  "price": 3800,
  "category": "Main Course",
  "isAvailable": true,
  "preparationTime": 30
}
```

#### Update Customer
```http
PUT /api/customers/1
Content-Type: application/json

{
  "name": "Patrick Habimana",
  "phone": "+250788567890",
  "email": "habimana.updated@email.rw",
  "villageId": 5,
  "streetAddress": "KG 15 Ave, Kigali - Updated"
}
```

### 9. Generate Receipt (Cashier only)

First, update order to PAID status:
```http
PUT /api/orders/1/status?status=PAID
```

Then generate receipt:
```http
POST /api/receipts/order/1
X-User-Id: 3
```

### 10. Download Receipt

```http
GET /api/receipts/1/download
```

This will download a text file with the receipt details.

### 11. Delete Operations

#### Delete Menu Item
```http
DELETE /api/menu-items/1
```

#### Delete User
```http
DELETE /api/users/1
```

#### Delete Customer
```http
DELETE /api/customers/1
```

## Testing Scenarios

### Scenario 1: Complete Order Flow
1. Create customer
2. Create address for customer
3. Waiter creates order
4. Update order status: PENDING → CONFIRMED → PREPARING → READY → SERVED → PAID
5. Cashier generates receipt
6. Download receipt

### Scenario 2: Province-based Customer Query
1. Create multiple customers with addresses in different provinces
2. Query customers by province: Kigali, Southern, Eastern, etc.
3. Verify only customers with addresses in that province are returned

### Scenario 3: Meal Deal (Many-to-Many)
1. Create multiple menu items
2. Create meal deal with selected menu items
3. Retrieve meal deal and verify all items are included
4. Calculate savings (sum of individual prices vs deal price)

### Scenario 4: Pagination and Sorting
1. Create 25+ menu items
2. Request page 0 with size 10, sorted by price ascending
3. Request page 1 with size 10
4. Verify pagination metadata (totalElements, totalPages, etc.)

### Scenario 5: Validation Testing
1. Try creating user with existing username (should fail)
2. Try creating customer with existing phone (should fail)
3. Try creating order with invalid menu item (should fail)
4. Try generating receipt for unpaid order (should fail)

## Expected Responses

### Success Response (201 Created)
```json
{
  "id": 1,
  "name": "Isombe",
  "price": 3500,
  "category": "Main Course",
  "isAvailable": true
}
```

### Paginated Response (200 OK)
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 25,
  "totalPages": 3,
  "last": false,
  "first": true
}
```

### Error Response (400 Bad Request)
```json
{
  "status": 400,
  "message": "Username already exists",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Validation Error (400 Bad Request)
```json
{
  "status": 400,
  "errors": {
    "username": "Username is required",
    "price": "Price must be greater than 0"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Not Found (404)
```json
{
  "status": 404,
  "message": "Customer not found with id: 99",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Tips for Testing

1. **Use Postman Collections**: Save all requests in a collection for easy reuse
2. **Environment Variables**: Set base URL and common IDs as variables
3. **Test Order**: Follow the sequence above to ensure data dependencies
4. **Check Logs**: Monitor application logs for SQL queries and errors
5. **Database Verification**: Use pgAdmin or psql to verify data in database

## Common Issues

### Issue: Foreign Key Constraint Violation
**Solution**: Ensure referenced entities exist before creating dependent entities

### Issue: Unique Constraint Violation
**Solution**: Use different usernames, emails, or phone numbers

### Issue: Validation Errors
**Solution**: Check request body matches DTO validation rules

### Issue: 404 Not Found
**Solution**: Verify entity IDs exist in database

## Performance Testing

Test pagination with large datasets:
```bash
# Create 100 menu items
for i in {1..100}; do
  curl -X POST http://localhost:8080/api/menu-items \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"Item $i\",\"price\":$i00,\"category\":\"Test\"}"
done

# Test pagination performance
curl "http://localhost:8080/api/menu-items?page=0&size=10&sortBy=price&sortDir=asc"
```

## Security Testing

Test role-based access:
1. Try creating order with non-waiter user (should fail)
2. Try generating receipt with non-cashier user (should fail)
3. Test with missing X-User-Id header

Note: Current implementation uses simple header-based authentication. In production, implement JWT or OAuth2.
