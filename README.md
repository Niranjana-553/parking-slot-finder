# ParkNow – Parking Slot Finder

ParkNow is an Android application developed as a college project to demonstrate how a parking-slot discovery and booking system could work. Users can explore parking locations, view parking-slot information, select a slot and booking time, and go through a booking and payment flow.

> **Project/Demo Note:** Parking-lot and parking-slot information is sample or simulated data for demonstration. The application is not connected to a live network of real parking facilities, so availability shown in the app should not be treated as real-world, real-time parking availability. This project is an academic prototype, not a production parking service.

## ✨ Features

- **User Registration and Login:** User authentication using Supabase.
- **Location-Based District Lookup:** Uses the device's location to identify the user's district.
- **Parking-Lot Browsing:** Explore parking locations and view their details.
- **Parking-Slot Display:** View parking slots for a selected parking lot.
- **Booking:** Select a parking slot and specify the booking start and end times.
- **Availability Checking:** Check for overlapping bookings in the backend data.
- **Online Payment:** Razorpay integration for payment order creation and verification.
- **My Bookings:** View booking records and cancel eligible bookings.
- **Parking-Lot Ratings:** View rating summaries and submit ratings.
- **Notifications:** View booking-related notifications.
- **User Profile:** View and update profile information.
- **Digital Receipt:** Generate PDF payment receipts.

## 🛠️ Technology Stack

| Part | Technologies |
|---|---|
| Android Frontend | Java, XML, Android Studio |
| Android Networking | Retrofit, Gson, OkHttp |
| Location | Google Play Services Location |
| Backend | Java, Spring Boot, Spring MVC |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Authentication | Supabase Authentication |
| Payment Integration | Razorpay |
| Backend Build Tool | Maven |
| Android Build Tool | Gradle |
| Version Control | Git and GitHub |

## 🏗️ Application Architecture

The project follows a frontend-backend architecture.

```text
             Android Application
                     |
                     | Retrofit HTTP Requests
                     |
                     v
             Spring Boot Backend
                     |
              +------+------+
              |             |
              v             v
         PostgreSQL     Razorpay
          Database      Payment API

       Android Application
                  |
                  v
          Supabase Authentication
```

- **Frontend:** The Android application provides the user interface and allows users to interact with the system.
- **Backend:** Spring Boot handles application logic, API requests, booking operations and database communication.
- **Database:** PostgreSQL stores application-related information.
- **Authentication:** Supabase Authentication handles user registration and login.
- **Payment Gateway:** Razorpay is integrated for payment order creation and verification.

## 📁 Project Structure

```text
ParkingSlotFinder/
│
├── frontend/                 # Android Studio project
│   ├── app/
│   │   └── src/main/
│   │       ├── java/com/example/parkingslotfinder/
│   │       ├── res/           # XML layouts and Android resources
│   │       └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── settings.gradle.kts
│
├── backend/                  # Spring Boot application
│   ├── src/main/java/com/parkingslot/parking_backend/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
└── README.md
```

## ⚙️ Prerequisites

Before running the project, make sure you have:

- Android Studio and the required Android SDK.
- A JDK compatible with the Java version configured in `backend/pom.xml`.
- PostgreSQL database access and the required database schema.
- A Supabase project configured for Authentication.
- Razorpay test-mode credentials if you want to test the payment functionality.
- An Android Emulator or a physical Android device.

## 🚀 Running the Project Locally

The application is currently intended to run as a local development/demo project. The Android application communicates with a Spring Boot backend that must be started separately.

### 1. Configure the Backend

The backend configuration is located at:

`backend/src/main/resources/application.properties`

The backend requires database and payment configuration.

| Environment Variable | Purpose |
|---|---|
| `DB_PASSWORD` | PostgreSQL database password |
| `RAZORPAY_KEY_ID` | Razorpay key ID |
| `RAZORPAY_KEY_SECRET` | Razorpay secret |

The backend uses PostgreSQL and Hibernate schema validation (`ddl-auto=validate`). The database and its expected tables must be available before starting the backend.

**Important:** Do not commit real database passwords or payment secrets to GitHub. Keep them in environment variables or a local configuration file excluded by `.gitignore`.

### 2. Start the Backend

Open a terminal in the `backend/` directory.

**Windows:**

```bash
mvnw.cmd spring-boot:run
```

**macOS/Linux:**

```bash
./mvnw spring-boot:run
```

The backend runs on port `8080` by default. Keep the backend running while using the application.

### 3. Configure the Android Application

Check the following configuration files:

- `ApiConfig.java` – Contains the Spring Boot API base URL.
- `SupabaseConfig.java` – Contains the Supabase project URL and client key.

The supplied `ApiConfig.java` currently uses:

```java
http://127.0.0.1:8080/
```

For the standard Android Emulator, use `10.0.2.2` to access a backend running on your development computer.

For a physical Android device, use your computer's reachable local-network IP address and ensure both devices can communicate over the network.

### 4. Run the Android Application

1. Open the `frontend/` folder in Android Studio.
2. Wait for Gradle synchronization to finish.
3. Ensure the Spring Boot backend is running.
4. Verify the API base URL and Supabase configuration.
5. Start an Android Emulator or connect a physical Android device.
6. Select the `app` run configuration and click **Run**.

The backend must remain running locally while the Android application makes API requests.

The project does not currently provide a publicly hosted backend or a ready-to-install public release.

## 🔗 Main Backend API Routes

| Route | Purpose |
|---|---|
| `/api/parking-lots` | Retrieve parking lots and parking-lot details |
| `/api/parking-slots` | Retrieve and manage parking slots |
| `/api/location/district` | Identify the district using latitude and longitude |
| `/api/bookings` | Create, retrieve and cancel bookings; check availability |
| `/api/payments` | Create Razorpay orders and verify payments |
| `/api/ratings` | Retrieve rating summaries and submit ratings |
| `/api/notifications` | Retrieve notifications and update read status |
| `/api/users` | Retrieve and update user profile information |

## ⚠️ Current Limitations

- Parking-lot and slot information is sample/simulated data.
- The application is not connected to live parking sensors or real parking operators.
- Parking availability displayed in the application is not real-time information.
- The backend is configured for local development and is not deployed to a public hosting service.
- The application requires the Spring Boot backend to be started separately during local testing.
- The project is an academic prototype and is not intended to be used as a production parking service.

## 🔮 Future Enhancements

- Integration of cameras for automatic vehicle detection and parking-slot occupancy updates.
- Connection with real parking facilities to obtain verified parking availability.
- Deployment of the backend to a cloud platform so users can access the application without running the backend locally.
- Real-time parking availability monitoring.
- Improvements to application security, reliability and scalability.

## 🔐 Security

- Never commit database passwords, Razorpay secrets, access tokens or other private credentials.
- If a real secret has already been pushed to GitHub, rotate it. Deleting it in a later commit does not remove it from Git history.
- Protect database access using appropriate authentication and access policies.
- Use HTTPS and production-grade configuration before any public deployment.

## 📌 Project Status

**Academic Project / Working Prototype**

Developed as a college project to demonstrate the design and implementation of an Android-based parking discovery and booking application.