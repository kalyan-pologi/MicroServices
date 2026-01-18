User–Hotel–Rating Microservices Platform

This project models a simplified hotel review platform, where users can rate hotels and retrieve enriched user profiles with associated ratings and hotel details.

**Core Components**

API Gateway – Single entry point for all client requests
Service Discovery (Eureka) – Dynamic service registration and lookup
Config Server – Centralized configuration management
User Service – Aggregates user profile, ratings, and hotel details
Rating Service – Manages user-to-hotel ratings
Hotel Service – Manages hotel information
Security – JWT-based authentication and authorization
Resilience – Circuit breakers, retries, rate limiting

**Tech Stack**

Java 17
Spring Boot
Spring Cloud
Gateway
Eureka
Config Server
Spring Security (JWT)
Resilience4j
Feign Client / RestTemplate
MySQL / PostgreSQL / H2
Maven
Docker

**Running the Application**

# Config Server
cd config-server
mvn spring-boot:run

# Eureka Server
cd service-registry
mvn spring-boot:run

cd api-gateway
mvn spring-boot:run

cd user-service
mvn spring-boot:run

cd hotel-service
mvn spring-boot:run

cd rating-service
mvn spring-boot:run


















