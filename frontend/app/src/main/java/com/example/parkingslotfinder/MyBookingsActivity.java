package com.example.parkingslotfinder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MyBookingsActivity extends AppCompatActivity {

    private LinearLayout list;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_my_bookings);

        list = findViewById(R.id.bookingList);

        load();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (list != null) {
            load();
        }
    }

    private void load() {

        String uid = new SessionManager(this).getUserId();

        if (uid == null || uid.isEmpty()) {
            Ui.toast(this, "Please login again.");
            return;
        }

        ParkingApi api = ApiClient.spring().create(ParkingApi.class);

        // First load parking lots so we can convert parkingLotId -> parking lot name
        api.getParkingLots().enqueue(new Callback<List<ParkingLot>>() {

            @Override
            public void onResponse(
                    Call<List<ParkingLot>> call,
                    Response<List<ParkingLot>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Ui.toast(
                            MyBookingsActivity.this,
                            "Could not load parking lots."
                    );

                    // Still try to load bookings
                    loadBookings(uid, new HashMap<>());
                    return;
                }

                Map<Integer, String> lotNames = new HashMap<>();

                for (ParkingLot lot : response.body()) {

                    if (lot.id != null) {

                        String name = lot.name;

                        if (name == null || name.trim().isEmpty()) {
                            name = "Parking lot";
                        }

                        lotNames.put(lot.id, name);
                    }
                }

                loadBookings(uid, lotNames);
            }

            @Override
            public void onFailure(
                    Call<List<ParkingLot>> call,
                    Throwable t) {

                // If parking lots fail, still show bookings
                loadBookings(uid, new HashMap<>());
            }
        });
    }

    private void loadBookings(
            String uid,
            Map<Integer, String> lotNames) {

        ParkingApi api =
                ApiClient.spring().create(ParkingApi.class);

        api.getUserBookings(uid)
                .enqueue(new Callback<List<Booking>>() {

                    @Override
                    public void onResponse(
                            Call<List<Booking>> call,
                            Response<List<Booking>> response) {

                        list.removeAllViews();

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            list.addView(
                                    Ui.text(
                                            MyBookingsActivity.this,
                                            "Could not load bookings.",
                                            16
                                    )
                            );

                            return;
                        }

                        List<Booking> bookings = response.body();

                        if (bookings.isEmpty()) {

                            list.addView(
                                    Ui.text(
                                            MyBookingsActivity.this,
                                            "No bookings yet.",
                                            16
                                    )
                            );

                            return;
                        }

                        for (Booking b : bookings) {

                            LinearLayout row =
                                    new LinearLayout(
                                            MyBookingsActivity.this
                                    );

                            row.setOrientation(
                                    LinearLayout.VERTICAL
                            );

                            // Get parking lot name
                            String parkingLotName =
                                    "Unknown parking lot";

                            if (b.parkingLotId != null) {

                                String foundName =
                                        lotNames.get(b.parkingLotId);

                                if (foundName != null
                                        && !foundName.trim().isEmpty()) {

                                    parkingLotName = foundName;

                                } else {

                                    parkingLotName =
                                            "Parking lot #"
                                                    + b.parkingLotId;
                                }
                            }

                            // Build booking information
                            StringBuilder details =
                                    new StringBuilder();

                            details.append("Booking: ")
                                    .append(b.id);

                            details.append("\nParking lot: ")
                                    .append(parkingLotName);

                            details.append("\nSlot: ")
                                    .append(
                                            b.slotId != null
                                                    ? b.slotId
                                                    : "Unknown"
                                    );

                            details.append("\n")
                                    .append(formatBookingTime(b.startTime))
                                    .append(" → ")
                                    .append(formatBookingTime(b.endTime));

                            details.append("\nStatus: ")
                                    .append(
                                            b.status != null
                                                    ? b.status
                                                    : "Unknown"
                                    );

                            if (b.totalCost != null) {

                                details.append("\nTotal: ₹")
                                        .append(b.totalCost);
                            }

                            TextView t =
                                    Ui.text(
                                            MyBookingsActivity.this,
                                            details.toString(),
                                            15
                                    );

                            row.addView(t);

                            // Show cancel button only for confirmed bookings
                            if ("confirmed".equalsIgnoreCase(b.status)) {

                                Button cancel =
                                        Ui.button(
                                                MyBookingsActivity.this,
                                                "Cancel"
                                        );

                                cancel.setOnClickListener(
                                        v -> cancelBooking(
                                                String.valueOf(b.id)
                                        )
                                );

                                row.addView(cancel);
                            }

                            list.addView(row);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Booking>> call,
                            Throwable t) {

                        Ui.toast(
                                MyBookingsActivity.this,
                                "Backend error: "
                                        + t.getMessage()
                        );
                    }
                });
    }

    private String formatBookingTime(String time) {
        try {
            return OffsetDateTime.parse(time)
                    .atZoneSameInstant(ZoneId.of("Asia/Kolkata"))
                    .format(DateTimeFormatter.ofPattern(
                            "dd MMM, hh:mm a",
                            Locale.getDefault()
                    ));
        } catch (Exception e) {
            return time;
        }
    }

    private void cancelBooking(String id) {

        ParkingApi api =
                ApiClient.spring().create(ParkingApi.class);

        api.cancelBooking(id)
                .enqueue(new Callback<Booking>() {

                    @Override
                    public void onResponse(
                            Call<Booking> call,
                            Response<Booking> response) {

                        Ui.toast(
                                MyBookingsActivity.this,
                                response.isSuccessful()
                                        ? "Booking cancelled."
                                        : "Cancellation failed."
                        );

                        if (response.isSuccessful()) {
                            load();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Booking> call,
                            Throwable t) {

                        Ui.toast(
                                MyBookingsActivity.this,
                                "Backend error: "
                                        + t.getMessage()
                        );
                    }
                });
    }
}