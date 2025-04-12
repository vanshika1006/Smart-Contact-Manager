# Smart Contact Management System

The Smart Contact Management System is a web application that helps users manage their personal contacts. Built with **Spring Boot** and **Thymeleaf**, the application allows users to:

- **Add, Update, Delete, and View** Contacts
- **Email Verification** through OTP
- **Password Recovery** via Forgot Password Module
- **Role-based Authentication** for secure access

## Features:
- **User Registration & Authentication**: Secure login and signup with Spring Security.
- **OTP-based Email Verification**: Ensures valid email addresses during signup.
- **Forgot Password**: Users can reset their passwords through email verification.
- **Contact Management**: Manage contacts, including adding, editing, and deleting information.
- **Profile Management**: Users can manage their profile details.

## Technologies Used:
- **Backend**: Spring Boot, Spring Security, JPA, MySQL
- **Frontend**: Thymeleaf, Bootstrap
- **Email Service**: JavaMail API for OTP and password reset
- **Database**: MySQL

## Getting Started:

### Prerequisites:
- Java 8 or higher
- MySQL Database

### Steps to Run:

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/smart-contact-manager.git
   mvn spring-boot:run
2. Access the app at http://localhost:8085.

