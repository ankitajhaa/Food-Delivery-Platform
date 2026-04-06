# Food Delivery Platform

A microservices-based food delivery platform built with Spring Boot, demonstrating asynchronous communication, service discovery, load balancing, centralized configuration, and resilience patterns.

---

## Architecture Overview

```
                          ┌─────────────────┐
                          │   API Gateway   │
                          │   :8080         │
                          └────────┬────────┘
                                   │ routes via Eureka
              ┌────────────────────┼────────────────────┐
              │                    │                    │
     ┌────────▼──────┐    ┌────────▼──────┐   ┌────────▼──────┐
     │ Order-Service │    │Restaurant Svc │   │Delivery-Svc   │
     │   :8081       │    │ :8082 / :8086 │   │   :8083       │
     └───────┬───────┘    └───────┬───────┘   └───────┬───────┘
             │ publishes          │ consumes           │ Feign+CB
             │                   │                    ▼
     ┌───────▼───────────────────┴──────┐    ┌────────▼──────┐
     │           RabbitMQ               │    │ Rider-Service │
     │  orders.exchange / delivery.exch │    │   :8084       │
     └───────┬──────────────────────────┘    └───────────────┘
             │ consumes
     ┌───────▼───────┐
     │Analytics-Svc  │
     │   :8085       │
     └───────────────┘

Support Services:
  Config Server  :8888  (Git-backed centralized config)
  Eureka Server  :8761  (Service discovery & registry)
  PostgreSQL     :5432  (One database per service)
  RabbitMQ       :5672  (Async messaging broker)
```

### Services

| Service | Port | Responsibility |
|---|---|---|
| API Gateway | 8080 | Routing, logging, timeout, circuit breaking |
| Config Server | 8888 | Centralized configuration with Git backend |
| Eureka Server | 8761 | Service discovery and registry |
| Order-Service | 8081 | Order placement, publishes OrderCreated events |
| Restaurant-Service | 8082 / 8086 | Consumes OrderCreated events, 2 instances for LB demo |
| Delivery-Service | 8083 | Delivery CRUD, Feign+CB to Rider-Service, publishes DeliveryCreated |
| Rider-Service | 8084 | Consumes DeliveryCreated events, rider assignment |
| Analytics-Service | 8085 | Consumes OrderCreated events, stores metrics |

### Messaging Flow

```
Order created
  → OrderCreated event → orders.exchange
      → restaurant.queue → Restaurant-Service (processes order)
      → analytics.queue  → Analytics-Service (records metric)

Delivery created
  → Delivery-Service calls Rider-Service via Feign (sync, circuit breaker)
  → DeliveryCreated event → delivery.exchange
      → rider.queue → Rider-Service (async fallback assignment)
```

### Key Design Decisions

**Why RabbitMQ instead of direct API calls:** Order-Service does not need to wait for Restaurant-Service or Analytics-Service to process before returning a response. Using async messaging decouples these services — if Restaurant-Service is down, the order is still persisted and the event sits in the queue until the consumer recovers.

**Dual path on Delivery:** Delivery-Service first tries a synchronous Feign call to Rider-Service (fast path). If that fails (circuit breaker open or service down), the DeliveryCreated event in RabbitMQ acts as a fallback — Rider-Service picks it up and assigns a rider asynchronously.

---

## Prerequisites

- Docker Desktop
- Java 21+
- Maven

---

## Setup & Running

### Option 1 — Full Docker (recommended)

```bash
git clone <repo-url>
cd food-delivery
docker compose up --build
```

Wait for all services to start (approx 2-3 minutes on first run). Startup order is enforced via healthchecks: Postgres → RabbitMQ → Config Server → Eureka → Microservices → API Gateway.

### Option 2 — Local Development (IntelliJ)

1. Start infrastructure:
```bash
docker compose up postgres rabbitmq -d
```

2. Run services in IntelliJ in this order, waiting a few seconds between each:
    - ConfigServerApplication
    - EurekaServerApplication
    - OrderServiceApplication
    - RestaurantServiceApplication
    - DeliveryServiceApplication
    - RiderServiceApplication
    - AnalyticsServiceApplication

### Verify Everything is Running

| Check | URL |
|---|---|
| Eureka Dashboard | http://localhost:8761 |
| RabbitMQ Management | http://localhost:15672 (guest/guest) |
| Config Server | http://localhost:8888/order-service/default |
| API Gateway | http://localhost:8080 |

All 5 microservices + 2 restaurant instances should appear as UP in Eureka.

---

## API Documentation

All requests below can go directly to the service port, or through the API Gateway on port 8080.

### Order-Service (port 8081)

