# Cloud Project - School Assignment

This repository contains code for a Cloud project, developed as part of a school assignment.

## Overview

The project involves developing a REST API for managing user folders and files. It includes basic functionalities like
creating folders, uploading and downloading files, managing user access, and more.

## Structure

The codebase is structured as follows:

- **Controllers:** Contains controller classes responsible for handling HTTP requests and implementing API endpoints.
- **Services:** Includes service classes responsible for business logic, data processing, and interaction with
  repositories.
- **Entities:** Defines entity classes representing users, folders, files, and other domain-specific objects.
- **Repositories:** Contains repository interfaces/classes responsible for data access and manipulation.
- **Utilities:** Includes utility classes used for common functionalities and operations across the application.
- **DTOs (Data Transfer Objects):** Contains classes defining data structures for transferring data between layers.
- **Mappers:** Holds mapper classes responsible for converting between different object representations (e.g., between
  DTOs and entities).
- **Configurations:** Holds configuration files defining application settings and properties.
- **Filters:** Contains filter classes responsible for intercepting and processing requests before they reach
  controllers.
- **Annotations:** Includes custom annotations used for specifying metadata, validation, or custom behavior.
- **Validators:** Houses validator classes used for validating data and enforcing specific constraints.
- **Exceptions:** Contains custom exception classes representing different types of application-specific exceptions.
- **Exception Handlers:** Includes classes responsible for handling and processing exceptions thrown during application
  execution.

## Technologies Used

- Java
- Spring Boot
- JWT for authentication
- AES Encryption for file security

## How to Use

To use this project:

1. Clone the repository.
2. Set up the necessary environment variables and configurations (if any).
3. Build and run the application using Maven or your preferred IDE.

## Contributing

Contributions to this project are welcome. Feel free to fork the repository, make changes, and create a pull request.

## License

This project is licensed under [MIT License](LICENSE).

## Disclaimer

This repository contains code developed as part of a school assignment. It may not represent a production-ready
application and might contain limited functionalities.
