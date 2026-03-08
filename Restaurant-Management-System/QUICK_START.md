# Quick Start Guide

## Prerequisites
- ✅ Java 21 installed
- ✅ PostgreSQL 12+ installed and running
- ✅ Maven 3.8+ installed
- ✅ Postman or cURL for API testing

## Step 1: Database Setup

### Create Database
```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE restaurant_db;

-- Exit psql
\q
```

### Verify Connection
```bash
psql -U postgres -d restaurant_db -c "SELECT version();"
```

## Step 2: Configure Application

### Update Database Credentials
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/restaurant_db
spring.datasource.username=postgres
spring.datasource.password=your_password_here
```

## Step 3: Build and Run

### Option A: Using Maven
```bash
cd Restaurant-Management-System

# Clean and build
mvn clean install

# Run application
mvn spring-boot:run
```

### Option B: Using JAR
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/Restaurant-Management-System-0.0.1-SNAPSHOT.jar
```

### Expected Output
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

Restaurant-Management-System : (v0.0.1-SNAPSHOT)

2024-01-15 10:30:00.000  INFO --- [main] RestaurantManagementSystemApplication : Started RestaurantManagementSystemApplication in 5.123 seconds
```

## Step 4: Verify Application

### Check Health
```bash
curl http://localhost:8080/api/users
```

Expected: Empty list or sample users (if DataInitializer ran)

### Check Database
```sql
psql -U postgres -d restaurant_db

-- List tables
\dt

-- Check sample data
SELECT * FROM users;
SELECT * FROM menu_items;
SELECT * FROM customers;
```

## Step 5: Test API Endpoints

### 1. Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_waiter",
    "password": "password123",
    "fullName": "Test Waiter",
    "role": "WAITER",
    "phone": "+250788999999",
    "email": "test@restaurant.rw"
  }'
```

### 2. Get All Users
```bash
curl http://localhost:8080/api/users?page=0&size=10
```

### 3. Create Menu Item
```bash
curl -X POST http://localhost:8080/api/menu-items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Dish",
    "description": "Test description",
    "price": 5000,
    "category": "Main Course",
    "isAvailable": true,
    "preparationTime": 20
  }'
```

### 4. Get Menu Items (with sorting)
```bash
curl "http://localhost:8080/api/menu-items?page=0&size=10&sortBy=price&sortDir=asc"
```

### 5. Create Customer
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Customer",
    "phone": "+250788888888",
    "email": "customer@test.rw",
    "address": "Kigali"
  }'
```

### 6. Create Address
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "province": "Kigali",
    "city": "Kigali City",
    "district": "Gasabo",
    "streetAddress": "KG 10 Ave",
    "postalCode": "KG001",
    "isDefault": true
  }'
```

### 7. Query Customers by Province
```bash
curl http://localhost:8080/api/customers/province/Kigali
```

### 8. Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 2" \
  -d '{
    "customerId": 1,
    "tableNumber": 5,
    "items": [
      {
        "menuItemId": 1,
        "quantity": 2,
        "specialInstructions": "Extra spicy"
      }
    ]
  }'
```

## Step 6: Sample Data

The application automatically initializes sample data on first run:

### Users
- **Manager**: mugisha_manager / password123
- **Waiter**: uwase_waiter / password123
- **Cashier**: mukamana_cashier / password123

### Menu Items
- Isombe - 3,500 RWF
- Brochettes - 5,000 RWF
- Ugali - 2,000 RWF
- Sambaza - 4,000 RWF
- Ibirayi - 1,500 RWF
- Ikivuguto - 1,000 RWF
- Banana Juice - 1,500 RWF
- Matoke - 3,000 RWF

### Customers
- Patrick Habimana (+250788567890)
- Claudine Uwera (+250788678901)
- Samuel Niyonzima (+250788789012)

## Troubleshooting

### Issue: Port 8080 already in use
**Solution:** Change port in `application.properties`
```properties
server.port=8081
```

### Issue: Database connection failed
**Solution:** Verify PostgreSQL is running
```bash
# Windows
pg_ctl status

# Linux/Mac
sudo systemctl status postgresql
```

### Issue: Lombok not working
**Solution:** Enable annotation processing in IDE
- IntelliJ: Settings → Build → Compiler → Annotation Processors → Enable
- Eclipse: Install Lombok plugin

### Issue: Tables not created
**Solution:** Check Hibernate DDL setting
```properties
spring.jpa.hibernate.ddl-auto=update
```

### Issue: Sample data not loaded
**Solution:** Check DataInitializer logs
```bash
# Look for this in logs
INFO --- DataInitializer : Initializing sample data...
```

## Development Tips

### View SQL Queries
Enable SQL logging in `application.properties`:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Hot Reload (Spring Boot DevTools)
Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

### Database GUI Tools
- **pgAdmin**: https://www.pgadmin.org/
- **DBeaver**: https://dbeaver.io/
- **DataGrip**: https://www.jetbrains.com/datagrip/

### API Testing Tools
- **Postman**: https://www.postman.com/
- **Insomnia**: https://insomnia.rest/
- **Thunder Client** (VS Code extension)

## Next Steps

1. ✅ Read `README.md` for complete API documentation
2. ✅ Review `ERD_DOCUMENTATION.md` for database relationships
3. ✅ Check `API_TESTING_GUIDE.md` for testing scenarios
4. ✅ Explore `ASSESSMENT_CRITERIA.md` for implementation details
5. ✅ Import `database_schema.sql` for manual database setup

## API Documentation

Once running, access:
- **Base URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/api/users
- **Sample Request**: http://localhost:8080/api/menu-items?page=0&size=10

## Support

For issues or questions:
1. Check application logs
2. Verify database connection
3. Review error messages
4. Check `GlobalExceptionHandler` for error details

## Success Indicators

✅ Application starts without errors
✅ Database tables created automatically
✅ Sample data loaded
✅ API endpoints respond correctly
✅ Pagination and sorting work
✅ Province-based queries return results
✅ Relationships properly established

## Performance Monitoring

### Check Database Connections
```sql
SELECT count(*) FROM pg_stat_activity WHERE datname = 'restaurant_db';
```

### Monitor Query Performance
```sql
SELECT query, calls, total_time, mean_time 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;
```

### Application Metrics
Add Spring Boot Actuator:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Access: http://localhost:8080/actuator/health

## Production Considerations

Before deploying to production:
1. ✅ Change default passwords
2. ✅ Enable security (Spring Security)
3. ✅ Use environment variables for credentials
4. ✅ Set `spring.jpa.hibernate.ddl-auto=validate`
5. ✅ Enable HTTPS
6. ✅ Configure connection pooling
7. ✅ Set up monitoring and logging
8. ✅ Implement rate limiting
9. ✅ Add API authentication (JWT)
10. ✅ Configure CORS properly

## Congratulations! 🎉

Your Restaurant Management System is now running!

Start testing the API endpoints and explore the features:
- ✅ User management with roles
- ✅ Menu items and meal deals
- ✅ Customer and address management
- ✅ Order processing
- ✅ Receipt generation
- ✅ Province-based queries
- ✅ Pagination and sorting
- ✅ All database relationships

Happy coding! 🚀
