
# SecondHand Marketplace

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-success?style=for-the-badge&logo=springboot)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge)
![JWT](https://img.shields.io/badge/JWT-Authentication-black?style=for-the-badge&logo=jsonwebtokens)
![SQLite](https://img.shields.io/badge/SQLite-Supported-336791?style=for-the-badge&logo=sqlite)
![GitHub last commit](https://img.shields.io/github/last-commit/AliNazari362/secondhand-java-project?style=for-the-badge)
![GitHub repo size](https://img.shields.io/github/repo-size/AliNazari362/secondhand-java-project?style=for-the-badge)
![GitHub top language](https://img.shields.io/github/languages/top/AliNazari362/secondhand-java-project?style=for-the-badge)

A desktop client-server marketplace for buying and selling second-hand goods. Developed for the **Advanced Programming** course at **Amirkabir University of Technology**.

## Team
- Ali Nazari
- Mohammadreza Kheradmand

## Table of Contents
1. Overview
2. Features
3. Technologies
4. Project Structure
5. Prerequisites
6. Installation
7. Backend Setup
8. Frontend Setup
9. Storage
10. Test Accounts
11. Screenshots
12. Architecture
13. Building
14. Responsibilities

## Overview
Users can register, login, publish advertisements, edit or delete them, search products, save favourites, rate sellers, chat, and manage the system through an administrator panel.

## Features
### Authentication
- Register
- Login
- JWT Authentication
- Password hashing
- Prevent unauthorized access to advertisements 

### Advertisement
- Create/Edit/Delete Advertisement
- Changing Advertisement status by seller
- Approval Workflow
- Status Management

### Chat
- Private conversations for each advertisement
- Seen messages

### Favorites
- Add/Remove favorites

### Rating
- Rating seller
- See the average rating of each seller

### Admin
- Ban/Unban users
- Accept/Reject advertisements
- Category management
- Dashboard & statistics

## Technologies
### Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven
- JWT

### Frontend
- JavaFX
- FXML
- CSS

### Database

- SQLite
- Spring Data JPA (Hibernate)

## Project Structure
```text
backend/
 ├── controller
 ├── service
 ├── repository
 ├── entity
 ├── dto
 ├── exception
 ├── JUnit tests
 └── MainApplication

frontend/
 ├── app
 ├── controller
 ├── component
 ├── model
 ├── service
 ├── config
 └── resources/
      ├── css
      ├── fxml
      ├── font
      └── image
```

## Prerequisites
- JDK 21+
- Maven 3.9+
- SQLite

## Installation
```text
git clone https://github.com/AliNazari362/secondhand-java-project.git

cd secondhand-java-project
```

## Backend Setup

### 1. Configure the Database

Create a PostgreSQL database (or use SQLite if configured) and update the database connection settings in:

```text
backend/src/main/resources/application.properties
```

Make sure the following properties match your database configuration:

```properties
spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...
```
> **Note:** The required database tables are created automatically by Hibernate when the application starts for the first time.


---

### 2. Build the Project

Open a terminal inside the `backend` directory and build the project:

```bash
cd backend
mvn clean install
```

---

### 3. Run the Backend

Start the Spring Boot server using Maven:

```bash
mvn spring-boot:run
```

Alternatively, if you are using **IntelliJ IDEA**, you can run the backend from the **Maven** tool window by executing:

1. `clean`
2. `spring-boot:run`

This is the workflow used during the development of this project.

---

### 4. Verify the Server

If everything is configured correctly, the backend will start successfully and listen on:

```text
http://localhost:8080
```
## Frontend Setup

### 1. Make Sure the Backend is Running

Before starting the frontend application, ensure that the backend server is already running.

---

### 2. Run the Frontend

From the `frontend` directory, execute:

```bash
mvn javafx:clean
mvn javafx:run
```

Alternatively, in **IntelliJ IDEA**, open the **Maven** tool window and execute the following goals in order:

1. `javafx:clean`
2. `javafx:run`

This is the workflow used during the development of this project.

---

### 3. Verify the Application

If everything is configured correctly, the login window will appear and the application will be ready to use.
## Storage

The application uses **SQLite** as its primary database during development.

Application data—including user accounts, advertisements, categories, chat messages, favorites, and seller ratings—is persisted using **Spring Data JPA (Hibernate)**, which provides object-relational mapping (ORM) and database persistence.

The database file is created automatically when the application is started for the first time.

## Test Accounts

To simplify the evaluation process, the following test accounts are included with the project:

| Role | Email | Password |
|------|-------|----------|
| **Administrator** | `admin@gmail.com` | `admin1234` |
| **User** | `ali@gmail.com` | `ali12345` |
| **User** | `hamid@gmail.com` | `hamid1234` |

The administrator account can be used to test administrative features such as user management, advertisement approval, and category management.

If these accounts have been removed or modified, new user accounts can be created through the **Register** page. Administrator privileges can be granted by updating the user's role in the database.

## Screenshots

### Login

The login page allows registered users to authenticate using their email and password.

![Login](docs/images/login.png)

---

### Register

New users can create an account using the registration page.

![Register](docs/images/register.png)

---

### Home

The home page displays all available advertisements and provides search and filtering options.

![Home](docs/images/home.png)

---

### Advertisement Details

Users can view complete information about an advertisement and contact the seller.

![Advertisement Details](docs/images/advertisement-details.png)

---

### Create Advertisement

Users can publish new advertisements by filling out the required information.

![Create Advertisement](docs/images/create-advertisement.png)

---

### Chat

The chat system enables direct communication between buyers and sellers.

![Chat](docs/images/chat.png)

---

### Favorites

Users can save advertisements for quick access.

![Favorites](docs/images/favorites.png)

---

### Profile

Users can update their profile information and change their password.

![Profile](docs/images/profile.png)

---

### Admin Dashboard

Administrators can manage users, advertisements, and categories.

![Admin Dashboard](docs/images/admin-dashboard.png)

---

### Category Management

Administrators can create, edit, and delete advertisement categories.

![Category Management](docs/images/category-management.png)

## Backend Architecture

The project follows a **Layered Architecture** pattern:

```text
Controller (REST API)
   ↓
Service (Business Logic)
   ↓
Repository (Data Access)
   ↓
Database (PostgreSQL / SQLite)
```

## Testing
```bash
cd backend
mvn test
```

## Future Improvements
- Docker
- CI/CD
- Email verification
- Push notifications
- Advanced filtering
- Dark mode

## Responsibilities

### Ali Nazari
Backend: Entity, DTO, Controller, MainApplication
Frontend: Service, Exception, Utils, FXML, CSS

### Mohammadreza Kheradmand
Backend: Repository, Service, Exception, Debugging
Frontend: App, Component, Controller, Model

## License
This project was developed for educational purposes as part of the Advanced Programming course.

---
Amirkabir University of Technology — Spring 1405

