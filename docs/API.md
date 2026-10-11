# HTTP API

All payloads and responses are JSON unless exporting. Authentication uses an HttpOnly session cookie. Fetch `GET /api/auth/session` before sign-in; it returns a CSRF token. Send `X-CSRF-Token` on every POST/PUT/DELETE, including login/logout. Login rotates both session ID and token; use the returned token afterward.

## Session

| Method/path | Request | Response |
|---|---|---|
| GET /api/auth/session | — | csrf plus user when signed in |
| POST /api/auth/login | email, password | csrf and user(id, name, role) |
| POST /api/auth/logout | empty body | ok |
| GET /api/dashboard | — | date, occupancy/counts, reservation/room/guest arrays, activity |

## Six resource endpoints

Replace `{module}` with guests, rooms, reservations, staff, services or reviews.

| Method/path | Behavior |
|---|---|
| GET /api/{module} | List records (no password hashes) |
| GET /api/{module}/{id} | Read one record |
| POST /api/{module} | Create; returns 201 and generated id |
| PUT /api/{module}/{id} | Update complete editable form; returns id |
| DELETE /api/{module}/{id} | Delete subject to business/FK rules |
| GET /api/{module}/export | Manager-only UTF-8 .txt JSON download |

PUT expects all required editable fields, not a partial patch. Unknown properties are ignored. `id`, derived totals, prices and password hashes cannot be set by clients. Total and cancellation fee are recomputed server-side on reservation saves. Blank staff password during update preserves the current hash.

## Create payloads

```json
{"name":"Nimal Perera","email":"nimal@example.com","phone":"0771234567","type":"VIP","discount_rate":0.10}
```

```json
{"number":"301","type":"SUITE","capacity":4,"base_price":10000,"luxury_tax":0.10,"status":"AVAILABLE"}
```

```json
{"guest_id":1,"room_id":4,"check_in":"2026-11-01","check_out":"2026-11-03","party_size":2,"type":"ADVANCE","status":"CONFIRMED"}
```

```json
{"name":"Desk Agent","email":"agent@example.com","phone":"0771234567","role":"RECEPTIONIST","shift":"MORNING","password":"ChangeMe12345"}
```

```json
{"name":"Yoga","category":"WELLNESS","type":"COMPLIMENTARY","base_cost":0,"status":"ACTIVE","description":"Morning studio session."}
```

```json
{"author":"Anonymous","type":"ANONYMOUS","reservation_id":null,"rating":5,"text":"Excellent stay.","status":"PUBLISHED"}
```

For verified feedback use type VERIFIED with an existing CHECKED_OUT reservation_id. Ratings are 1–5. See schema.sql for supported values and numeric constraints.

## Permission matrix

| Resource | Manager | Receptionist |
|---|---|---|
| Guests | CRUD + export | CRUD |
| Rooms | CRUD + export | Read |
| Reservations | CRUD + export | CRUD |
| Staff | CRUD + export | No access |
| Services | CRUD + export | Read |
| Reviews | CRUD + export | Read |
| Dashboard | Read | Read |

## Errors

Errors use `{"error":"Human-readable explanation"}`. 400 invalid fields/domain rule; 401 sign-in required or invalid login; 403 insufficient role/CSRF; 404 unknown record; 409 duplicate/foreign-key conflict; 413 body limit; 429 login throttled; 500 unexpected failure. Mutation requests are limited to 16,384 characters. List endpoints return the entire small teaching dataset; no server pagination is implemented.
