# StandardQL Engine (MRQ - moteur de validation des règles de qualité)

## Project Structure

```
- engine/     # Spring Boot application with rule evaluation engine
- compiler/   # StandardQL compiler wrapper and AST implementation
- model/      # Shared domain models
```

## Modules Overview

### Engine Module

The `engine` module contains a Spring Boot application that serves as the rule evaluation engine. It
processes business rules and evaluates them against domain objects.

#### Starting the Engine Application

The easiest way to run the engine is using Spring Boot's built-in Docker Compose support:

```bash
cd engine
./gradlew bootRun
```

This will automatically:

- Start an ActiveMQ broker using Docker Compose (configured in `engine/compose.yaml`)
- Launch the Spring Boot application
- Connect to the ActiveMQ instance for JMS messaging

The application runs on the default Spring Boot port (8080) and is configured through
`engine/src/main/resources/application.properties`.

Rules are defined in the `src/main/resources/rules.yaml` file, rules can be added or removed as
needed.

#### Key Features

- Rule evaluation runtime
- JMS integration for asynchronous processing
- Function handlers for custom operations (COUNT, SUM, MIN, MAX, etc.)
- Relation resolvers for domain object relationships

### Compiler Module

The `compiler` module provides a Java wrapper around the `standardql-compiler` binary (written in
Haskell). It handles:

- Deserialization of StandardQL's internal language representation
- Construction of an Abstract Syntax Tree (AST)
- Visitor pattern implementation for AST traversal
- Expression and statement model classes

The compiler integrates with the engine to compile and execute rules defined in StandardQL format.

### Model Module

The `model` module contains standard domain model classes that are shared across all modules:

- Domain entities (Building, Dwelling, Entrance, Permit, Project, RealEstate, Work)
- Rule-related classes (Rule, RuleContext, RuleResult, RuleMetadata)
- Common enumerations and value objects

These models can be referenced by any module in the project, ensuring consistency across the
application.

## Building the Project

To build all modules:

```bash
./gradlew build
```

To build a specific module:

```bash
./gradlew :engine:build
./gradlew :compiler:build
./gradlew :model:build
```

## Requirements

- Java 21 or higher
- Docker (for ActiveMQ in development mode)
- Gradle (wrapper included)

## Development Setup

1. Clone the repository
2. Ensure Docker is running (required for ActiveMQ)
3. Run `./gradlew build` to compile all modules
4. Start the engine application as described above

The ActiveMQ broker will be automatically managed by Spring Boot's Docker Compose integration when
running in development mode.
