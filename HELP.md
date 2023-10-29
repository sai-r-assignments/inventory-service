# Inventory Service

Welcome to the Inventory-Service GitHub repository! This project is written in Java and utilizes the Spring framework. Below are the instructions to set up and run this project on your local machine.

## Prerequisites

Before you start, make sure you have the following prerequisites installed on your system:

- Java 17
- Maven 3

## Getting Started

1. Clone the repository to your local machine using the following command:

   ```shell
   git clone https://github.com/sai-r-assignments/inventory-service.git
2. Navigate to the project directory:

   ```shell
   cd inventory-service
3. Generate private key using the following commands:

   ```shell
   openssl genrsa -out pvtkey.pem 4096
4. Generate public key for the above private key using the following commands:

   ```shell
   openssl rsa -pubout -in pvtkey.pem -outform PEM -out pubkey.pem
5. Copy these two pem files (pvtkey.pem and pubkey.pem) and add them to the src/main/properties folder of the project. If you already have an existing KeyPair, you can update application.properties file to point to your keys.

## Running the Application

### Command Line
To run the application, you can use the following Maven command:

   ```shell
   mvn spring-boot:run
   ```

### Integrated Development Environment (IDE)

If you prefer to run the application from your IDE, follow these steps:

1. Open your IDE (e.g., IntelliJ IDEA or Eclipse).

2. Import the project into your IDE.

3. Locate the `InventoryServiceApplication` class in the project.

4. Run the `main` method in the `InventoryServiceApplication` class.

### Lombok Configuration

If you are using an IDE, you need to configure the Lombok plugin. Lombok is used in this project to reduce boilerplate code. Here's how to configure it:

1. Install the Lombok plugin for your IDE if you haven't already.

2. Enable annotation processing for Lombok in your IDE settings.

### Using the Application

To use the application, follow these steps:

#### User Registration

New users can register using the following POST endpoint:

- **Endpoint**: `/api/v1/auth/register`

**Request:**

POST /api/v1/auth/register
```json
{
  "name": "new_user",
  "email": "user@mail.com",
  "password": "password123",
  "isAdmin": true
}
```

**Response:**

Upon successful registration, a user account will be created.

#### User Signin

Users can sign in using the following endpoint, which will return a JWT token:

- **Endpoint**: `/api/v1/auth/signin`

**Request:**

POST /api/v1/auth/signin
```json
{
  "email": "user@mail.com",
  "isAdmin": true
}
```
**Response:**
```json
{
  "token": "your_jwt_token_here"
}
```
**NOTE**: You will need the JWT token to access the remaining endpoints.

### User Scopes

To perform specific operations, user scopes are required:

- **USER Scope**: Allows read operations.
- **ADMIN Scope**: Allows write operations.

Make sure to have the appropriate user scope when accessing the API endpoints.

#### API Specifications

The specifications for all available endpoints can be found in the [inventory-service-specs.yaml](https://github.com/sai-r-assignments/inventory-service/blob/main/inventory-service-specs.yaml) file.

#### Postman Collection

A Postman collection is available in the [InventoryService.postman_collection.json](https://github.com/sai-r-assignments/inventory-service/blob/main/InventoryService.postman_collection.json) file, which includes preconfigured requests for testing the API endpoints. You can import this collection into Postman to simplify testing and interaction with the application.
