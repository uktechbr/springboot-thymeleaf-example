# Spring Boot Thymeleaf Example (UK Tech CMS)

A modern, reactive Content Management System built with **Spring Boot 4.1.1**, **Spring WebFlux**, **Thymeleaf**, and enterprise-grade infrastructure.

This project follows a decoupled architecture split into two main applications:
1. **`cms-backend`**: A reactive REST API responsible for business logic, data persistence, and caching.
2. **`cms-frontend`**: A reactive server-side rendered web application that communicates with the backend via WebClient and serves user-friendly HTML pages using Thymeleaf and Bootstrap.

---

## Key Features

* **Cutting-Edge Stack:** Powered by Java 25 and Spring Boot 4.1.1.
* **Reactive Architecture:** Built on Project Reactor and Spring WebFlux for high performance and non-blocking operations.
* **High Performance Caching (Redis):** Integrates Redis (Alpine) to cache frequently accessed data, drastically reducing database load and improving response times.
* **Database Versioning (Liquibase):** Manages database schema evolution safely and automatically.
* **Global Error Handling:** Gracefully intercepts backend connection failures and displays a user-friendly HTML error page instead of technical stack traces.
* **Containerized Environment:** Fully supported by Docker Compose for seamless local deployment.

---

## Technology Stack

* **Language:** Java 25
* **Framework:** Spring Boot 4.1.1 (Spring WebFlux, Thymeleaf)
* **Database:** PostgreSQL 18
* **Database Migrations:** Liquibase
* **Cache:** Redis (Alpine)
* **Frontend UI:** Bootstrap 5

---

## Running with Docker Compose (Recommended)

The easiest way to run the entire infrastructure and applications without manual setup is using Docker Compose.

1. Ensure you have Docker and Docker Compose installed.
2. Open your terminal at the root of the project where the `docker-compose.yml` file is located.
3. Run the following command to start all containers in the background:
   ```bash
   docker compose up -d
   ```
4. This will automatically spin up PostgreSQL 18, Redis Alpine, and the services.
5. To stop the environment, run:
   ```bash
   docker compose down
   ```

---

## Running Manually (For Dummies / Local Development)

If you prefer running the applications manually via your IDE or terminal:

### Step 1: Start Infrastructure
Make sure PostgreSQL and Redis are running locally (or via Docker).

### Step 2: Start the Backend (`cms-backend`)
1. Open your terminal and navigate to the backend folder:
   ```bash
   cd cms-backend
   ```
2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
3. Wait until the backend starts (usually on port **8081**).

### Step 3: Start the Frontend (`cms-frontend`)
1. Open a new terminal window and navigate to the frontend folder:
   ```bash
   cd cms-frontend
   ```
2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
3. Wait for the frontend to start (running on port **8080**).

### Step 4: Access the Application
* Open your web browser and go to:
  **`http://localhost:8080`**

---

## Testing the Global Error Handler

1. Make sure both backend and frontend are running, and the homepage loads fine.
2. Go to the terminal where `cms-backend` is running and stop it by pressing `Ctrl + C`.
3. Refresh your browser at `http://localhost:8080`.
4. Instead of a broken page, you will see a friendly **"System Error / Service Unavailable"** page rendered by Thymeleaf.
5. Restart the backend and refresh the browser; the site comes back to life instantly.

---

## License

This project is licensed under the terms of the **MIT License**.