GST Billing Application — Spring Boot Backend
A production-ready REST API for generating GST invoices, managing products and businesses, and automatically calculating CGST, SGST, and IGST.

Tech Stack
Layer	Technology
Language	Java 17
Framework	Spring Boot 3.2
ORM	Spring Data JPA + Hibernate
Database	MySQL 8+
Build Tool	Maven
Utilities	Lombok
API Testing	Postman
Project Structure
com.company.gstbilling
├── controller          # REST controllers (Business, Product, Invoice)
├── service
│   └── impl            # Service implementations with GST logic
├── repository          # Spring Data JPA repositories
├── entity              # JPA entities (Business, Product, Invoice, InvoiceItem)
├── dto
│   ├── request         # Request DTOs (BusinessRequest, ProductRequest, etc.)
│   └── response        # Response DTOs (ApiResponse, InvoiceResponse, etc.)
├── exception           # Custom exceptions + GlobalExceptionHandler
├── util                # GSTCalculator, InvoiceNumberGenerator
└── config              # CorsConfig
Database Setup
Make sure MySQL is running.

Run the schema script:

-- file: src/main/resources/schema.sql
source schema.sql;
Or Spring Boot will auto-create tables via spring.jpa.hibernate.ddl-auto=update.

Update credentials in application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/gst_billing_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
Running the Application
# Clone / navigate to project root
cd gst-billing

# Build
mvn clean install

# Run
mvn spring-boot:run
Server starts at: http://localhost:8080

API Reference
Business APIs
Method	Endpoint	Description
POST	/api/business	Register a business
GET	/api/business/{id}	Get business by ID
GET	/api/business?page=0&size=10	All businesses (paginated)
POST /api/business — Request Body:

{
  "businessName": "TechMart Pvt Ltd",
  "gstNumber": "27AABCT1332L1ZV",
  "address": "Andheri, Mumbai",
  "state": "Maharashtra",
  "phone": "9876543210"
}
Product APIs
Method	Endpoint	Description
POST	/api/products	Add a product
GET	/api/products?page=0&size=10	All products (paginated)
GET	/api/products/{id}	Get product by ID
PUT	/api/products/{id}	Update product
DELETE	/api/products/{id}	Delete product
GST Rates Allowed: 0, 5, 12, 18, 28

Invoice APIs
Method	Endpoint	Description
POST	/api/invoices	Create invoice (auto-calculates GST)
GET	/api/invoices/{id}	Get invoice with line items
GET	/api/invoices?page=0&size=10	All invoices (paginated)
GET	/api/invoices/business/{businessId}	Invoices for a business (paginated)
POST /api/invoices — Request Body:

{
  "businessId": 1,
  "customerName": "Rohan Sharma",
  "customerState": "Maharashtra",
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 2, "quantity": 3 }
  ]
}
GST Calculation Logic
Base Amount  = price × quantity
GST Amount   = (price × quantity × gstPercentage) / 100
Grand Total  = Base Amount + GST Amount
Scenario	Tax Applied
Business state == Customer state	CGST 50% + SGST 50%
Business state != Customer state	IGST 100%
Example — Intra-State (Maharashtra → Maharashtra):

Laptop ₹45,000 × 1, GST 18% = ₹8,100 → CGST ₹4,050 + SGST ₹4,050
Example — Inter-State (Maharashtra → Gujarat):

Laptop ₹45,000 × 1, GST 18% = ₹8,100 → IGST ₹8,100
Standard Response Format
All APIs return a consistent JSON structure:

{
  "status": "SUCCESS",
  "message": "Invoice created successfully",
  "data": { }
}
Error Response:

{
  "status": "ERROR",
  "message": "Business not found with id: '99'",
  "data": null
}
HTTP Status Codes
Scenario	Status Code
Successful creation	201 Created
Successful read/update	200 OK
Resource not found	404 Not Found
Validation / bad input	400 Bad Request
Unexpected server error	500 Internal Error
Pagination Query Parameters
All GET list endpoints support:

Param	Default	Description
page	0	Page number (0-indexed)
size	10	Items per page
sortBy	id	Field to sort by
sortDir	asc	Sort direction: asc / desc
Postman Collection
Import GST_Billing_API.postman_collection.json into Postman.

Set the baseUrl variable to http://localhost:8080.

Running Tests
mvn test
Unit tests cover all GST calculation scenarios in GSTCalculatorTest.java.
