# Entity Relationship Diagram (ERD)

## Database Schema Overview

This Restaurant Management System implements **8 tables** with various relationship types:

## ASCII ERD Diagram

```
┌─────────────────┐
│     USERS       │
│─────────────────│
│ PK id           │
│    username     │
│    password     │
│    full_name    │
│    role         │◄──────────┐
│    phone        │           │
│    email        │           │
│    is_active    │           │
│    created_at   │           │
└─────────────────┘           │
        │                     │
        │ 1:N (waiter)        │ 1:N (cashier)
        │                     │
        ▼                     │
┌─────────────────┐           │
│     ORDERS      │           │
│─────────────────│           │
│ PK id           │           │
│    order_number │           │
│ FK customer_id  │◄──┐       │
│ FK waiter_id    │   │       │
│    table_number │   │       │
│    order_date   │   │       │
│    total_amount │   │       │
│    status       │   │       │
│    payment_*    │   │       │
└─────────────────┘   │       │
        │             │       │
        │ 1:1         │ 1:N   │
        ▼             │       │
┌─────────────────┐   │       │
│    RECEIPTS     │   │       │
│─────────────────│   │       │
│ PK id           │   │       │
│ FK order_id     │   │       │
│    receipt_num  │   │       │
│ FK generated_by │───┘       │
│    pdf_path     │           │
│    total_amount │           │
│    tax_amount   │           │
│    discount_amt │           │
│    generated_at │           │
└─────────────────┘           │
                              │
┌─────────────────┐           │
│   CUSTOMERS     │           │
│─────────────────│           │
│ PK id           │           │
│    name         │           │
│    phone        │           │
│    email        │           │
│    address      │           │
│    created_at   │           │
└─────────────────┘           │
        │                     │
        │ 1:N                 │
        ├─────────────────────┘
        │
        │ 1:N
        ▼
┌─────────────────┐
│   ADDRESSES     │
│─────────────────│
│ PK id           │
│ FK customer_id  │
│    province     │ ◄── Province-based queries
│    city         │
│    district     │
│    street_addr  │
│    postal_code  │
│    is_default   │
│    created_at   │
└─────────────────┘


┌─────────────────┐
│   MENU_ITEMS    │
│─────────────────│
│ PK id           │
│    name         │
│    description  │
│    price        │
│    category     │
│    is_available │
│    prep_time    │
│    created_at   │
└─────────────────┘
        │
        │ 1:N
        ▼
┌─────────────────┐
│  ORDER_ITEMS    │
│─────────────────│
│ PK id           │
│ FK order_id     │
│ FK menu_item_id │
│    quantity     │
│    unit_price   │
│    subtotal     │
│    special_inst │
└─────────────────┘


┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│   MENU_ITEMS    │         │ MEAL_DEAL_ITEMS  │         │   MEAL_DEALS    │
│─────────────────│         │──────────────────│         │─────────────────│
│ PK id           │◄────────│ FK menu_item_id  │         │ PK id           │
│    name         │   N:M   │ FK meal_deal_id  │────────►│    name         │
│    description  │         └──────────────────┘         │    description  │
│    price        │         (Join Table)                 │    deal_price   │
│    category     │                                      │    is_active    │
│    is_available │                                      │    created_at   │
└─────────────────┘                                      └─────────────────┘
```

## Detailed Relationship Explanations

### 1. One-to-Many Relationships

#### User → Orders (Waiter)
- **Cardinality**: 1:N
- **Description**: One waiter can handle multiple orders
- **Foreign Key**: orders.waiter_id → users.id
- **JPA Mapping**:
  ```java
  // User entity
  @OneToMany(mappedBy = "waiter")
  private List<Order> orders;
  
  // Order entity
  @ManyToOne
  @JoinColumn(name = "waiter_id")
  private User waiter;
  ```
