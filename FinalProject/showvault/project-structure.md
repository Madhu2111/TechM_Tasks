# ShowVault Project Structure

## Directory Structure

```
showvault/
├── backend/                  # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── showvault/
│   │   │   │           ├── controller/    # REST controllers
│   │   │   │           ├── model/         # Entity classes
│   │   │   │           ├── repository/    # Data repositories
│   │   │   │           ├── service/       # Business logic
│   │   │   │           ├── config/        # Configuration classes
│   │   │   │           ├── exception/     # Custom exceptions
│   │   │   │           ├── dto/           # Data Transfer Objects
│   │   │   │           ├── util/          # Utility classes
│   │   │   │           └── ShowVaultApplication.java  # Main class
│   │   │   └── resources/
│   │   │       ├── application.properties  # Application configuration
│   │   │       ├── static/                 # Static resources
│   │   │       └── templates/              # Template files
│   │   └── test/                           # Test classes
│   ├── pom.xml                             # Maven configuration
│   └── README.md                           # Backend documentation
├── frontend/                               # Angular application
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/                 # Angular components
│   │   │   │   ├── user/                   # User module components
│   │   │   │   ├── organizer/              # Organizer module components
│   │   │   │   ├── admin/                  # Admin module components
│   │   │   │   └── shared/                 # Shared components
│   │   │   ├── services/                   # Angular services
│   │   │   ├── models/                     # Data models
│   │   │   ├── guards/                     # Route guards
│   │   │   ├── interceptors/              # HTTP interceptors
│   │   │   ├── pipes/                      # Custom pipes
│   │   │   ├── directives/                # Custom directives
│   │   │   └── app.module.ts              # Main module
│   │   ├── assets/                         # Static assets
│   │   ├── environments/                   # Environment configurations
│   │   ├── styles.css                      # Global styles
│   │   └── index.html                      # Main HTML file
│   ├── angular.json                        # Angular configuration
│   ├── package.json                        # NPM dependencies
│   └── README.md                           # Frontend documentation
├── database/                               # Database scripts
│   ├── schema.sql                          # Database schema
│   └── test-data.sql                       # Test data
├── docs/                                   # Project documentation
│   ├── api-docs/                           # API documentation
│   ├── user-manual/                        # User manual
│   └── development-guide/                  # Development guide
└── README.md                               # Project overview
```

## Key Components

### Backend Components

- **Controllers**: Handle HTTP requests and responses
- **Models**: Define database entities
- **Repositories**: Interface with the database
- **Services**: Implement business logic
- **DTOs**: Transfer data between layers
- **Config**: Configure application components

### Frontend Components

- **User Module**: Components for user registration, login, show browsing, and ticket booking
- **Organizer Module**: Components for show management and analytics
- **Admin Module**: Components for system administration
- **Shared Components**: Reusable UI components
- **Services**: Handle API communication and state management

### Database

- **Schema**: Database structure definition
- **Test Data**: Sample data for development and testing

### Documentation

- **API Documentation**: Swagger documentation for REST APIs
- **User Manual**: End-user documentation
- **Development Guide**: Guide for developers