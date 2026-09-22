package com.example.parkingslotfinder;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private Button confirmButton;

    private TextView slotText;
    private TextView priceText;
    private TextView durationText;
    private TextView totalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        slotText = findViewById(R.id.slotText);
        priceText = findViewById(R.id.priceText);
        durationText = findViewById(R.id.durationText);
        totalText = findViewById(R.id.totalText);
        confirmButton = findViewById(R.id.confirmButton);
        int slotId = getIntent().getIntExtra(
                "slotId",
                 -1
        );

        int parkingLotId = getIntent().getIntExtra(
                "parkingLotId",
                -1
        );

        String slotNumber = getIntent().getStringExtra(
                "slotNumber"
        );

        String parkingLotName = getIntent().getStringExtra(
                "parkingLotName"
        );
        if (slotId == -1) {

            Ui.toast(
                    BookingActivity.this,
                    "Invalid parking slot."
            );

            finish();
            return;
        }

        if (parkingLotId == -1) {

            Ui.toast(
                    BookingActivity.this,
                    "Invalid parking lot."
            );

            finish();
            return;
        }
        double price = getParkingPrice(parkingLotName);
        if (slotNumber != null && !slotNumber.isEmpty()) {

            slotText.setText(
                    "Selected slot: " + slotNumber
            );

        } else {

            slotText.setText(
                    "Selected slot: " + slotId
            );
        }
        if (price > 0) {

            priceText.setText(
                    "Price: ₹"
                            + formatPrice(price)
                            + "/hour"
            );

        } else {

            priceText.setText(
                    "Price: Not specified"
            );
        }
        durationText.setText(
                "Duration: 1 hour"
        );
        if (price > 0) {

            totalText.setText(
                    "Total: ₹"
                            + formatPrice(price)
            );

        } else {

            totalText.setText(
                    "Total: Not specified"
            );
        }
        confirmButton.setOnClickListener(v -> {

            if (price <= 0) {

                Ui.toast(
                        BookingActivity.this,
                        "Price is not available."
                );

                return;
            }

            createBooking(
                    slotId,
                    parkingLotId,
                    price
            );
        });
    }

    private double getParkingPrice(String parkingLotName) {

        if (parkingLotName == null) {
            return 0.0;
        }

        String name = parkingLotName
                .trim()
                .toLowerCase(Locale.US);

        // Supreme = ₹120
        if (name.contains("supreme")) {
            return 120.0;
        }

        // Chinnakada = ₹90
        if (name.contains("chinnakada")) {
            return 90.0;
        }

        // Ashramam = ₹50
        if (name.contains("ashramam")) {
            return 50.0;
        }

        // Kollam = ₹70
        if (name.contains("kollam")) {
            return 70.0;
        }

        return 0.0;
    }
    private void createBooking(
            int slotId,
            int parkingLotId,
            double price
    ) {
        confirmButton.setEnabled(false);

        Date now = new Date();
        Date endTime = new Date(
                now.getTime()
                        + (60 * 60 * 1000)
        );


        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ssXXX",
                        Locale.US
                );

        String startTime = sdf.format(now);

        String endTimeString = sdf.format(endTime);
        SessionManager sessionManager =
                new SessionManager(
                        BookingActivity.this
                );

        String userId =
                sessionManager.getUserId();

        if (userId == null || userId.isEmpty()) {

            confirmButton.setEnabled(true);

            Ui.toast(
                    BookingActivity.this,
                    "Please login again."
            );

            return;
        }
        ParkingApi api =
                ApiClient.spring().create(
                        ParkingApi.class
                );
        api.createBooking(
                userId,
                slotId,
                parkingLotId,
                startTime,
                endTimeString
        ).enqueue(
                new Callback<Booking>() {

                    @Override
                    public void onResponse(
                            Call<Booking> call,
                            Response<Booking> response) {

                        confirmButton.setEnabled(true);
                        if (response.isSuccessful()) {

                            Ui.toast(
                                    BookingActivity.this,
                                    "Booking confirmed! ₹"
                                            + formatPrice(price)
                                            + " for 1 hour."
                            );

                            finish();

                            return;
                        }

                        String errorMessage =
                                "Booking failed ("
                                        + response.code()
                                        + ")";

                        try {

                            if (response.errorBody() != null) {

                                String errorBody =
                                        response.errorBody()
                                                .string();

                                Log.e(
                                        "BOOKING_ERROR",
                                        errorBody
                                );

                                errorMessage +=
                                        ": " + errorBody;
                            }

                        } catch (Exception e) {

                            Log.e(
                                    "BOOKING_ERROR",
                                    "Could not read error response",
                                    e
                            );
                        }

                        Ui.toast(
                                BookingActivity.this,
                                errorMessage
                        );
                    }

                    @Override
                    public void onFailure(
                            Call<Booking> call,
                            Throwable t) {

                        confirmButton.setEnabled(true);

                        Log.e(
                                "BOOKING_ERROR",
                                "Backend error",
                                t
                        );

                        Ui.toast(
                                BookingActivity.this,
                                "Backend error: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }

    private String formatPrice(double price) {

        if (price == (long) price) {

            return String.valueOf(
                    (long) price
            );
        }

        return String.format(
                Locale.US,
                "%.2f",
                price
        );
    }
}