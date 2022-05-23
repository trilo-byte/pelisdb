# PelisDB

A small API for managing movies as part as a challenge for RedPoints. It takes advantage of spring boot for rapid development.

##  🚀 Getting started

### Third party libraries
- Spring Boot 2.5.7
  Spring Boot is Spring's convention-over-configuration solution for creating stand-alone, production-grade Spring-based Applications that you can "just run".
    - spring-boot-starter-web
    - spring-boot-starter-validation
    - spring-boot-starter-actuator
    - spring-boot-starter-security
    - spring-boot-starter-data-jpa
    - spring-boot-starter-cache
    - spring-boot-starter-test
    - spring-boot-maven-plugin
- Springfox 3.0.0
  The Springfox suite of java libraries are all about automating the generation of machine and human readable specifications for JSON APIs written using the spring family of projects.
- [Mapstruct](https://mapstruct.org/) 1.4.2.
  Final Code generator that greatly simplifies the implementation of mappings between Java bean types.
- openapi- generator
  OpenAPI Generator allows generation of API client libraries (SDK generation), server stubs, documentation and configuration automatically given an OpenAPI Spec (v2, v3)

### 🔧 Installation

To run and develop over this service you must have the following requirements:
- [OpenJDK 11](https://adoptopenjdk.net/installation.html#installers)
- [Maven 3.6.3](https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/).
- Docker

Run `mvn clean verify` to check that everything works.

### Execution

If you want you can start the service locally with
```
cd pelisdb-application
mvn spring-boot:run
```
Once is up you can check it on
```
http://localhost:8083/actuator/health
```

### ⚙️ Local testing
If you want to run the test from console, just:
```
mvn test
```
Or from the IDE you would like

## 🛠 Technical design
### Architecture diagram
![Arquitecture](assets/pelisdb_Arq.png)
### Project structure
- pelisdb-application: just the spring boot application
- pelisdb-contract: Artifact thats generates REST API controller's interfaces and models from specificationyml, **APIFirst**
- pelisdb-persistence: All the task to put and extract data from Database
- pelisdb-presentation: Controllers layer
- pelisdb-services: All core logic

### Layers

This project uses an N-Layer architecture, containing the following packages:

- /aspect: Aspects layer
- /config: Spring java-configurations
- /controllers: Implementation of REST API controllers (REST endpoints).
- /exceptions: Hierarchy of exceptions specific to the microservice.
- /mappers: Mapstruct mappers between entities and DTOs.
- /persistence: entities and repository for data presistence
- /security: Manages configuration for spring security
- /services: Services which provides two way communication between layers, and manages transactions.
- /utils: Library for messages translation

### Security
The user needs to be authorized in order to use the API REST.
For rapid development and just for demoing pursopues basic-auth is used
```
user@mock.es (No password) for read access 
admin@mock.es (No password) for read/write access
```

## ️ Author

* **Antonio Otero Andría** - **a.otero.andria@gmail.com**

