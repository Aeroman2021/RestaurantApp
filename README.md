
🧠 Domain Analysis & System Flow

This project is a Restaurant Order Management System designed to model a real-world ordering workflow with proper authentication, authorization, and role separation.

📦 Core Entities

•	The main domain entities of the system are:
•	User
•	Category
•	MenuItem
•	Order
•	OrderItem

Each entity is designed to represent a real concept in a restaurant ordering system and is mapped to the database using JPA.

🔄 Application Workflow
1. User Registration & Authentication
A user first registers in the system.
After registration, the user logs in using their username and password.
Upon successful login, the system issues a JWT (JSON Web Token).
This token is required to access protected endpoints such as order creation.

2. Menu Browsing & Item Selection
Menu items are organized into categories.
Each MenuItem belongs to exactly one Category.
Users can browse the menu and select one or more items, specifying the quantity for each item.

3. Order Creation
To create an order, the user must provide a valid JWT token.
An order consists of:
A reference to the user
A list of OrderItem objects
Each OrderItem references:
A MenuItem
A quantity
When the order is first created, its status is set to CREATED.

4. Order Management by Admin
An admin user can log into the system and:
View all orders
Update the status of any order
Order status updates reflect the real-life order lifecycle (e.g. CREATED, CONFIRMED, PREPARED, DELIVERED).

These operations are protected using role-based authorization.

🔐 Security & Access Control

The application uses Spring Security with JWT-based authentication.
Access is controlled using roles:
ROLE_CUSTOMER → allowed to create orders
ROLE_ADMIN → allowed to manage and update orders
Method-level security is enforced using annotations such as @PreAuthorize.

🎯 Design Goals

•	The main objectives of this project are:
•	Clear separation of responsibilities between users and administrators
•	Clean and understandable domain modeling
•	Implementation of a realistic restaurant ordering workflow
•	Stateless and secure authentication using JWT
•	Enterprise-style backend architecture using Spring Boot
