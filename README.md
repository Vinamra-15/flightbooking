Flight Booking (in-memory) - Short README

Overview

Small Spring Boot service (Java 17) providing in-memory flight and booking management.
Core entities: Flight(flightNumber,totalSeats,bookedSeats) and Booking(id,flightNumber,passengerName,seatsBooked).

How to run

- Ensure Java 17 is available. On Windows, if JAVA_HOME points to an older JDK, run:
  $env:JAVA_HOME='C:\\Program Files\\Java\\jdk-17'

- From repo root (Windows):
  .\mvnw.cmd -DskipTests spring-boot:run

- Or build and run jar:
  .\mvnw.cmd -DskipTests package
  java -jar target\flightbooking-0.0.1-SNAPSHOT.jar

HTTP API (examples)

1) Create a flight

Request:
  ```\
    curl -i -X POST http://localhost:8080/flights \
    -H "Content-Type: application/json" \
    -d '{"flightNumber":"F100","totalSeats":10}'
```

Expected response:
  HTTP/1.1 201 Created
  Location: http://localhost:8080/flights/F100
  (empty body)

2) Create a booking

Request:
```
  curl -i -X POST http://localhost:8080/bookings \
    -H "Content-Type: application/json" \
    -d '{"flightNumber":"F100","passengerName":"Alice","seatsBooked":2}'
```

Expected response (201):
  HTTP/1.1 201 Created
  Content-Type: application/json
  Location: http://localhost:8080/bookings/{id}

  Body (example):
  ```
  {
    "id": "b5f3e2a8-...",
    "flightNumber": "F100",
    "passengerName": "Alice",
    "seatsBooked": 2
  }
```

3) Overbooking attempt

Request (if only 2 seats remain and you request 3):
 ```
  curl -i -X POST http://localhost:8080/bookings \
    -H "Content-Type: application/json" \
    -d '{"flightNumber":"F100","passengerName":"Bob","seatsBooked":9}'
```

Expected response (409):
  HTTP/1.1 409 Conflict
  ```
  Body: {
    "code": "CONFLICT",
    "message": "Not enough seats available",
    "timestamp": "2026-06-15T19:26:12.875188300Z",
    "errors": null
}
```

4) Cancel a booking

Request:
```
  curl -i -X DELETE http://localhost:8080/bookings/{id}
```

Expected response:
  HTTP/1.1 204 No Content

Error cases and status codes

- 400 Bad Request: validation failures (missing fields, seatsBooked < 0)
- 404 Not Found: flight or booking not found, e.g. Body :
```
{
    "code": "NOT_FOUND",
    "message": "Booking not found or already cancelled: {id}",
    "timestamp": "2026-06-15T19:26:42.112634Z",
    "errors": null
}
```
- 409 Conflict: duplicate flight creation or overbooking
- 500 Internal Server Error: unexpected errors

Notes

- In-memory storage only; restarting the app clears all flights/bookings.
- No authentication, no search — clients must know flightNumber.


What would I have improved given more time?
- Fix concurrency/consistency: make booking+persist atomic (use per-flight lock or transactional DB) and avoid removing booking before decrementing    
  seats.                                                                                                                                               
- Persistent storage: replace in-memory maps with a simple DB (H2/JPA) so that the state survives restarts.                                                     
- Add tests unit and integration.                                                                      
- Add API docs (springdoc/OpenAPI) and health/metrics (actuator + Micrometer).                                                                      
- Improve error messages & HTTP semantics and include request IDs and structured logging (SLF4J) for tracing.                                                      
- Introduce interfaces for repositories + DI to ease swapping implementations.
- Introduce observability (Prometheus / newrelic) and push metrics accordingly. 
