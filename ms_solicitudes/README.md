# Project Overview 🚀

This project implements a credit request management microservice for the CrediYa lending system, following Clean Architecture principles in Java with Gradle. It exposes a reactive API for submitting credit requests, validates request data and client eligibility, and persists approved requests in a MySQL database using R2DBC.

## Main Features ✨

- 📝 **Credit Request Submission**: Clients can submit credit requests via a REST API endpoint (`/api/v1/solicitud`).
- ✅ **Validation**: Request data is validated for required fields and business rules, including client eligibility through external services.
- 💾 **Persistence**: Approved requests are stored in a MySQL database using R2DBC.
- ⚡ **Reactive Programming**: The API leverages Spring WebFlux for non-blocking, asynchronous request handling.
- 📄 **OpenAPI Documentation**: API endpoints are documented using OpenAPI annotations and SpringDoc.

## Architecture 🏗️

The project is organized in modules following Clean Architecture:

- 🧩 **Domain**: Contains core business models (`Solicitud`) and business rules (validation).
- 🛠️ **Usecases**: Implements application logic, such as registering a credit request (`RegistrarSolicitudUseCase`).
- 🗄️ **Infrastructure**: Adapters for persistence (R2DBC for MySQL) and entry points (WebFlux handlers and routers).
- 🚦 **Application**: Assembles modules, configures beans, and starts the application.

## Main Flow 🔄

1. 📥 **API Request**: A client sends a POST request to `/api/v1/solicitud` with credit request data.
2. 🤖 **Handler**: The request is processed by a reactive handler (`Handler`), which validates input and calls the use case.
3. 🧑‍💼 **Use Case**: The use case (`RegistrarSolicitudUseCase`) validates the client and orchestrates persistence.
4. 🗃️ **Persistence**: The repository adapter maps domain models to database entities and saves them using R2DBC.
5. 📤 **Response**: The API returns a response indicating success, validation errors, or authorization issues.

## Technologies 🛠️

- ☕ Java 17+
- 🌐 Spring WebFlux
- 🗄️ R2DBC (MySQL)
- 🏗️ Gradle (multi-module)
- 📋 SLF4J for logging
- 📄 OpenAPI/Swagger for API documentation

## References 🔗

- [Clean Architecture – Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)
- [Scaffold Clean Architecture Documentation](https://bancolombia.github.io/scaffold-clean-architecture/docs/intro)