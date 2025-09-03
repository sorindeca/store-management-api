# Store Management API

Spring Boot API for product management.

**App:** `http://localhost:8080`  

## 📋 Features

- **CRUD Products** - Complete product management
- **Basic Authentication** - Secure authentication
- **Role-Based Access** - ADMIN, MANAGER, EMPLOYEE, USER
- **H2 Database** - Local development with console
- **Flyway Migrations** - Automatic schema versioning

## 🔐 Default Users

| User | Password | Role |
|------|----------|------|
| admin | admin123 | ADMIN |
| manager | manager123 | MANAGER |
| employee | employee123 | EMPLOYEE |
| user | user123 | USER |

## 📡 API Endpoints

**Public:**
- `GET /actuator/health` - Health check

**Protected:**
- `GET /api/products` - List products
- `GET /api/products/{id}` - Specific product
- `POST /api/products` - Create product
- `PUT /api/products/{id}` - Update product
- `PATCH /api/products/{id}/price` - Change price
- `DELETE /api/products/{id}` - Delete product

## 📁 Structure

```
src/main/java/com/sd/store/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── model/          # Entities
├── dto/            # Data transfer objects
├── config/         # Configuration
└── exception/      # Error handling
```

## 🔧 Tech Stack

- **Java 21** - Programming language
- **Spring Boot 3.5.5** - Framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Data persistence
- **Spring Validation** - Input validation
- **Spring Actuator** - Monitoring & metrics
- **H2 Database** - Local development
- **Flyway** - Database migrations
- **OpenAPI/Swagger** - API documentation
- **Maven** - Build tool