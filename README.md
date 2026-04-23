# Smart Campus Sensor & Room Management API



---

## Project Overview

This project is a RESTful web service built using JAX-RS (Jersey). It models a Smart Campus system that manages rooms and IoT sensors (e.g., temperature, CO₂), along with sensor readings.

The API enables:

* Room management (create, retrieve, delete)
* Sensor registration and monitoring
* Sensor reading logging and retrieval
* Query-based filtering of sensors
* API discovery via a root endpoint

---

## Architecture and Design

### Singleton DAO Pattern

The application uses an in-memory data store implemented via the DAO pattern with a Singleton design. This ensures that data is shared consistently across HTTP requests.

### HATEOAS-Based Discovery

A root endpoint (`/api/v1`) provides API metadata and resource links, allowing clients to dynamically navigate the API.

### Data Integrity Constraints

* Unique identifiers are enforced for all resources
* Rooms cannot be deleted if associated sensors exist
* Sensors must reference valid rooms

---

## Technology Stack

* Java (JDK 8 or higher)
* JAX-RS (Jersey)
* Maven
* Apache Tomcat 9.x

---

## Getting Started

### Prerequisites

* Java 8 or higher
* Maven 3.6 or higher
* Apache Tomcat 9.x

---

### Build and Run

#### 1. Clone the Repository

```bash
git clone <https://github.com/NishalFdo/CSA-SmartCampus.git>

```

#### 2. Build the Project

```bash
mvn clean install
```

Alternatively, in NetBeans:
Right-click the project and select "Clean and Build".

#### 3. Deploy

Copy the generated `.war` file into the Tomcat `webapps` directory.


---

## Base URL

```
http://localhost:8080/SmartCampusAPI/api/v1
```

---

## API Endpoints

### Discovery

| Method | Endpoint | Description                     |
| ------ | -------- | ------------------------------- |
| GET    | /api/v1  | Retrieve API metadata and links |

---

### Rooms

| Method | Endpoint    | Description              |
| ------ | ----------- | ------------------------ |
| GET    | /rooms      | Retrieve all rooms       |
| GET    | /rooms/{id} | Retrieve a specific room |
| POST   | /rooms      | Create a new room        |
| DELETE | /rooms/{id} | Delete a room            |

---

### Sensors

| Method | Endpoint | Description           |
| ------ | -------- | --------------------- |
| GET    | /sensors | Retrieve all sensors  |
| POST   | /sensors | Register a new sensor |

---

### Sensor Readings

| Method | Endpoint               | Description              |
| ------ | ---------------------- | ------------------------ |
| GET    | /sensors/{id}/readings | Retrieve sensor readings |
| POST   | /sensors/{id}/readings | Add a new sensor reading |

---

## Sample CURL Commands

### Create a Room

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms -H "Content-Type: application/json" -H "Accept:application/json" -d "{\"id\":\"ROOM-001\",\"name\":\"LIBRARY\",\"capacity\":100}"
```

---

### Retrieve All Rooms

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms -H "Accept: application/json"
```

---

### Retrieve a Room by ID

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/ROOM-001 -H "Accept: application/json"
```

---

### Register a Sensor

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":111,\"roomId\":\"ROOM-001\"}"
```

---

