# Dispatch Load Balancer - Spring Boot Assignment

A REST-based dispatch optimization system that assigns delivery orders to vehicles 
based on capacity, priority, and geographic proximity.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- PostgreSQL
- Maven
- JUnit 5
- Mockito
- Swagger (OpenAPI)
- Haversine formula for distance calculation

---

## 📌 Features

### 1️⃣ Order Management
- Upsert (create/update) delivery orders
- Each order includes:
  - `orderId`
  - `latitude`
  - `longitude`
  - `address`
  - `packageWeight`
  - `priority` (HIGH / MEDIUM / LOW)

### 2️⃣ Vehicle Management
- Upsert vehicle data
- Each vehicle includes:
  - `vehicleId`
  - `capacity`
  - `currentLatitude`
  - `currentLongitude`
  - `currentAddress`

### 3️⃣ Dispatch Optimization
- High priority orders assigned first
- Vehicle capacity constraints respected
- Travel distance minimized using Haversine formula
- Nearest-neighbour routing per vehicle

### 4️⃣ Output Dispatch Plan
Returns:
- Assigned vehicles with routes
- Total load per vehicle
- Total distance travelled
- Unassigned orders (with reason)
- Summary statistics

---

## 🏗 Architecture & Design

- Clean layered architecture
- Controller → Service → Repository
- DTO separation from Entity
- Greedy assignment algorithm
- Deterministic sorting for consistent output
- Efficient upsert operations

---

## 🧮 Dispatch Algorithm

1. Sort orders by:
   - Priority (HIGH → LOW)
   - Weight (descending)
   - OrderId (for deterministic output)

2. Assign each order to:
   - Vehicle with enough remaining capacity
   - Minimum distance score

3. Optimize per-vehicle route using:
   - Nearest Neighbor heuristic

4. Distance calculated using:
   - Haversine Formula

---
