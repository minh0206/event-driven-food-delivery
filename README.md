# Event-Driven Food Delivery System

This project is a real-time food delivery application built with a modern, scalable architecture. It utilizes Spring
Boot for building microservices, Apache Kafka for event-driven communication, and Docker for containerizing the
infrastructure. The goal is to create a robust system where users can place orders, restaurants can process them, and
delivery can be tracked in real-time.

This README documents the project setup as of the completion of Phase 1.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
    - [1. Clone the Repository](#1-clone-the-repository)
    - [2. Start the Infrastructure](#2-start-the-infrastructure)
    - [3. Build the Project](#3-build-the-project)
    - [4. Run the Microservices](#4-run-the-microservices)
- [Service Directory](#service-directory)
- [Kafka Event Architecture](#kafka-event-architecture)

## Architecture Overview

The system is designed as a **multi-module microservices architecture**. All external client requests are routed through
a central **API Gateway**, which forwards them to the appropriate downstream service. The services are designed to be
independent and communicate with each other asynchronously using Apache Kafka events where appropriate.

#### Application Services

- **API Gateway**: The single entry point for all incoming API requests. It handles routing to the correct microservice.
- **User Service**: Manages user registration, authentication, and profiles.
- **Restaurant Service**: Handles restaurant profiles, menus, and operating hours.
- **Order Service**: Responsible for the core logic of creating and managing orders.
- **Delivery Service**: Manages delivery personnel and delivery logistics.
- **Notification Service**: Will be responsible for sending notifications (email, push, etc.).

#### Infrastructure (Managed by Docker Compose)

- **PostgreSQL**: The primary relational database for all microservices. A single database instance (`food_delivery_db`)
  is used for simplicity in local development.
- **Apache Kafka**: The event-streaming platform used for asynchronous communication between services.
- **Zookeeper**: Required by Kafka for cluster coordination.

## Prerequisites

Before you begin, ensure you have the following installed on your local machine:

- **Git**: For cloning the repository.
- **Java Development Kit (JDK)**: Version 25.
- **Apache Maven**: For building the project and managing dependencies.
- **Docker and Docker Compose**: For running the infrastructure services (database and message broker).

## Getting Started

Follow these steps to get the entire application running locally.

### 1. Clone the Repository

Open your terminal and clone the project to your local machine:

```bash
git clone <your-repository-url>
cd event-driven-food-delivery
```

### 2. Start the Infrastructure

The project includes a `docker-compose.yml` file to easily spin up all the necessary backing services (PostgreSQL,
Kafka, Zookeeper).

From the root directory of the project, run:

```bash
docker-compose up -d
```

- The `-d` flag runs the containers in detached mode.
- This command will pull the required images and start a container for each service.
- It also pre-configures Kafka to automatically create the necessary topics on startup.

To verify that the containers are running, use the command `docker ps`. You should see containers for `postgres`,
`kafka`, and `zookeeper`.

### 3. Build the Project

This is a multi-module Maven project. To build all the microservices at once, run the following command from the **root
directory**:

```bash
mvn clean install
```

This command will compile the code, run any tests, and package each service into a `.jar` file in its respective
`target` directory.

### 4. Run the Microservices

Each microservice is a standalone Spring Boot application. You need to run each one in a separate terminal window.

**Terminal 1: API Gateway**

```bash
cd api-gateway
mvn spring-boot:run
```

**Terminal 2: User Service**

```bash
cd user-service
mvn spring-boot:run
```

**Terminal 3: Restaurant Service**

```bash
cd restaurant-service
mvn spring-boot:run
```

**Terminal 4: Order Service**

```bash
cd order-service
mvn spring-boot:run
```

...and so on for the `delivery-service` and `notification-service`.

Once all services are running, the system is operational.

## Service Directory

All API requests should be made through the API Gateway. The gateway routes requests based on the URL path prefix.

| Service              | Local Port | Gateway Route Prefix  | Example URL through Gateway                   |
|----------------------|:----------:|:---------------------:|-----------------------------------------------|
| **API Gateway**      |   `8080`   |           -           | -                                             |
| User Service         |   `8081`   |     `/api/users/`     | `http://localhost:8080/api/users/{endpoint}`  |
| Restaurant Service   |   `8082`   |  `/api/restaurants/`  | `http://localhost:8080/api/restaurants/...`   |
| Order Service        |   `8083`   |    `/api/orders/`     | `http://localhost:8080/api/orders/...`        |
| Delivery Service     |   `8084`   |   `/api/delivery/`    | `http://localhost:8080/api/delivery/...`      |
| Notification Service |   `8085`   | `/api/notifications/` | `http://localhost:8080/api/notifications/...` |

## Kafka Event Architecture

This table defines the topics used for asynchronous communication between services.

| Topic Name                | Event Schema (Payload)                                               | Producer Service(s)  | Consumer Service(s)  |
|---------------------------|----------------------------------------------------------------------|----------------------|----------------------|
| `order_placed`            | `{ orderId, restaurantId, customerId, items[] }`                     | `order-service`      | `restaurant-service` |
| `order_status_updates`    | `{ orderId, newStatus }`                                             | `restaurant-service` | `order-service`      |
| `order_accepted`          | `{ orderId, restaurantId, restaurantLatitude, restaurantLongitude }` | `restaurant-service` | `delivery-service`   |
| `driver_assigned`         | `{ orderId, driverId, driverUserId }`                                | `delivery-service`   | `order-service`      |
| `driver_location_updates` | `{ orderId, driverId, latitude, longitude }`                         | `delivery-service`   | `order-service`      |
| `order_in_transit`        | `{ orderId, driverId, driverUserId }`                                | `delivery-service`   | `order-service`      |
| ... *(more to come)*      |                                                                      |                      |                      |