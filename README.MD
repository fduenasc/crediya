# CrediYa – Personal Loan Management Platform 🚀

CrediYa is a platform designed to digitize and optimize the management of personal loan applications, eliminating manual and in-person processes. The system allows applicants to enter their data and desired loan information, automates the initial evaluation, and streamlines the administrative management of pre-approved cases.

## Key Features ✨

- **Loan Product Management**: Administrators can create, edit, and delete credit products.
- **Application Process**: Applicants submit their data and desired loan information.
- **Automated Evaluation**: The system determines the viability of the application and the applicant's borrowing capacity.
- **User and Role Management**: Distinct roles for applicants and administrators, each with specific permissions.
- **Automatic Notifications**: Applicants receive notifications about the status of their loan.
- **Performance Reports**: Administrators access business performance reports (e.g., total approved loans).
- **Efficient and Transparent Experience**: Fast and clear process for all users.

## Architecture & Methodology 🏗️

- **Independent Microservices**: Each service is an autonomous component with its own repository.
- **Hexagonal Architecture (Clean Architecture)**: Clear separation between domain, use cases, infrastructure, and application.
- **Developed with WebFlux**: Reactive, non-blocking APIs.
- **Asynchronous Communication**: Message queues (SQS) for service integration.
- **Databases**: RDS for transactional data and DynamoDB for reporting.
- **Deployment on AWS**: Manual deployment using ECS with Fargate and API Gateway.
- **Version Control & CI/CD**: Docker and AWS ECR.
- **Best Practices**: Proper variable naming, constant management, concise methods.
- **Code Validation**: SonarLint.
- **API Documentation**: Swagger/OpenAPI.
- **Unit Testing**: Ensured coverage and quality.
- **Gitflow**: Each user story (HU) is developed in its own branch.

## Main Technologies 🛠️

- Java 17+
- Spring WebFlux
- R2DBC (MySQL)
- DynamoDB (reporting)
- Gradle (multi-module)
- SLF4J for logging
- Swagger/OpenAPI for documentation
- Docker
- AWS (ECS, Fargate, API Gateway, SQS, RDS, ECR)

## Main Flow 🔄

1. The applicant submits a loan request.
2. The system performs an automated initial evaluation.
3. Viable requests are reviewed by an administrator.
4. The administrator makes the final decision.
5. The system notifies the applicant of their request status.
6. Administrators can consult performance reports.

## References & Resources 🔗

- [Clean Architecture – Isolating the Details](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)
- [Scaffold Clean Architecture Documentation](https://bancolombia.github.io/scaffold-clean-architecture/docs/intro)