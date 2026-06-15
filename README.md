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
  curl -i -X POST http://localhost:8080/flights \
    -H "Content-Type: application/json" \
    -d '{"flightNumber":"F100","totalSeats":10}'

Expected response:
  HTTP/1.1 201 Created
  Location: http://localhost:8080/flights/F100
  (empty body)

2) Create a booking

Request:
  curl -i -X POST http://localhost:8080/bookings \
    -H "Content-Type: application/json" \
    -d '{"flightNumber":"F100","passengerName":"Alice","seatsBooked":2}'

Expected response (201):
  HTTP/1.1 201 Created
  Content-Type: application/json
  Location: http://localhost:8080/bookings/{id}

  Body (example):
  {
    "id": "b5f3e2a8-...",
    "flightNumber": "F100",
    "passengerName": "Alice",
    "seatsBooked": 2
  }

3) Overbooking attempt

Request (if only 2 seats remain and you request 3):
  curl -i -X POST http://localhost:8080/bookings \
    -H "Content-Type: application/json" \
    -d '{"flightNumber":"F100","passengerName":"Bob","seatsBooked":9}'

Expected response (409):
  HTTP/1.1 409 Conflict
  Body: Not enough seats available

4) Cancel a booking

Request:
  curl -i -X DELETE http://localhost:8080/bookings/{id}

Expected response:
  HTTP/1.1 204 No Content

Error cases and status codes

- 400 Bad Request: validation failures (missing fields, seatsBooked < 0)
- 404 Not Found: flight or booking not found
- 409 Conflict: duplicate flight creation or overbooking
- 500 Internal Server Error: unexpected errors

Concurrency and correctness

- Seat allocation is protected by a per-flight ReentrantLock. Concurrent bookings for the same flight are serialized to prevent overbooking.
- Flight creation is atomic using putIfAbsent to avoid duplicates under concurrent requests.

Notes

- In-memory storage only; restarting the app clears all flights/bookings.
- No authentication, no search — clients must know flightNumber.

