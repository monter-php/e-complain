# E-Complain Service

E-Complain is a RESTful microservice for managing product complaints. It allows users to submit, edit, and retrieve complaints. The service automatically detects the complainant's country based on their IP address and handles duplicate complaints by incrementing a counter.

## Features

- **Add New Complaints:** Submit new product complaints.
- **Edit Complaint Content:** Modify the text of existing complaints.
- **Retrieve Complaints:** Fetch previously saved complaints.
- **Country Detection:** Automatically identifies the country of the complainant using their IP address.
- **Duplicate Handling:** If a complaint for the same product by the same reporter already exists, the service increments a counter for that complaint instead of creating a new one.

## Complaint Data

A complaint includes the following information:

- Product Identifier
- Complaint Message Content
- Creation Date
- Reporter Details (First Name, Last Name, Email)
- Country of Origin (derived from IP)
- Submission Counter (tracks duplicate reports)

## Tech Stack

- Java 21
- Spring Boot 3
- Maven
- PostgreSQL (recommended for production)
- H2 Database (for development/testing)

## Prerequisites

- Java JDK 21 or later
- Apache Maven 3.6.3 or later
- PostgreSQL server (if not using H2)

## Installation and Running

1.  **Clone the repository (if applicable) or ensure you are in the project's root directory.**

2.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    You can run the application using the Spring Boot Maven plugin:
    ```bash
    mvn spring-boot:run
    ```
    Alternatively, you can run the packaged JAR file (after building):
    ```bash
    java -jar target/ecomplain-0.0.1-SNAPSHOT.jar
    ```
    *(Note: The JAR filename might vary based on the project version in `pom.xml`)*

4.  **Accessing the API:**
    Once the application is running, the API will typically be available at `http://localhost:8080`.
    The OpenAPI documentation can be accessed at `http://localhost:8080/swagger-ui.html` or `http://localhost:8080/v3/api-docs`.

## Database Configuration

This application is configured by default to use an H2 in-memory database for ease of development and testing. For production or persistent storage, PostgreSQL is recommended.

### PostgreSQL Configuration

To use PostgreSQL, you need to update the `src/main/resources/application-local.yml` file with your PostgreSQL connection details.

1.  **Ensure you have a PostgreSQL server running and a database created for this application.**
    For local development, you can use Docker to run a local PostgreSQL instance.

2.  **Running PostgreSQL with Docker (Optional for local development):**
    If you prefer to run PostgreSQL using Docker for local development, you can use a command like the following:

    ```bash
    docker run --name ecomplain-postgres -e POSTGRES_USER=your_user -e POSTGRES_PASSWORD=your_password -e POSTGRES_DB=ecomplain_db -p 5432:5432 -d postgres
    ```
    Remember to adjust the `POSTGRES_USER`, `POSTGRES_PASSWORD`, and `POSTGRES_DB` environment variables and update your `application-local.yml` accordingly.

3. ** Creating the Database:**
    Once the PostgreSQL server is running, you can create the database using the following command:

    ```bash
    ./scripts/create-database.sh
    ```

## API Endpoints

Refer to the OpenAPI documentation for a detailed list of available endpoints and their usage.