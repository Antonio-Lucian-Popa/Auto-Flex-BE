# AutoFlex Backend

Platformă Peer-to-Peer de închiriere mașini — Spring Boot + Keycloak + PostgreSQL

---

## 🔐 Autentificare

- Integrare Keycloak (login, register, token)
- Roluri: CLIENT, OWNER, ADMIN
- Acces restricționat prin `@PreAuthorize`

---

## 🚘 Mașini

- `POST /api/cars` — creare mașină cu imagini
- `GET /api/cars` — listare cu filtre:
    - căutare (brand/model)
    - locație
    - transmisie
    - combustibil
    - preț minim/maxim
    - sortare + paginare
- `GET /api/cars/{id}` — detalii mașină

---

## 📅 Booking

- `POST /api/bookings` — rezervare mașină
- `PUT /api/bookings/{id}/status` — actualizare status
- `GET /api/bookings/user` — rezervările mele
- `GET /api/bookings/car/{carId}` — rezervările unei mașini (OWNER only)
- `GET /api/bookings/car/{carId}/occupied` — intervale ocupate pentru calendar

✅ Validări:
- Nu poți rezerva propria mașină
- Nu poți suprapune perioada cu alt booking

---

## 🌟 Review-uri

- `POST /api/reviews` — după booking finalizat
- `GET /api/reviews/car/{carId}` — review-uri la mașină
- `GET /api/reviews/user` — review-uri proprii

---

## 🛠 Features tehnice

- Spring Boot 3.x
- Java 17
- PostgreSQL + Liquibase
- DTO-uri + ModelMapper
- Validare & Global Exception Handler
- Scheduler: auto-completare rezervări expirate
- Imagini servite via `/images/**`

---

## 📂 Fișiere media

Imaginile se salvează în:
- `src/main/resources/static/images/cars/{carId}/{imageName}`

- Și pot fi accesate direct din browser:
- `http://localhost:8080/images/cars/{carId}/{imageName}`

### Asigură-te că Keycloak rulează pe portul configurat și PostgreSQL e disponibil.