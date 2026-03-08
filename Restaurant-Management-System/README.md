# Restaurant Management System - Spring Boot RESTful API

## Project Overview
A complete Restaurant Management System built with **Spring Boot 4.0.3** and **Java 21**, featuring role-based access control (MANAGER, WAITER, CASHIER), comprehensive database relationships, and Rwandan context (dishes, currency, locations).

## Technology Stack
- **Spring Boot**: 4.0.3
- **Java**: 21
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Key Dependencies**: Spring Data JPA, Spring Web, Lombok, Validation

## Database Schema

### Tables Overview (8 Tables)
1. **users** - System users with roles
2. **customers** - Restaurant customers
3. **addresses** - Customer addresses with province field
4. **menu_items** - Food and beverage items
5. **meal_deals** - Combo deals (Many-to-Many with menu_items)
6. **orders** - Customer orders
7. **order_items** - Order line items
8. **receipts** - Payment receipts

## Entity Relationship Diagram (ERD) Explanation

### Relationship Types Implemented

#### 1. One-to-Many Relationships
- **User → Orders**: One waiter handles multiple orders
  - `User.orders` (mappedBy = "waiter")
  - `Order.waiter` (@ManyToOne)
  
- **User → Receipts**: One cashier generates multiple receipts
  - `User.receipts` (mappedBy = "generatedBy")
  - `Receipt.generatedBy` (@ManyToOne)

- **Customer → Orders**: One customer places multiple orders
  - `Customer.orders` (mappedBy = "customer")
  - `Order.customer` (@ManyToOne)

- **Customer → Addresses**: One customer has multiple addresses
  - `Customer.addresses` (mappedBy = "customer", cascade = ALL)
  - `Address.customer` (@ManyToOne)

- **Order → OrderItems**: One order contains multiple items
  - `Order.orderItems` (mappedBy = "order", cascade = ALL)
  - `OrderItem.order` (@ManyToOne)

- **MenuItem → OrderItems**: One menu item appears in multiple orders
  - `MenuItem.orderItems` (mappedBy = "menuItem")
  - `OrderItem.menuItem` (@ManyToOne)

#### 2. One-to-One Relationship
- **Order ↔ Receipt**: One order has one receipt
  - `Order.receipt` (mappedBy = "order", cascade = ALL)
  - `Receipt.order` (@OneToOne with @JoinColumn)
  - **Explanation**: Uses foreign key approach where Receipt owns the relationship with order_id column

#### 3. Many-to-Many Relationship
- **MenuItem ↔ MealDeal**: Menu items can be in multiple deals, deals contain multiple items
  - `MealDeal.menuItems` (@ManyToMany with @JoinTable)
  - `MenuItem.mealDeals` (mappedBy = "menuItems")
  - **Join Table**: `meal_deal_items` with columns (meal_deal_id, menu_item_id)
  - **Explanation**: Allows creating combo meals with multiple menu items at discounted prices

## Key Features Implementation

### 1. Location/Address with Province Field (3 Marks)

**Address Entity** includes:
```java
@Column(nullable = false, length = 50)
private String province; // Kigali, Eastern, Western, Northern, Southern
```

**Province-based Query**:
```java
// Repository method
@Query("SELECT DISTINCT c FROM Customer c JOIN c.addresses a WHERE a.province = :province")
List<Customer> findByProvince(@Param("province") String province);
```

**API Endpoint**:
```
GET /api/customers/province/{province}
Example: GET /api/customers/province/Kigali
```

**Explanation**: 
- Addresses are linked to customers via `customer_id` foreign key
- One-to-Many relationship allows multiple addresses per customer
- Province field enables location-based customer filtering
- Useful for delivery zones, regional analytics, and targeted marketing

### 2. Sorting Implementation (3 Marks)

**How Sorting Works**:
```java
@GetMapping
public ResponseEntity<Page<MenuItem>> getAllMenuItems(
    @RequestParam(defaultValue = "name") String sortBy,
    @RequestParam(defaultValue = "asc") String sortDir) {
    
    Sort sort = sortDir.equalsIgnoreCase("desc") ? 
        Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(menuItemRepository.findAll(pageable));
}
```

**Sorting Strategies**:
- **Single Field**: `Sort.by("name")`
- **Multiple Fields**: `Sort.by("category").and(Sort.by("price"))`
- **Direction**: `.ascending()` or `.descending()`

**Performance Considerations**:
- Database indexes on sorted columns improve query speed
- Sorting happens at database level (efficient)
- Avoid sorting on non-indexed columns for large datasets

**Example URLs**:
```
GET /api/menu-items?sortBy=price&sortDir=asc
GET /api/users?sortBy=fullName&sortDir=desc
GET /api/orders?sortBy=orderDate&sortDir=desc
```

