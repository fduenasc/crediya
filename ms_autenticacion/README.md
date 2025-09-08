# Project Overview 🚀

This project implements a user management microservice for the CrediYa lending system, following Clean Architecture principles in Java with Gradle. It exposes a reactive API for user registration, validates user data, and persists users in a MySQL database using R2DBC.

## Main Features ✨

- 📝 **User Registration**: Clients can register users via a REST API.
- ✅ **Validation**: User data is validated for required fields and business rules \(e\.g\., email format, salary range, uniqueness\).
- 💾 **Persistence**: Valid users are stored in a MySQL database using R2DBC.
- ⚡ **Reactive Programming**: The API leverages Spring WebFlux for non\-blocking, asynchronous request handling.
- 🔒 **Role Management**: Supports role assignment and management for users.

## Architecture 🏗️

The project is organized in modules following Clean Architecture:

- 🧩 **Domain**: Contains core business model \(`Usuario`, `UsuarioDto`\) and business rules \(validation, role logic\).
- 🛠️ **Usecases**: Implements application logic, such as registering a user \(`RegistrarUsuarioUseCase`\).
- 🗄️ **Infrastructure**: Adapters for persistence \(R2DBC for MySQL\) and entry points \(WebFlux handlers and routers\).
- 🚦 **Application**: Assembles modules, configures beans, and starts the application.

## Main Flow 🔄

1. 📥 **API Request**: A client sends a POST request to `/api/v1/users` with user data.
2. 🤖 **Handler**: The request is processed by a reactive handler \(`Handler`\), which validates input and calls the use case.
3. 🧑‍💼 **Use Case**: The use case \(`RegistrarUsuarioUseCase`\) checks for existing users and orchestrates persistence.
4. 🗃️ **Persistence**: The repository adapter maps domain models to database entities and saves them using R2DBC.
5. 📤 **Response**: The API returns a response indicating success or validation errors.

## Technologies 🛠️

- ☕ Java 17\+
- 🌐 Spring WebFlux
- 🗄️ R2DBC \(MySQL\)
- 🏗️ Gradle \(multi\-module\)
- 📋 SLF4J for logging
- 📖 OpenAPI/Swagger for API documentation

## References 🔗

- [Clean Architecture – Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)
- [Scaffold Clean Architecture Documentation](https://bancolombia.github.io/scaffold-clean-architecture/docs/intro)