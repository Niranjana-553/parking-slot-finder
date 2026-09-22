# ParkNow frontend integration

This version is wired to the backend/API structure documented for the project.

## 1. Supabase credentials

Edit:

`app/src/main/java/com/example/parkingslotfinder/SupabaseConfig.java`

Set:
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY` (publishable/anon key)

Never put the Supabase service-role/secret key in the Android app.

## 2. Spring Boot URL

`ApiConfig.BASE_URL` is:

`http://10.0.2.2:8080/`

This is correct when the Android app runs in the standard Android emulator and Spring Boot runs on the development computer.

For a physical phone, use the computer's LAN IP instead and make sure the phone can reach the backend.

## 3. Implemented flow

Splash
-> Supabase session check
-> Login/Register
-> Home (GET /api/parking-lots)
-> Parking slots (GET /api/parking-slots/available/lot/{parkingLotId})
-> Booking (POST /api/bookings)
-> My Bookings (GET /api/bookings/user/{userId})
-> Cancel (PUT /api/bookings/{bookingId}/cancel)
-> Notifications (GET /api/notifications/user/{userId}, mark read)
-> Profile/logout

## 4. Booking UI

The backend contract documented for this project accepts:
- `p_slot_id`
- `p_start_time`
- `p_end_time`

The starter booking screen currently creates a one-hour booking starting now. Replace this with date/time pickers for the final UI.

## 5. Backend limitation to remember

The documented booking service reserves the selected slot when a booking is created and does not yet implement overlapping time-range availability. That is a backend limitation, not a frontend bug.

## 6. Profile

No confirmed profile-management REST endpoint was included in the supplied backend documentation, so this integration does not invent one. The Profile screen currently shows the authenticated Supabase email/user ID and provides logout.

## 7. If requests return 401

Make sure:
1. Login succeeds through Supabase.
2. `SessionManager` contains the access token.
3. Spring Boot validates the Supabase JWT.
4. The Android app is calling the correct backend host/port.

## 8. Build

Open the `ParkingSlotFinder` folder in Android Studio, let Gradle sync, then run the `app` configuration.