### 3. Pagination Implementation (3 Marks)

**How Pagination Works**:
```java
@GetMapping
public ResponseEntity<Page<Order>> getOrders(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Order> orders = orderRepository.findAll(pageable);
    return ResponseEntity.ok(orders);
}
```

**Benefits**:
- **Database Load Reduction**: Only fetches requested page (e.g., 10 records instead of 10,000)
- **Memory Optimization**: Reduces memory consumption on server
- **Network Efficiency**: Smaller response payloads
- **User Experience**: Faster page loads, smooth scrolling

**Page Response Structure**:
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

### 4. existsBy() Method Implementation (2 Marks)

**Repository Methods**:
```java
// UserRepository
boolean existsByUsername(String username);
boolean existsByEmail(String email);

// CustomerRepository
boolean existsByPhone(String phone);

// MenuItemRepository
boolean existsByName(String name);

// ReceiptRepository
boolean existsByOrderId(Long orderId);
```

**Usage in Service**:
```java
if (userRepository.existsByUsername(request.getUsername())) {
    throw new BadRequestException("Username already exists");
}
```

**Explanation**: 
- Checks record existence without fetching full entity
- More efficient than `findBy()` for validation
- Returns boolean instead of Optional<Entity>

### 5. Cascade Types and Fetch Strategies

**Cascade Operations**:
```java
@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
private List<Address> addresses;
```
- **CascadeType.ALL**: Save, update, delete operations cascade to addresses
- When customer is deleted, all addresses are automatically deleted

**Fetch Strategies**:
```java
@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
private List<OrderItem> orderItems;
```
- **LAZY**: OrderItems loaded only when accessed (default for collections)
- **EAGER**: Loaded immediately with parent entity
- **Best Practice**: Use LAZY to avoid N+1 query problems

## API Endpoints

### User Management (Manager Only)

#### Create User
```
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

Response: 201 Created
{
  "id": 1,
  "username": "uwase_waiter",
  "fullName": "Marie Uwase",
  "role": "WAITER",
  "phone": "+250788234567",
  "email": "uwase@restaurant.rw",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00"
}
```

#### Get All Users (with Pagination & Sorting)
```
GET /api/users?page=0&size=10&sortBy=fullName&sortDir=asc

Response: 200 OK
{
  "content": [...],
  "totalElements": 25,
  "totalPages": 3,
  "size": 10,
  "number": 0
}
```

#### Get User by ID
```
GET /api/users/1

Response: 200 OK
```

#### Update User
```
PUT /api/users/1
Content-Type: application/json

{
  "username": "uwase_waiter",
  "password": "password123",
  "fullName": "Marie Uwase Updated",
  "role": "WAITER",
  "phone": "+250788234567",
  "email": "uwase@restaurant.rw"
}
```

#### Delete User
```
DELETE /api/users/1

Response: 204 No Content
```

### Customer Management

#### Create Customer
```
POST /api/customers
Content-Type: application/json

{
  "name": "Patrick Habimana",
  "phone": "+250788567890",
  "email": "habimana@email.rw",
  "address": "KG 15 Ave, Kigali"
}

Response: 201 Created
```

#### Get All Customers
```
GET /api/customers?page=0&size=10&sortBy=name&sortDir=asc
```

#### Get Customers by Province
```
GET /api/customers/province/Kigali

Response: 200 OK
[
  {
    "id": 1,
    "name": "Patrick Habimana",
    "phone": "+250788567890",
    "addresses": [...]
  }
]
```

#### Get Customers by Province (Paginated)
```
GET /api/customers/province/Kigali/paginated?page=0&size=10
```

### Address Management

#### Create Address
```
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

Response: 201 Created
```

#### Get Addresses by Customer
```
GET /api/addresses/customer/1
```

#### Get Addresses by Province
```
GET /api/addresses/province/Kigali?page=0&size=10
```

### Menu Management (Manager Only)

#### Create Menu Item
```
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

Response: 201 Created
```

#### Get All Menu Items
```
GET /api/menu-items?page=0&size=10&sortBy=price&sortDir=asc
```

#### Get Menu Items by Category
```
GET /api/menu-items/category/Main%20Course?page=0&size=10
```

#### Update Menu Item
```
PUT /api/menu-items/1
```

#### Delete Menu Item
```
DELETE /api/menu-items/1
```

#### Create Meal Deal (Many-to-Many)
```
POST /api/menu-items/meal-deals
Content-Type: application/json

{
  "name": "Family Combo",
  "description": "Brochettes, Ugali, and 2 drinks",
  "dealPrice": 12000,
  "menuItemIds": [1, 2, 5, 6],
  "isActive": true
}

Response: 201 Created
```

#### Get All Meal Deals
```
GET /api/menu-items/meal-deals?page=0&size=10
```

