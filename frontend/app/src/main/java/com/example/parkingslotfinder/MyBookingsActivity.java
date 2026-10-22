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
import android.content.Intent;

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


                    loadBookings(uid, new HashMap<>(), new HashMap<>());
                    return;
                }

                Map<Integer, String> lotNames = new HashMap<>();
                Map<Integer, String> slotNames = new HashMap<>();

                for (ParkingLot lot : response.body()) {

                    if (lot.id != null) {

                        String name = lot.name;

                        if (name == null || name.trim().isEmpty()) {
                            name = "Parking lot";
                        }

                        lotNames.put(lot.id, name);
                    }
                }

                loadAllSlots(uid, lotNames);
            }

            @Override
            public void onFailure(
                    Call<List<ParkingLot>> call,
                    Throwable t) {

                // If parking lots fail, still show bookings
                loadBookings(uid, new HashMap<>(), new HashMap<>());
            }
        });
    }



    private void loadAllSlots(
            String uid,
            Map<Integer, String> lotNames) {

        ParkingApi api =
                ApiClient.spring().create(ParkingApi.class);

        api.getAllParkingSlots().enqueue(
                new Callback<List<ParkingSlot>>() {

                    @Override
                    public void onResponse(
                            Call<List<ParkingSlot>> call,
                            Response<List<ParkingSlot>> response) {

                        Map<Integer, String> slotNames =
                                new HashMap<>();

                        if (response.isSuccessful()
                                && response.body() != null) {

                            for (ParkingSlot slot : response.body()) {

                                if (slot.id != null
                                        && slot.slotNumber != null
                                        && !slot.slotNumber.trim().isEmpty()) {

                                    slotNames.put(
                                            slot.id,
                                            slot.slotNumber
                                    );
                                }
                            }
                        }

                        loadBookings(uid, lotNames, slotNames);
                    }

                    @Override
                    public void onFailure(
                            Call<List<ParkingSlot>> call,
                            Throwable t) {

                        // Bookings can still be displayed without slot names.
                        loadBookings(uid, lotNames, new HashMap<>());
                    }
                });
    }


    private void loadBookings(
            String uid,
            Map<Integer, String> lotNames,
            Map<Integer, String> slotNames) {

        ParkingApi api =
                ApiClient.spring().create(ParkingApi.class);

        api.getUserBookings(uid)
                .enqueue(new Callback<List<Booking>>() {

                    @Override
                    public void onResponse(
                            Call<List<Booking>> call,
                            Response<List<Booking>> response) {

                        list.removeAllViews();
                        android.util.Log.d("MY_BOOKINGS", "Bookings response received");

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
                        android.util.Log.d(
                                "MY_BOOKINGS",
                                "Number of bookings: " + bookings.size()
                        );

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


                            details.append("\nParking lot: ")
                                    .append(parkingLotName);

                            String slotNumber = "Unknown";

                            if (b.slotId != null) {
                                String foundSlot = slotNames.get(b.slotId);

                                if (foundSlot != null && !foundSlot.trim().isEmpty()) {
                                    slotNumber = foundSlot;
                                }
                            }

                            details.append("\nSlot: ")
                                    .append(slotNumber);

                            details.append("\n")
                                    .append(formatBookingTime(b.startTime))
                                    .append(" → ")
                                    .append(formatBookingTime(b.endTime));

                            boolean hasEnded = false;

                            try {
                                hasEnded = b.endTime != null
                                        && OffsetDateTime.parse(b.endTime)
                                        .isBefore(OffsetDateTime.now());
                            } catch (Exception e) {
                                // If the end time cannot be parsed,
                                // keep the original booking status.
                            }

                            String displayStatus;

                            if (hasEnded) {
                                displayStatus = "Ended";
                            } else {
                                displayStatus = b.status != null
                                        ? b.status
                                        : "Unknown";
                            }

                            details.append("\nStatus: ")
                                    .append(displayStatus);

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

                            t.setTextColor(android.graphics.Color.WHITE);

                            row.setBackgroundColor(android.graphics.Color.rgb(25, 58, 90));
                            row.setPadding(24, 20, 24, 20);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                            params.setMargins(0, 0, 0, 20);
                            row.setLayoutParams(params);

                            row.addView(t);



                            final String receiptParkingLot = parkingLotName;
                            final String receiptSlotNumber = slotNumber;
                            final String receiptStatus = displayStatus;

                            Button receiptButton = Ui.button(
                                    MyBookingsActivity.this,
                                    "View Receipt"
                            );

                            receiptButton.setOnClickListener(v -> {
                                Intent intent = new Intent(
                                        MyBookingsActivity.this,
                                        ReceiptActivity.class
                                );

                                intent.putExtra("bookingId", String.valueOf(b.id));
                                intent.putExtra("parkingLot", receiptParkingLot);
                                intent.putExtra("slotNumber", receiptSlotNumber);
                                intent.putExtra("startTime", formatBookingTime(b.startTime));
                                intent.putExtra("endTime", formatBookingTime(b.endTime));
                                intent.putExtra("status", receiptStatus);

                                intent.putExtra(
                                        "totalCost",
                                        b.totalCost != null
                                                ? String.valueOf(b.totalCost)
                                                : "Not available"
                                );

                                startActivity(intent);
                            });

                            row.addView(receiptButton);



                            // Show cancel button only for confirmed bookings
                            if (!hasEnded
                                    && "confirmed".equalsIgnoreCase(b.status)) {

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