### Filter Sensors by Type

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature" -H "Accept: application/json"
```

---
# Report

## Part 1: Service Architecture and Setup

### Question 1.1: Default Lifecycle of a JAX-RS Resource Class and In-Memory Data Synchronization

**Lifecycle**
By default, JAX-RS treats resource classes as request-scoped. A new instance of the resource class is created for each incoming HTTP request and destroyed after the response is returned. Resource classes are not singletons by default.

**Impact on Data Structures**
Because a new instance is created per request, any in-memory data structures (e.g., `HashMap`, `ArrayList`) declared as instance variables are reinitialized for each request. This results in data loss between API calls. To maintain persistence, such data structures must be declared as `static` or managed through a Singleton-based data access layer.

**Race Conditions**
Since the server (e.g., Tomcat) handles multiple requests concurrently using threads, shared in-memory data structures are vulnerable to race conditions. To ensure thread safety, concurrent collections such as `ConcurrentHashMap` or `CopyOnWriteArrayList` should be used, or updates should be controlled using synchronization mechanisms.

---

### Question 1.2: HATEOAS and the Benefits of Hypermedia

**Why It Is a Hallmark of REST**
HATEOAS (Hypermedia as the Engine of Application State) represents the highest level of the Richardson Maturity Model. Instead of returning only data, the API provides dynamic links that indicate possible next actions.

**Benefits Over Static Documentation**
Static documentation requires clients to hardcode URIs, making them fragile to changes. With HATEOAS, clients navigate the API dynamically using links provided in responses. This reduces coupling between client and server, allows URI evolution without breaking clients, and makes the API self-descriptive.

---

## Part 2: Room Management

### Question 2.1: Implications of Returning Only IDs vs. Full Room Objects

**Returning Only IDs**
Returning only identifiers reduces payload size and improves initial response speed. However, it introduces the "N+1 request problem," where the client must make additional requests to retrieve full details, increasing latency and network overhead.

**Returning Full Objects**
Returning complete resource representations increases payload size but eliminates additional requests. This reduces overall latency and is generally preferred in modern web and mobile applications where minimizing round-trips is critical.

---

### Question 2.2: Is the DELETE Operation Idempotent?

Yes, the DELETE operation is idempotent.

**Justification**
In REST, an operation is idempotent if multiple identical requests result in the same server state as a single request. For example, sending `DELETE /api/v1/rooms/LIB-301` multiple times results in the room being removed after the first request. Subsequent requests may return `404 Not Found`, but the server state remains unchanged after the initial deletion.

---

## Part 3: Sensor Operations and Linking

### Question 3.1: Handling Incorrect Media Types (`@Consumes`)

**Technical Consequences**
If a client sends a request with an unsupported media type (e.g., `text/plain` or `application/xml`) to an endpoint that only consumes `application/json`, the request is rejected before reaching the resource method.

**Framework Handling**
JAX-RS automatically returns an HTTP `415 Unsupported Media Type` response. This ensures that invalid input formats are handled at the framework level, preventing application-level parsing errors.

---

### Question 3.2: Query Parameters vs. Path Parameters for Filtering

**Why Query Parameters Are Preferred**
In REST design, path parameters identify specific resources (e.g., `/sensors/{id}`), while query parameters are used for filtering, sorting, and pagination (e.g., `/sensors?type=CO2`).

Using path-based filtering leads to rigid and less scalable URI structures. Query parameters are flexible and composable, allowing multiple filters such as:

```
/sensors?type=CO2&status=ACTIVE
```

---

## Part 4: Deep Nesting with Sub-Resources

### Question 4.1: Architectural Benefits of the Sub-Resource Locator Pattern

The Sub-Resource Locator pattern allows delegation of nested paths to separate resource classes. For example, `/sensors/{id}/readings` can be handled by a dedicated `SensorReadingResource` instead of being embedded within a single `SensorResource` class.

**Benefits**

* Promotes the Single Responsibility Principle
* Improves code organization and maintainability
* Prevents overly large ("God") classes
* Reduces routing conflicts in JAX-RS

---

## Part 5: Advanced Error Handling, Exception Mapping, and Logging

### Question 5.2: Why HTTP 422 Is More Accurate Than 404 for Missing References

A `404 Not Found` indicates that the requested endpoint does not exist. In contrast, `422 Unprocessable Entity` indicates that the request is syntactically correct but semantically invalid.

For example, if a request contains a valid JSON payload with a `roomId` that does not exist, the endpoint itself is valid, but the data is incorrect. Therefore, `422` is the more appropriate response.

---

### Question 5.4: Cybersecurity Risks of Exposing Internal Stack Traces

**Information Disclosure**
Exposing stack traces reveals sensitive details such as framework versions, internal file structures, and execution paths.

**Exploitation Risk**
Attackers can use this information to identify known vulnerabilities (CVEs) and craft targeted attacks. Suppressing stack traces in production environments is essential for security.

---

### Question 5.5: Advantages of JAX-RS Filters for Cross-Cutting Concerns

**Separation of Concerns**
Logging is a cross-cutting concern that should not be embedded within business logic.

Using JAX-RS filters (`ContainerRequestFilter`, `ContainerResponseFilter`) allows centralized handling of logging. This ensures consistent logging across all endpoints while keeping resource classes clean and focused on core functionality.


