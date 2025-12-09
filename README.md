# Kinto - System Zarządzania Restauracją

**Kinto** to kompleksowa aplikacja internetowa typu full-stack, zaprojektowana w celu cyfryzacji i usprawnienia działalności nowoczesnej restauracji z kuchnią gruzińską. Wyróżnia się architekturą opartą na dwóch interfejsach: **Web UI (Vaadin)** dla personelu i klientów oraz **REST API** dla zewnętrznych integracji.

---

## Spis treści
- [Kluczowe funkcje](#-kluczowe-funkcje)
- [Stos technologiczny](#-stos-technologiczny)
- [Wymagania systemowe](#-wymagania-systemowe)
- [Instalacja i konfiguracja](#-instalacja-i-konfiguracja)
- [Role użytkowników i domyślne dane logowania](#-role-użytkowników-i-domyślne-dane-logowania)
- [Dokumentacja REST API](#-dokumentacja-rest-api)
- [Testowanie](#-testowanie)
- [Zespół projektowy](#-zespół-projektowy)

---

## Kluczowe funkcje

### 1. **Architektura Klient-Serwer**
- **Backend:** Spring Boot (Java 17) 
- **Frontend:** Vaadin Flow (interfejs w Javie) 
- 
### 2. **Kontrola dostępu oparta na rolach (RBAC)**
- Bezpieczne uwierzytelnianie przy użyciu **Spring Security**.
- **JWT (JSON Web Token)** dla bezpiecznego dostępu do API.
- Odrębne widoki UI i uprawnienia API dla ról: `ADMIN`, `WAITER` (Kelner), `CHEF` (Szef Kuchni), `INVENTORY_MANAGER` (Kierownik Magazynu), `CUSTOMER` (Klient).

### 3. **Główne moduły**
- **Menu i Dania:** Pełny CRUD, przesyłanie zdjęć, powiązanie składników z daniami oraz recenzje i oceny klientów.
- **Zarządzanie Zamówieniami:** Obsługa przepływu pracy (Nowe -> W przygotowaniu -> Gotowe -> Podane -> Opłacone) z systemem **Kitchen Display System (KDS)** dla kucharzy.
- **Rezerwacje:** System rezerwacji stolików z wykrywaniem konfliktów.
- **Magazyn i Łańcuch Dostaw:** Automatyczne odliczanie stanów magazynowych, przepływ zamówień dostaw (Utworzone -> Wysłane -> Odebrane) oraz alerty o niskim stanie zapasów.
- **Zarządzanie Personelem:** Planowanie zmian, śledzenie obecności (widżet Wejście/Wyjście) oraz pulpity KPI (przepracowane godziny, zmiany).
- **Rozliczenia i Płatności:** Obsługa dzielenia rachunków, historia płatności i generowanie paragonów.
- **Powiadomienia:** Alerty w czasie rzeczywistym w aplikacji o gotowych zamówieniach i niskim stanie magazynowym.

### 4. **Zarządzanie Danymi**
- **Import/Eksport:** Obsługa wymiany danych w formatach **JSON** i **XML** dla Menu i Użytkowników.
- **Data Seeding:** Automatyczne uzupełnianie bazy danych danymi demonstracyjnymi przy pierwszym uruchomieniu.

---

## 🛠 Stos technologiczny

- **Język:** Java 17
- **Framework:** Spring Boot 3.x
- **Framework UI:** Vaadin 24 (Flow)
- **Baza danych:** PostgreSQL (Produkcja), H2 (Testy)
- **ORM:** Hibernate / Spring Data JPA
- **Bezpieczeństwo:** Spring Security, JWT (JJWT)
- **Narzędzie budowania:** Gradle
- **Testy:** JUnit 5, Mockito, AssertJ, Spring Boot Test
- **Narzędzia:** Swagger UI  

---

## Wymagania systemowe

* **JDK:** Java 17 lub nowsza
* **Baza danych:** PostgreSQL 13+
* **Narzędzie budowania:** Gradle (wrapper dołączony do projektu)

---

## Instalacja i konfiguracja

### 1. Konfiguracja bazy danych
Upewnij się, że PostgreSQL jest uruchomiony i utwórz bazę danych o nazwie `georgian_restaurant`.
Zaktualizuj plik `src/main/resources/application.properties`, jeśli Twoje dane logowania są inne:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/georgian_restaurant
spring.datasource.username=postgres
spring.datasource.password=Post098ewq@#
```

## Role użytkowników i domyślne dane logowania

Aplikacja automatycznie tworzy następujących użytkowników przy pierwszym uruchomieniu:

| Rola | Email | Hasło | Dostęp |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@kinto.com` | `admin` | Pełny dostęp do Panelu Admina, Użytkowników, Danych, Raportów. |
| **WAITER** | `waiter@kinto.com` | `waiter` | Zamówienia, Rezerwacje, Płatności, Menu. |
| **CHEF** | `chef@kinto.com` | `chef` | System Kuchenny (KDS), Grafik. |
| **MANAGER** | `manager@kinto.com`| `manager` | Magazyn, Łańcuch Dostaw, Raporty. |
| **CUSTOMER**| *(Rejestracja)* | *(Dowolne)* | Rezerwacja stolików, Przeglądanie Menu, Dodawanie opinii. |

---

## Dokumentacja REST API

System udostępnia bogate API. Poniżej znajduje się szczegółowy wykaz dostępnych punktów końcowych (endpoints) pogrupowanych według modułów.

### Auth Controller (Uwierzytelnianie)
* `POST /api/auth/register` – Rejestracja nowego użytkownika.
* `POST /api/auth/login` – Logowanie i pobranie tokena JWT.

### User Management (Użytkownicy)
* `GET /api/users` – Pobranie listy wszystkich użytkowników.
* `GET /api/users/{email}` – Wyszukanie użytkownika po emailu.
* `PUT /api/users/{id}` – Aktualizacja danych użytkownika.
* `DELETE /api/users/{id}` – Usunięcie użytkownika.

### Menu Management (Menu)
* `GET /api/menu` – Pobranie pełnego menu restauracji.
* `POST /api/menu` – Dodanie nowego dania (wymaga uprawnień).
* `GET /api/menu/{id}` – Pobranie szczegółów dania.
* `PUT /api/menu/{id}` – Aktualizacja dania.
* `DELETE /api/menu/{id}` – Usunięcie dania.

### Order Management (Zamówienia)
* `GET /api/orders` – Pobranie aktywnych zamówień.
* `POST /api/orders` – Utworzenie nowego zamówienia.
* `GET /api/orders/{id}` – Szczegóły konkretnego zamówienia.
* `PUT /api/orders/{id}` – Aktualizacja statusu zamówienia (np. PREPARING -> READY).
* `DELETE /api/orders/{id}` – Anulowanie/usunięcie zamówienia.
* `GET /api/orders/stats` – Pobranie statystyk dashboardu (przychód, ilość zamówień).

### Reservation Management (Rezerwacje)
* `GET /api/reservations` – Lista wszystkich rezerwacji.
* `POST /api/reservations` – Utworzenie nowej rezerwacji.
* `GET /api/reservations/{id}` – Szczegóły rezerwacji.
* `PUT /api/reservations/{id}` – Edycja rezerwacji.
* `DELETE /api/reservations/{id}` – Anulowanie rezerwacji.

### Inventory & Supply Chain (Magazyn i Dostawy)
* `GET /api/inventory` – Pobranie listy składników i ich stanów magazynowych.
* `POST /api/inventory` – Dodanie lub aktualizacja składnika.
* `DELETE /api/inventory/{id}` – Usunięcie składnika.
* `GET /api/supply/suppliers` – Lista dostawców.
* `POST /api/supply/suppliers` – Dodanie dostawcy.
* `PUT /api/supply/suppliers/{id}` – Edycja dostawcy.
* `DELETE /api/supply/suppliers/{id}` – Usunięcie dostawcy.
* `GET /api/supply/orders` – Lista zamówień dostaw.
* `POST /api/supply/orders/{id}/receive` – Oznaczenie dostawy jako odebranej (automatycznie zwiększa stany magazynowe).
* `DELETE /api/supply/orders/{id}` – Usunięcie zamówienia dostawy.

### Staff Management (Personel)
* `GET /api/shifts` – Pobranie grafiku zmian.
* `POST /api/shifts` – Przypisanie nowej zmiany pracownikowi.
* `PUT /api/shifts/{id}` – Edycja zmiany.
* `DELETE /api/shifts/{id}` – Usunięcie zmiany.

### Payment System (Płatności)
* `POST /api/payments/{orderId}` – Przetworzenie płatności dla danego zamówienia (obsługa dzielenia rachunków).

### Reviews & Ratings (Opinie)
* `GET /api/reviews/{dishId}` – Pobranie opinii dla danego dania.
* `POST /api/reviews/{dishId}` – Dodanie opinii.
* `PUT /api/reviews/{id}` – Edycja opinii.
* `DELETE /api/reviews/{id}` – Usunięcie opinii.

### Notifications (Powiadomienia)
* `GET /api/notifications/{userEmail}` – Pobranie powiadomień dla użytkownika.
* `PUT /api/notifications/{id}/read` – Oznaczenie powiadomienia jako przeczytane.
* `DELETE /api/notifications/{id}` – Usunięcie powiadomienia.

### Data Export (Eksport Danych)
* `GET /api/export/menu/json` (oraz `/xml`) – Eksport menu.
* `GET /api/export/users/json` (oraz `/xml`) – Eksport użytkowników.
* `GET /api/export/reservations/json` (oraz `/xml`) – Eksport rezerwacji.
* `GET /api/profile/export/json` (oraz `/xml`) – Eksport danych profilowych.

---

## Specyfikacja OpenAPI: /v3/api-docs

Aplikacja automatycznie generuje specyfikację API zgodną ze standardem **OpenAPI 3.0** (wcześniej znane jako Swagger Specification).

Endpoint `/v3/api-docs` zwraca surowy dokument JSON, który opisuje całą strukturę API REST projektu, w tym:
* Wszystkie dostępne ścieżki (paths/endpoints).
* Obsługiwane metody HTTP (GET, POST, PUT, DELETE).
* Oczekiwane parametry wejściowe (request bodies, query params).
* Struktury zwracanych danych (Schemas/DTOs), np. User, Dish, Order.
* Wymagania dotyczące autoryzacji (np. format Bearer Token).


1. **Swagger UI:** Interfejs graficzny dostępny pod `/swagger-ui/index.html` korzysta z tego pliku, aby wygenerować interaktywną dokumentację.
2. **Integracje:** Zewnętrzne narzędzia (np. Postman) mogą zaimportować ten adres URL, aby automatycznie skonfigurować kolekcję żądań.
3. **Generowanie Klientów:** Na podstawie tego pliku można automatycznie wygenerować kod klienta API dla Frontendów (np. w TypeScript/Angular/React) lub aplikacji mobilnych.

Aby pobrać specyfikację, uruchom aplikację i wejdź na:
`http://localhost:8080/v3/api-docs`

---

## 🧪 Testowanie

Projekt utrzymuje wysoką jakość kodu dzięki zestawowi **~40 testów** obejmujących scenariusze jednostkowe i integracyjne.

### Uruchamianie testów
```bash
./gradlew test