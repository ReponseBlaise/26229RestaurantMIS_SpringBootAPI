# Rwanda Location Hierarchy Implementation Guide

## Overview
This system implements Rwanda's administrative structure using a **self-referencing Location entity**:
```
Province → District → Sector → Cell → Village
```

## Database Structure

### Location Table (Self-Referencing)
```sql
CREATE TABLE locations (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) UNIQUE,
    type VARCHAR(20) NOT NULL, -- PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
    parent_id BIGINT REFERENCES locations(id)
);
```

### Address Table
```sql
CREATE TABLE addresses (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id),
    village_id BIGINT REFERENCES locations(id), -- Links to VILLAGE only
    street_address TEXT,
    postal_code VARCHAR(10),
    is_default BOOLEAN
);
```

## How It Works

### 1. Hierarchical Relationship
When you save an address with a **Village**, it automatically links to:
- Village → Cell → Sector → District → Province

**Example:**
```
Kimironko Village
  └─ Kimironko Cell
      └─ Remera Sector
          └─ Gasabo District
              └─ Kigali Province
```

### 2. Querying by Any Level
You can retrieve customers/addresses by:
- Province name
- District name
- Sector name
- Cell name
- Village name

## API Usage with Postman

### Step 1: Create Location Hierarchy

#### 1.1 Create Province
```http
POST http://localhost:8081/api/locations
Content-Type: application/json

{
  "name": "Kigali",
  "code": "KGL",
  "type": "PROVINCE",
  "parentId": null
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Kigali",
  "code": "KGL",
  "type": "PROVINCE",
  "province": "Kigali"
}
```

#### 1.2 Create District
```http
POST http://localhost:8081/api/locations
Content-Type: application/json

{
  "name": "Gasabo",
  "code": "KGL-GSB",
  "type": "DISTRICT",
  "parentId": 1
}
```

#### 1.3 Create Sector
```http
POST http://localhost:8081/api/locations
Content-Type: application/json

{
  "name": "Remera",
  "code": "KGL-GSB-RMR",
  "type": "SECTOR",
  "parentId": 2
}
```

#### 1.4 Create Cell
```http
POST http://localhost:8081/api/locations
Content-Type: application/json

{
  "name": "Kimironko",
  "code": "KGL-GSB-RMR-KMR",
  "type": "CELL",
  "parentId": 3
}
```

#### 1.5 Create Village
```http
POST http://localhost:8081/api/locations
Content-Type: application/json

{
  "name": "Kimironko I",
  "code": "KGL-GSB-RMR-KMR-001",
  "type": "VILLAGE",
  "parentId": 4
}
```

**Response with Full Hierarchy:**
```json
{
  "id": 5,
  "name": "Kimironko I",
  "code": "KGL-GSB-RMR-KMR-001",
  "type": "VILLAGE",
  "parentId": 4,
  "parentName": "Kimironko",
  "province": "Kigali",
  "district": "Gasabo",
  "sector": "Remera",
  "cell": "Kimironko",
  "village": "Kimironko I"
}
```

### Step 2: Create Customer with Address

#### 2.1 Create Customer
```http
POST http://localhost:8081/api/customers
Content-Type: application/json

{
  "name": "Jean Mugabo",
  "phone": "+250788123456",
  "email": "mugabo@email.rw"
}
```

#### 2.2 Create Address (Using Village ID Only)
```http
POST http://localhost:8081/api/addresses
Content-Type: application/json

{
  "customerId": 1,
  "villageId": 5,
  "streetAddress": "KG 15 Ave, House #25",
  "postalCode": "KG001",
  "isDefault": true
}
```

**✅ That's it! The address is now linked to:**
- Village: Kimironko I
- Cell: Kimironko
- Sector: Remera
- District: Gasabo
- Province: Kigali

### Step 3: Query Customers by Location

#### Get Customers by Province
```http
GET http://localhost:8081/api/customers/province/Kigali?page=0&size=10
```

#### Get Addresses by Province
```http
GET http://localhost:8081/api/addresses/province/Kigali?page=0&size=10
```

#### Get Addresses by District
```http
GET http://localhost:8081/api/addresses/district/Gasabo?page=0&size=10
```

## Location API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/locations` | Create new location |
| GET | `/api/locations` | Get all locations |
| GET | `/api/locations/{id}` | Get location by ID |
| GET | `/api/locations/code/{code}` | Get location by code |
| GET | `/api/locations/type/{type}` | Get all locations of a type |
| GET | `/api/locations/children/{parentId}` | Get child locations |

## Example: Complete Rwanda Structure

### Kigali Province
```
Kigali (Province)
├── Gasabo (District)
│   ├── Remera (Sector)
│   │   ├── Kimironko (Cell)
│   │   │   ├── Kimironko I (Village)
│   │   │   └── Kimironko II (Village)
│   │   └── Rukiri (Cell)
│   └── Kinyinya (Sector)
├── Kicukiro (District)
└── Nyarugenge (District)
```

## Benefits

1. **Data Integrity**: Enforces proper hierarchy
2. **Easy Queries**: Query by any administrative level
3. **Automatic Linking**: Village automatically links to all parent levels
4. **Scalable**: Easy to add new locations
5. **Flexible**: Can query at any level of the hierarchy

## Reference
Full Rwanda administrative structure: https://kindly-mouth-eff.notion.site/Rwanda-Location-Structure-31e08f5a673880f394e8ceb65d4be927

## Important Notes

- ✅ Always use **Village ID** when creating addresses
- ✅ Never store Province/District/Sector/Cell as separate strings
- ✅ The hierarchy is enforced: District must have Province parent, Sector must have District parent, etc.
- ✅ Location codes should be unique across the entire system
- ✅ You can query customers/addresses by any level (Province, District, Sector, Cell, Village)
