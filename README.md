# Courier Management System

## Overview

Courier Management System is a Spring Boot-based REST API for managing courier operations including order creation, parcel tracking, delivery agent assignments, and user management. The system supports multiple user roles (Admin, Manager, Agent, Customer) with JWT-based authentication and role-based access control.

## Features

* User registration and authentication with JWT
* Order creation and tracking
* Parcel management
* Delivery agent assignment
* Bulk parcel assignment
* Automated order status tracking via scheduler
* Role-based access control

## Tech Stack

* Java 21
* Spring Boot 3.2.5
* Spring Security
* Spring Data JPA
* MySQL
* JWT (io.jsonwebtoken)
* Lombok

## System Design / How It Works

1. User registers via `/api/auth/register` with a role (ADMIN, MANAGER, AGENT, CUSTOMER)
2. User logs in to receive JWT token
3. Customer creates order with pickup and drop locations
4. Manager views parcels and assigns delivery agents
5. Agent updates parcel status (IN_TRANSIT, DELIVERED)
6. Scheduler runs every 10 seconds to auto-update order status
7. Customer tracks order status

## Project Structure

```text
com.example.couriermanagementsystem
├── controller
│   ├── AdminController
│   ├── AgentController
│   ├── AssignmentController
│   ├── AuthController
│   ├── OrderController
│   └── ParcelController
├── service
│   ├── AdminService
│   ├── AgentService
│   ├── AssignmentService
│   ├── AuthService
│   ├── CustomUserDetailsService
│   ├── DeliveryScheduler
│   ├── OrderService
│   └── ParcelService
├── repository
├── entity
│   ├── DeliveryAssignment
│   ├── Location
│   ├── Order
│   ├── Parcel
│   └── User
├── dto
│   ├── request
│   └── response
├── config
│   ├── JwtFilter
│   ├── JwtUtil
│   └── SecurityConfig
├── enums
│   ├── AssignmentStatus
│   ├── OrderStatus
│   ├── ParcelStatus
│   └── UserRole
└── CourierManagementApplication
```

## Setup & Installation

1. Install Java 21 and MySQL
2. Create database `courier_management_system`
3. Configure `application.properties` with database credentials
4. Run `mvn spring-boot:run`

## API Endpoints

Base path: `/api`

### 1) Register User

* **POST** `/api/auth/register`
* **Request Body**: `RegisterRequest` (name, email, password)

### 2) Login

* **POST** `/api/auth/login`
* **Request Body**: `LoginRequest` (email, password)

### 3) Create Order

* **POST** `/api/orders`
* **Request Body**: `CreateOrderRequest` (pickupAddress, pickupCity, pickupPincode, dropAddress, dropCity, dropPincode, parcels)
* **Auth**: CUSTOMER role required

### 4) Track Order

* **GET** `/api/orders/{orderId}`
* **Auth**: CUSTOMER role required

### 5) Get All Parcels

* **GET** `/api/parcels`
* **Auth**: MANAGER role required

### 6) Get Agent Parcels

* **GET** `/api/agent/parcels`
* **Auth**: AGENT role required

### 7) Update Parcel Status

* **PUT** `/api/agent/status`
* **Request Body**: `UpdateParcelStatusRequest` (parcelId, status)
* **Auth**: AGENT role required

### 8) Assign Parcel

* **POST** `/api/assignments`
* **Request Body**: `AssignParcelRequest` (parcelId, agentId)
* **Auth**: MANAGER role required

### 9) Bulk Assign

* **POST** `/api/assignments/bulk`
* **Request Body**: List of `AssignParcelRequest`
* **Auth**: MANAGER role required

### 10) Create User (Admin)

* **POST** `/api/admin/users`
* **Request Body**: `CreateUserRequest` (name, email, password, role)
* **Auth**: ADMIN role required

### 11) Get All Users

* **GET** `/api/admin/users`
* **Auth**: ADMIN role required

## Database Schema

### `users`

* `id` (PK)
* `name`
* `email`
* `password`
* `role`

### `orders`

* `id` (PK)
* `customer_id` (FK -> users)
* `status`
* `pickup_location_id` (FK -> locations)
* `drop_location_id` (FK -> locations)
* `createdAt`
* `updatedAt`

### `parcels`

* `id` (PK)
* `parcelName`
* `weight`
* `status`
* `order_id` (FK -> orders)
* `createdAt`

### `locations`

* `id` (PK)
* `address`
* `city`
* `pincode`

### `delivery_assignments`

* `id` (PK)
* `agent_id` (FK -> users)
* `parcel_id` (FK -> parcels)
* `status`
* `assignedAt`

## Configuration Notes

* `spring.application.name=couriermanagementsystem`
* `spring.datasource.url=jdbc:mysql://localhost:3306/courier_management_system`
* `spring.datasource.username=root`
* `spring.datasource.password=kunal5410`
* `spring.jpa.hibernate.ddl-auto=update`
* `jwt.secret=12345YourSecretKeyForJWTTokenCreation45321`

## Future Improvements

* Add email/SMS notifications for order status updates
* Implement real-time tracking with WebSocket
* Add payment integration
* Implement delivery proof with image capture
* Add analytics dashboard
