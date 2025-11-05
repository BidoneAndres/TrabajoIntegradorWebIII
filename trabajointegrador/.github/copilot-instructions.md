# AI Agent Instructions for TrabajoIntegradorWebIII

This document provides essential context for AI agents working with this Spring Boot-based logistics management system.

## Project Overview

Backend system for managing truck loading operations with the following key components:

- Order management (`Orden.java`) - Core entity tracking loading states
- Equipment tracking (Trucks, Tanks) - `Camion.java`, `Sisterna.java`
- Personnel management (`Chofer.java`) - Driver information
- Load monitoring (`DatoCarga.java`) - Loading process data
- Client management (`Cliente.java`) - Customer information

## Architecture Patterns

### Data Layer
- JPA entities in `model/` with standard Spring Data repositories in `persistence/`
- MySQL database (configured via Docker)
- Key tables: `ordenes`, `camiones`, `sisternas`, `choferes`, `clientes`

### Business Layer
- Interface-based business services in `model/business/interfaces/`
- Implementations in `model/business/implementations/`
- Domain-specific exceptions in `model/business/exceptions/`

### API Layer
- REST controllers in `controllers/` extending `BaseRestController`
- Standard response format using `StandartResponse` util class
- Consistent error handling via business exceptions

## Development Workflow

### Environment Setup
1. Start MySQL container:
```bash
cd Documentacion
./script-docker-init-mysql.sh
```

2. Application properties (`application.properties`):
- Database: `localhost:33306/integrador`
- Credentials: integrador/integrador
- Auto DDL update enabled

### Key Conventions

1. Error Handling:
- Business logic throws domain-specific exceptions (`BusinessException`, `NotFoundException`, etc.)
- Controllers use `BaseRestController` methods to standardize error responses

2. Data Validation:
- Entity constraints via JPA annotations
- Business-level validation in service implementations

3. State Management:
- Orders (`Orden`) follow strict state transitions:
  - RECIBIDA → REGISTRADA_PESAJE_INICIAL → CERRADA → REGISTRADA_PESAJE_FINAL
  - CANCELADA (from any state)

## Integration Points

1. Database:
- MySQL 8 with configurable connection via environment properties
- JPA/Hibernate for ORM with custom dialect

2. REST APIs:
- Base path: `/api/v1/`
- Standard response format for success/error
- JSON payload structures defined by model classes

## Common Development Tasks

1. Adding New Entity:
- Create model class in `model/`
- Add repository interface in `persistence/`
- Create business interface and implementation
- Implement REST controller extending `BaseRestController`

2. Business Logic Changes:
- Implement in relevant service class in `business/implementations/`
- Add appropriate exception handling
- Update unit tests in `test/` directory

3. Database Schema Changes:
- Update entity classes with new fields/relationships
- Rely on Hibernate's auto DDL update for development

## Troubleshooting
- Check Docker container for database connectivity issues
- Enable debug logging in `application.properties` for JPA/Hibernate investigation
- Review business exception hierarchy for proper error handling