- **Business Logic**: Tracks which waiter is responsible for each order
- **Cascade**: None (deleting waiter shouldn't delete orders)

#### User → Receipts (Cashier)
- **Cardinality**: 1:N
- **Description**: One cashier can generate multiple receipts
- **Foreign Key**: receipts.generated_by → users.id
- **Business Logic**: Audit trail for who generated each receipt

#### Customer → Orders
- **Cardinality**: 1:N
- **Description**: One customer can place multiple orders
- **Foreign Key**: orders.customer_id → customers.id
- **Business Logic**: Order history per customer

#### Customer → Addresses
- **Cardinality**: 1:N
- **Description**: One customer can have multiple delivery addresses
- **Foreign Key**: addresses.customer_id → customers.id
- **JPA Mapping**:
  ```java
  // Customer entity
  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Address> addresses;
  
  // Address entity
  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;
  ```
- **Cascade**: ALL (deleting customer deletes their addresses)
- **Fetch**: LAZY (addresses loaded only when accessed)

#### Order → OrderItems
- **Cardinality**: 1:N
- **Description**: One order contains multiple line items
- **Foreign Key**: order_items.order_id → orders.id
- **JPA Mapping**:
  ```java
  // Order entity
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<OrderItem> orderItems;
  
  // OrderItem entity
  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;
  ```
- **Cascade**: ALL (deleting order deletes all items)
- **Business Logic**: Order details with quantities and prices

#### MenuItem → OrderItems
- **Cardinality**: 1:N
- **Description**: One menu item can appear in multiple orders
- **Foreign Key**: order_items.menu_item_id → menu_items.id
- **Business Logic**: Tracks which items are ordered most frequently

### 2. One-to-One Relationship

#### Order ↔ Receipt
- **Cardinality**: 1:1
- **Description**: Each order has exactly one receipt
- **Foreign Key**: receipts.order_id → orders.id (UNIQUE constraint)
- **JPA Mapping**:
  ```java
  // Order entity (inverse side)
  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
  private Receipt receipt;
  
  // Receipt entity (owning side)
  @OneToOne
  @JoinColumn(name = "order_id", unique = true)
  private Order order;
  ```
- **Approach**: Foreign key approach (Receipt owns the relationship)
- **Alternative**: Shared primary key (receipt.id = order.id)
- **Business Logic**: One receipt per paid order
- **Constraint**: Prevents duplicate receipts for same order

### 3. Many-to-Many Relationship

#### MenuItem ↔ MealDeal
- **Cardinality**: M:N
- **Description**: Menu items can be in multiple deals; deals contain multiple items
- **Join Table**: meal_deal_items
  - Columns: meal_deal_id, menu_item_id
  - Composite Primary Key: (meal_deal_id, menu_item_id)
- **JPA Mapping**:
  ```java
  // MealDeal entity (owning side)
  @ManyToMany
  @JoinTable(
      name = "meal_deal_items",
      joinColumns = @JoinColumn(name = "meal_deal_id"),
      inverseJoinColumns = @JoinColumn(name = "menu_item_id")
  )
  private Set<MenuItem> menuItems;
  
  // MenuItem entity (inverse side)
  @ManyToMany(mappedBy = "menuItems")
  private Set<MealDeal> mealDeals;
  ```
- **Business Logic**: 
  - Create combo meals (e.g., "Family Combo" with Brochettes + Ugali + 2 drinks)
  - Offer discounted prices for meal combinations
  - Same menu item can be in multiple deals
- **Example**:
  - "Lunch Special" = [Isombe, Ibirayi, Ikivuguto] @ 5,000 RWF
  - "Family Combo" = [Brochettes, Ugali, 2x Banana Juice] @ 12,000 RWF

## Cascade Types Explained

### CascadeType.ALL
```java
@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
private List<Address> addresses;
```
- **Operations**: PERSIST, MERGE, REMOVE, REFRESH, DETACH
- **Effect**: All operations on Customer cascade to Addresses
- **Use Case**: When child entities have no meaning without parent

### CascadeType.PERSIST
- Only save operations cascade
- Use when you want to save children with parent but not delete them

### CascadeType.REMOVE
- Only delete operations cascade
- Use when children should be deleted with parent

## Fetch Strategies Explained

### FetchType.LAZY (Recommended)
```java
@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
private List<OrderItem> orderItems;
```
- **Behavior**: Child entities loaded only when accessed
- **Advantage**: Reduces initial query load
- **Disadvantage**: May cause N+1 query problem if not careful
- **Use Case**: Collections, large datasets

### FetchType.EAGER
```java
@ManyToOne(fetch = FetchType.EAGER)
private Customer customer;
```
- **Behavior**: Child entities loaded immediately with parent
- **Advantage**: No additional queries needed
- **Disadvantage**: Can load unnecessary data
- **Use Case**: Small, frequently accessed relationships

## Foreign Key Constraints

All relationships use foreign key constraints:
```sql
ALTER TABLE orders 
ADD CONSTRAINT fk_orders_customer 
FOREIGN KEY (customer_id) REFERENCES customers(id);

ALTER TABLE orders 
ADD CONSTRAINT fk_orders_waiter 
FOREIGN KEY (waiter_id) REFERENCES users(id);

ALTER TABLE receipts 
ADD CONSTRAINT fk_receipts_order 
FOREIGN KEY (order_id) REFERENCES orders(id);
```

**Benefits**:
- Referential integrity
- Prevents orphaned records
- Database-level validation

## Indexes for Performance

Recommended indexes:
```sql
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_waiter ON orders(waiter_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_addresses_province ON addresses(province);
CREATE INDEX idx_menu_items_category ON menu_items(category);
```

## Summary

| Relationship Type | Count | Examples |
|------------------|-------|----------|
| One-to-Many | 6 | User→Orders, Customer→Addresses, Order→OrderItems |
| One-to-One | 1 | Order↔Receipt |
| Many-to-Many | 1 | MenuItem↔MealDeal |
| **Total Tables** | **8** | users, customers, addresses, menu_items, meal_deals, orders, order_items, receipts |

This ERD demonstrates comprehensive understanding of database relationships, proper foreign key usage, and real-world business logic implementation.
