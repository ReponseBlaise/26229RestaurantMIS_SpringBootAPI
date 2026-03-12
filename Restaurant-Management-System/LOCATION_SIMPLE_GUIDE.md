# Simple Location Usage Guide

## Quick Start: 3 Steps to Create a Customer with Location

### Step 1: Create Location Hierarchy (One-time setup)

You need to populate the location hierarchy from Province down to Village. Here's an example for Kigali:

```json
// 1. Create Province
POST http://localhost:8081/api/locations
{
  "name": "Kigali",
  "code": "KGL",
  "type": "PROVINCE",
  "parentId": null
}
// Response: { "id": 1, ... }

// 2. Create District
POST http://localhost:8081/api/locations
{
  "name": "Gasabo",
  "code": "KGL-GSB",
  "type": "DISTRICT",
  "parentId": 1
}
// Response: { "id": 2, ... }

// 3. Create Sector
POST http://localhost:8081/api/locations
{
  "name": "Remera",
  "code": "KGL-GSB-RMR",
  "type": "SECTOR",
  "parentId": 2
}
// Response: { "id": 3, ... }

// 4. Create Cell
POST http://localhost:8081/api/locations
{
  "name": "Kimironko",
  "code": "KGL-GSB-RMR-KMR",
  "type": "CELL",
  "parentId": 3
}
// Response: { "id": 4, ... }

// 5. Create Village
POST http://localhost:8081/api/locations
{
  "name": "Kimironko I",
  "code": "KGL-GSB-RMR-KMR-001",
  "type": "VILLAGE",
  "parentId": 4
}
// Response: { "id": 5, ... }
```

### Step 2: Create Customer (Use Village ID only)

```json
POST http://localhost:8081/api/customers
{
  "name": "Jean Mugabo",
  "phone": "+250788123456",
  "email": "mugabo@email.rw",
  "villageId": 5,
  "streetAddress": "KG 15 Ave, House #25"
}
```

**That's it!** The customer is automatically linked to:
- Village: Kimironko I
- Cell: Kimironko  
- Sector: Remera
- District: Gasabo
- Province: Kigali

### Step 3: Query Customers by Any Level

```bash
# By Province
GET http://localhost:8081/api/customers/province/Kigali

# By District
GET http://localhost:8081/api/customers/district/Gasabo

# By Sector
GET http://localhost:8081/api/customers/sector/Remera

# By Cell
GET http://localhost:8081/api/customers/cell/Kimironko

# By Village
GET http://localhost:8081/api/customers/village/Kimironko%20I
```

## Common Mistakes to Avoid

❌ **DON'T** store province/district as strings:
```json
{
  "name": "Jean",
  "province": "Kigali",  // WRONG!
  "district": "Gasabo"   // WRONG!
}
```

✅ **DO** use village ID:
```json
{
  "name": "Jean",
  "villageId": 5  // CORRECT!
}
```

## Why This Approach?

1. **Data Integrity**: Can't have invalid locations
2. **Easy Queries**: Query by any administrative level
3. **Automatic Linking**: Village links to all parent levels
4. **No Duplication**: Location data stored once, referenced many times
5. **Scalable**: Easy to add new locations

## Location API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/locations` | Create location |
| GET | `/api/locations` | Get all locations |
| GET | `/api/locations/{id}` | Get by ID |
| GET | `/api/locations/code/{code}` | Get by code |
| GET | `/api/locations/type/PROVINCE` | Get all provinces |
| GET | `/api/locations/type/VILLAGE` | Get all villages |
| GET | `/api/locations/children/{parentId}` | Get child locations |

## Customer API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/customers` | Create customer |
| GET | `/api/customers` | Get all customers |
| GET | `/api/customers/{id}` | Get by ID |
| GET | `/api/customers/province/{name}` | Get by province |
| GET | `/api/customers/district/{name}` | Get by district |
| GET | `/api/customers/sector/{name}` | Get by sector |
| GET | `/api/customers/cell/{name}` | Get by cell |
| GET | `/api/customers/village/{name}` | Get by village |
| PUT | `/api/customers/{id}` | Update customer |
| DELETE | `/api/customers/{id}` | Delete customer |

## Full Example Flow

```bash
# 1. Create location hierarchy (one-time)
curl -X POST http://localhost:8081/api/locations \
  -H "Content-Type: application/json" \
  -d '{"name":"Kigali","code":"KGL","type":"PROVINCE","parentId":null}'

# Get the province ID from response, then create district
curl -X POST http://localhost:8081/api/locations \
  -H "Content-Type: application/json" \
  -d '{"name":"Gasabo","code":"KGL-GSB","type":"DISTRICT","parentId":1}'

# Continue until you have a village...

# 2. Create customer with village
curl -X POST http://localhost:8081/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Jean Mugabo",
    "phone":"+250788123456",
    "email":"mugabo@email.rw",
    "villageId":5,
    "streetAddress":"KG 15 Ave"
  }'

# 3. Query customers by province
curl http://localhost:8081/api/customers/province/Kigali
```

## Reference

Full Rwanda administrative structure:  
https://kindly-mouth-eff.notion.site/Rwanda-Location-Structure-31e08f5a673880f394e8ceb65d4be927