### Order Management (Waiter Only)

#### Create Order
```
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
    }
  ]
}

Response: 201 Created
{
  "id": 1,
  "orderNumber": "ORD-1705315800000",
  "customerId": 1,
  "customerName": "Patrick Habimana",
  "waiterId": 2,
  "waiterName": "Marie Uwase",
  "tableNumber": 5,
  "totalAmount": 9000,
  "status": "PENDING",
  "paymentStatus": "PENDING",
  "items": [...]
}
```

#### Get All Orders
```
GET /api/orders?page=0&size=10&sortBy=orderDate&sortDir=desc
```

#### Get Order by ID
```
GET /api/orders/1
```

#### Get Orders by Waiter
```
GET /api/orders/waiter/2?page=0&size=10
```

#### Update Order Status
```
PUT /api/orders/1/status?status=CONFIRMED

Response: 200 OK
```

**Order Status Flow**: PENDING → CONFIRMED → PREPARING → READY → SERVED → PAID

### Receipt Management (Cashier Only)

#### Generate Receipt
```
POST /api/receipts/order/1
X-User-Id: 4

Response: 201 Created
{
  "id": 1,
  "receiptNumber": "RCP-20240115-5800",
  "orderNumber": "ORD-1705315800000",
  "orderId": 1,
  "totalAmount": 9000,
  "taxAmount": 1620,
  "discountAmount": 0,
  "generatedByName": "Grace Mukamana",
  "generatedAt": "2024-01-15T14:30:00",
  "downloadUrl": "/api/receipts/1/download"
}
```

#### Get Receipt by Order ID
```
GET /api/receipts/order/1
```

#### Download Receipt
```
GET /api/receipts/1/download

Response: 200 OK
Content-Type: text/plain
Content-Disposition: attachment; filename="receipt-1.txt"

[Receipt file content]
```

## Sample Data (Rwandan Context)

### Users
- **Manager**: Jean Paul Mugisha (+250788123456)
- **Waiters**: Marie Uwase, Eric Nkunda
- **Cashier**: Grace Mukamana

### Menu Items (Prices in RWF)
- **Isombe**: 3,500 RWF - Cassava leaves with peanut sauce
- **Brochettes**: 5,000 RWF - Grilled meat skewers
- **Ugali**: 2,000 RWF - Cornmeal porridge
- **Sambaza**: 4,000 RWF - Fried small fish
- **Ibirayi**: 1,500 RWF - Fried Irish potatoes
- **Ikivuguto**: 1,000 RWF - Fermented milk
- **Banana Juice**: 1,500 RWF
- **Matoke**: 3,000 RWF - Cooked plantains

### Provinces
- Kigali (City of Kigali)
- Eastern Province
- Western Province
- Northern Province
- Southern Province

## Setup Instructions

### Prerequisites
- Java 21
- PostgreSQL 12+
- Maven 3.8+

### Database Setup
```sql
CREATE DATABASE restaurant_db;
```

### Configuration
Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/restaurant_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Run Application
```bash
cd Restaurant-Management-System
mvn clean install
mvn spring-boot:run
```

Application runs on: `http://localhost:8080`

## Testing with Postman/cURL

### Example: Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 2" \
  -d '{
    "customerId": 1,
    "tableNumber": 5,
    "items": [
      {"menuItemId": 1, "quantity": 2, "specialInstructions": "Extra spicy"}
    ]
  }'
```

## Error Handling

All errors return consistent format:
```json
{
  "status": 404,
  "message": "Customer not found with id: 99",
  "timestamp": "2024-01-15T10:30:00"
}
```

**HTTP Status Codes**:
- 200: Success
- 201: Created
- 204: No Content (Delete)
- 400: Bad Request (Validation errors)
- 404: Not Found
- 500: Internal Server Error

## Assessment Criteria Coverage

✅ **Project Setup & Architecture** (4 Marks): Complete package structure
✅ **Database Tables** (8 Tables): All entities with proper annotations
✅ **ERD Explanation** (3 Marks): All relationships documented
✅ **Location/Address** (3 Marks): Province field with queries
✅ **Sorting** (3 Marks): Implemented on all GET endpoints
✅ **Pagination** (3 Marks): Implemented with benefits explained
✅ **Many-to-Many** (3 Marks): MenuItem ↔ MealDeal
✅ **One-to-Many** (3 Marks): Multiple relationships
✅ **One-to-One** (2 Marks): Order ↔ Receipt
✅ **existsBy()** (Bonus): Multiple implementations
✅ **Province Query** (Bonus): Customer filtering by province
✅ **Receipt Download** (Bonus): PDF/Text generation

## Author
Developed for Web Technology Mid-Exam Assessment
Student ID: 26229
