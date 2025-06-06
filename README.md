# Tennis Club Reservation System

This is a backend application for managing court reservations in a tennis club.

## Project Description

The application allows users to:
- Manage tennis courts and their surfaces.
- Create and view reservations for single or double games.
- Track users by their phone number.
- Calculate rental prices dynamically based on surface type and match type.
- Soft-delete all entities to maintain data integrity.

All exposed endpoints follow REST conventions and are prefixed with `/api`.

## Features

- CRUD operations for courts.
- RUD operations for reservations.
- Surface type management (with dynamic pricing per minute).
- Reservations searchable by court or phone number, with filtering for future reservations.
- Automatic user creation based on phone number.
- Overlap prevention in reservation time slots.
- Price calculation.

## Tech Stack

- Java 17
- Spring Boot 3.5.0
- JPA (Hibernate, without Spring Data JPA)
- H2 in-memory database
- MapStruct for mapping
- Lombok for reducing boilerplate
- Log4j for logging
- Springdoc OpenAPI for API documentation
- Spotless plugin for code formatting
- JaCoCo for test coverage reporting

## Getting Started

### Prerequisites

- Java 17+
- Maven

### Run the application

```bash
    mvn spring-boot:run
