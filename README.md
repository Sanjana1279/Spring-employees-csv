# Spring-employees-csv
Spring Boot application with Employee CRUD APIs, CSV export using Spring Batch, and CSV download feature.  Includes Swagger UI for API documentation and H2 in-memory database for easy setup.

# 📊 Spring Boot Employees CSV Export

A simple Spring Boot application that demonstrates:

- CRUD operations for Employees (REST APIs)
- Export Employees to CSV using **Spring Batch**
- Download the generated CSV via REST API
- Integrated with Swagger/OpenAPI for easy testing
- H2 in-memory database for quick setup

---

## 🚀 Features
- **Employees CRUD APIs** (`GET`, `POST`, `PUT`, `DELETE`)
- **CSV Export** via Spring Batch
- **CSV Download** via REST
- **Swagger UI** at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2 Console** at: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---

## ⚙️ Tech Stack
- Java 17+
- Spring Boot
- Spring Data JPA
- Spring Batch
- H2 Database
- Swagger (springdoc-openapi)

---

## 📂 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/employees` | GET | List all employees |
| `/api/employees/{id}` | GET | Get employee by id |
| `/api/employees` | POST | Create employee |
| `/api/employees/{id}` | PUT | Update employee |
| `/api/employees/{id}` | DELETE | Delete employee |
| `/api/employees/export` | POST | Export employees to CSV |
| `/api/employees/download` | GET | Download exported CSV |

---

## ▶️ How to Run
```bash
# clone repository
git clone https://github.com/<your-username>/spring-employees-csv.git
cd spring-employees-csv

# build
mvn clean install

# run
mvn spring-boot:run

