# Class diagrams and database relationships

These Mermaid diagrams render in GitHub and compatible Markdown editors. Package names follow the provided blueprint; SQL repositories replace file-only primary persistence while module FileManagers retain read/write features.

```mermaid
classDiagram
  Entity <|-- User
  Entity <|-- Room
  Entity <|-- Reservation
  Entity <|-- HotelService
  Entity <|-- Review
  User <|-- Guest
  Guest <|-- VIPGuest
  User <|-- Staff
  Staff <|-- Manager
  Staff <|-- Receptionist
  Room <|-- StandardRoom
  Room <|-- SuiteRoom
  Reservation <|-- AdvanceBooking
  Reservation <|-- WalkInBooking
  HotelService <|-- PaidService
  HotelService <|-- ComplimentaryService
  Review <|-- VerifiedStayReview
  Review <|-- AnonymousReview
  Reservation --> Guest : discounts
  Reservation --> Room : nightly pricing
  VerifiedStayReview --> Reservation : completed stay
  class Entity {
    <<abstract>>
    -long id
    +getId() long
    +setId(long)
    +getDisplayTag()* String
  }
  class User {
    <<abstract>>
    -String name
    -String email
    +getAccessLevel()* String
  }
  class Guest {
    +calculateDiscount(BigDecimal) BigDecimal
  }
  class VIPGuest {
    -BigDecimal discountRate
    +calculateDiscount(BigDecimal) BigDecimal
  }
  class Room {
    <<abstract>>
    -String roomNumber
    -BigDecimal basePrice
    +calculateFinalPrice()* BigDecimal
  }
  class SuiteRoom {
    -BigDecimal luxuryTax
    +calculateFinalPrice() BigDecimal
  }
  class Reservation {
    <<abstract>>
    -long guestId
    -long roomId
    -LocalDate checkIn
    -LocalDate checkOut
    +getNights() long
    +calculateTotal(Room, Guest) BigDecimal
    +calculateCancellationFee(BigDecimal)* BigDecimal
  }
  class Staff {
    <<abstract>>
    -String shift
    +getShift() String
  }
  class HotelService {
    <<abstract>>
    -String serviceName
    -BigDecimal baseCost
    +calculateCost()* BigDecimal
  }
  class Review {
    <<abstract>>
    -String text
    -int rating
    +getRating() int
  }
```

```mermaid
classDiagram
  HttpServlet <|-- ApiServlet
  ApiServlet <|-- CrudServlet
  ApiServlet <|-- AuthServlet
  ApiServlet <|-- DashboardServlet
  CrudServlet <|-- GuestServlet
  CrudServlet <|-- RoomServlet
  CrudServlet <|-- ReservationServlet
  CrudServlet <|-- StaffServlet
  CrudServlet <|-- ServiceServlet
  CrudServlet <|-- ReviewServlet
  CrudServlet --> HotelServiceLayer
  CrudServlet --> FileManager
  HotelServiceLayer --> SqlRepository
  HotelServiceLayer --> DomainFactory
  Repository <|.. SqlRepository
  SqlRepository --> Database
  FileManager <|-- GuestFileManager
  FileManager <|-- RoomFileManager
  FileManager <|-- ReservationFileManager
  FileManager <|-- StaffFileManager
  FileManager <|-- ServiceFileManager
  FileManager <|-- ReviewFileManager
  AuthServlet --> Passwords
  class Repository {
    <<interface>>
    +findAll() List
    +findById(long) Map
    +save(long, Map) long
    +delete(long)
  }
  class HotelServiceLayer {
    +list(Module) List
    +save(Module, long, Map, long) long
    +delete(Module, long, long)
  }
  class FileManager {
    +writeSnapshot(List) Path
    +readSnapshot(Path) JsonArray
    +audit(String)$
    +recentActivity()$ List
  }
```

```mermaid
erDiagram
  guests ||--o{ reservations : makes
  rooms ||--o{ reservations : assigned
  reservations o|--o{ reviews : verifies
  guests {
    bigint id PK
    varchar name
    varchar email UK
    varchar phone
    varchar type
    decimal discount_rate
  }
  rooms {
    bigint id PK
    varchar number UK
    varchar type
    int capacity
    decimal base_price
    decimal luxury_tax
    varchar status
  }
  reservations {
    bigint id PK
    bigint guest_id FK
    bigint room_id FK
    date check_in
    date check_out
    int party_size
    varchar type
    varchar status
    decimal total
    decimal cancellation_fee
  }
  staff {
    bigint id PK
    varchar name
    varchar email UK
    varchar phone
    varchar role
    varchar shift
    varchar password_hash
  }
  services {
    bigint id PK
    varchar name UK
    varchar category
    varchar type
    decimal base_cost
    varchar status
    varchar description
  }
  reviews {
    bigint id PK
    bigint reservation_id FK
    varchar author
    varchar type
    int rating
    varchar text
    varchar status
  }
```

`staff` and `services` are independent operational entities. An anonymous review has a null reservation FK. The database enforces this distinction. The internal `app_metadata` table holds a one-time sample import marker.