#### Create Order
```
POST /orders
Content-Type: application/json

{
  "restaurantId": 1
}
```
Response `201 Created`:
```json
{
  "id": 1,
  "restaurantId": 1,
  "status": "CREATED"
}
```
Triggers: `OrderCreated` event published to `orders.exchange` → consumed by Restaurant-Service and Analytics-Service.

#### Get Order by ID
```
GET /orders/{id}
```
Response `200 OK`:
```json
{
  "id": 1,
  "restaurantId": 1,
  "status": "CREATED"
}
```

---

### Restaurant-Service (port 8082)

#### Get Restaurant by ID
```
GET /restaurants/{id}
```
Response `200 OK`:
```json
{
  "id": 1,
  "name": "Pizza Palace"
}
```

#### Get Instance Info (Load Balancing Demo)
```
GET /restaurants/instance-info
```
Returns the port of the responding instance. Repeated calls will alternate between 8082 and 8086 via the load balancer.

---

### Delivery-Service (port 8083)

#### Create Delivery
```
POST /deliveries
Content-Type: application/json

{
  "orderId": 1
}
```
Response `201 Created`:
```json
{
  "id": 1,
  "orderId": 1,
  "riderId": 1,
  "status": "ASSIGNED"
}
```
Triggers: Feign call to Rider-Service (sync), then `DeliveryCreated` event published.

#### Get Delivery by ID
```
GET /deliveries/{id}
```

#### Get Delivery Details (API Composition)
```
GET /deliveries/{id}/details
```
Response `200 OK`:
```json
{
  "deliveryId": 1,
  "deliveryStatus": "ASSIGNED",
  "orderId": 1,
  "orderStatus": "CREATED",
  "restaurantId": 1,
  "riderId": 1,
  "riderName": "Ali",
  "riderZone": "North"
}
```
Aggregates data from Order-Service and Rider-Service via Feign.

#### Circuit Breaker Endpoints
```
POST /deliveries/simulate-failure     — force circuit breaker OPEN
POST /deliveries/simulate-recovery    — reset circuit breaker to CLOSED
GET  /deliveries/circuit-breaker/status — check current state
```

---

### Rider-Service (port 8084)

#### Get Rider by ID
```
GET /riders/{id}
```
Response `200 OK`:
```json
{
  "id": 1,
  "name": "Ali",
  "zone": "North"
}
```

---

### Analytics-Service (port 8085)

#### Get All Metrics
```
GET /analytics
```
Response `200 OK`:
```json
[
  { "id": 1, "metricType": "ORDER_CREATED", "value": "1" },
  { "id": 2, "metricType": "ORDER_CREATED", "value": "2" }
]
```

#### Get Metrics by Type
```
GET /analytics/{type}
```
Example: `GET /analytics/ORDER_CREATED`

---

## Seeded Data

Restaurant and Rider data is pre-seeded via `init-db.sql` on first Docker startup.

**Restaurants:**
| ID | Name |
|---|---|
| 1 | Pizza Palace |
| 2 | Burger Barn |
| 3 | Sushi Spot |

**Riders:**
| ID | Name | Zone |
|---|---|---|
| 1 | Ali | North |
| 2 | Sara | South |
| 3 | Ravi | East |

---

## Demonstrating Key Features

### Load Balancing

Start the system and run repeated requests to the restaurant endpoint via the Gateway:

```bash
for i in {1..6}; do curl -s http://localhost:8080/restaurants/instance-info; echo; done
```

You should see responses alternating between port 8082 and 8086.

### Circuit Breaker

1. Force the circuit breaker open:
```bash
curl -X POST http://localhost:8083/deliveries/simulate-failure
```

2. Try creating a delivery — rider assignment will fail gracefully with fallback:
```bash
curl -X POST http://localhost:8083/deliveries \
  -H "Content-Type: application/json" \
  -d '{"orderId": 1}'
```

3. Check circuit breaker state:
```bash
curl http://localhost:8083/deliveries/circuit-breaker/status
```

4. Recover:
```bash
curl -X POST http://localhost:8083/deliveries/simulate-recovery
```

### Messaging Flow

1. Create an order via POST /orders
2. Open RabbitMQ at http://localhost:15672
3. Check Queues tab — `restaurant.queue` and `analytics.queue` will show message activity
4. Check Analytics-Service — a new metric record will be saved for each order

---

## Port Reference

| Service | Local Port |
|---|---|
| API Gateway | 8080 |
| Order-Service | 8081 |
| Restaurant-Service (instance 1) | 8082 |
| Delivery-Service | 8083 |
| Rider-Service | 8084 |
| Analytics-Service | 8085 |
| Restaurant-Service (instance 2) | 8086 |
| Eureka Server | 8761 |
| Config Server | 8888 |
| PostgreSQL | 5432 |
| RabbitMQ AMQP | 5672 |
| RabbitMQ Management | 15672 |