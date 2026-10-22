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
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;
import android.widget.AdapterView;
import java.util.Calendar;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

public class BookingActivity extends AppCompatActivity
implements PaymentResultWithDataListener {

    private Button confirmButton;

    private TextView slotText;
    private TextView priceText;
    private TextView durationText;
    private TextView totalText;
    private TextView availabilityText;

    private boolean isAvailabilityKnown = false;
    private boolean isSlotAvailable = false;

    private retrofit2.Call<AvailabilityResponse> availabilityCall;
    private int availabilityCheckId = 0;

    private TimePicker startTimePicker;
    private Spinner durationSpinner;

    private int selectedDuration = 1;
    private double hourlyPrice = 0;

    private String userId;
    private String currentOrderId;

    private int slotId;
    private int parkingLotId;

    private String startTime;
    private String endTimeString;

    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Checkout.preload(getApplicationContext());
        slotText = findViewById(R.id.slotText);
        priceText = findViewById(R.id.priceText);
        durationText = findViewById(R.id.durationText);
        totalText = findViewById(R.id.totalText);
        confirmButton = findViewById(R.id.confirmButton);
        startTimePicker = findViewById(R.id.startTimePicker);
        durationSpinner = findViewById(R.id.durationSpinner);
        availabilityText = findViewById(R.id.availabilityText);

        slotId = getIntent().getIntExtra("slotId", -1);
        parkingLotId = getIntent().getIntExtra("parkingLotId", -1);

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
        double price = getIntent().getDoubleExtra(
                "price",
                0.0
        );
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

        hourlyPrice = price;

        durationSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        selectedDuration = position + 1;

                        durationText.setText(
                                "Duration: " + selectedDuration +
                                        (selectedDuration == 1 ? " hour" : " hours")
                        );

                        if (hourlyPrice > 0) {
                            totalText.setText(
                                    "Total: ₹" +
                                            formatPrice(hourlyPrice * selectedDuration)
                            );
                        } else {
                            totalText.setText("Total: Not specified");
                        }
                        checkAvailability();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        startTimePicker.setOnTimeChangedListener(
                (view, hourOfDay, minute) -> checkAvailability()
        );

        confirmButton.setOnClickListener(v -> {

            if (!isAvailabilityKnown || !isSlotAvailable) {
                Ui.toast(this, "Please check slot availability first.");
                return;
            }

            if (hourlyPrice <= 0) {
                Ui.toast(
                        BookingActivity.this,
                        "Price is not available."
                );
                return;
            }

            startPayment(slotId, parkingLotId);
        });

        String[] durations = {
                "1 hour", "2 hours", "3 hours",
                "4 hours", "5 hours", "6 hours",
                "7 hours", "8 hours", "9 hours",
                "10 hours", "11 hours", "12 hours"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                durations
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        durationSpinner.setAdapter(adapter);
        checkAvailability();
    }



    private void startPayment(int slotId, int parkingLotId) {

        confirmButton.setEnabled(false);

        Calendar startCalendar = Calendar.getInstance();

        startCalendar.set(
                Calendar.HOUR_OF_DAY,
                startTimePicker.getHour()
        );

        startCalendar.set(
                Calendar.MINUTE,
                startTimePicker.getMinute()
        );

        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Date startDate = startCalendar.getTime();

        if (!startDate.after(new Date())) {
            confirmButton.setEnabled(true);
            Ui.toast(this, "Please select a future time.");
            return;
        }

        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.add(Calendar.HOUR_OF_DAY, selectedDuration);

        Date endDate = endCalendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                Locale.US
        );

        String startTime = sdf.format(startDate);

        String endTimeString = sdf.format(endDate);

        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getUserId();

        if (userId == null || userId.isEmpty()) {
            confirmButton.setEnabled(true);
            Ui.toast(this, "Please login again.");
            return;
        }

        CreatePaymentRequest request = new CreatePaymentRequest(
                userId,
                (long) slotId,
                (long) parkingLotId,
                startTime,
                endTimeString
        );

        ParkingApi api = ApiClient.spring().create(ParkingApi.class);

        api.createPaymentOrder(request).enqueue(
                new Callback<CreatePaymentResponse>() {

                    @Override
                    public void onResponse(
                            Call<CreatePaymentResponse> call,
                            Response<CreatePaymentResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) {

                            confirmButton.setEnabled(true);

                            String errorMessage = "No error details available";

                            try {
                                if (response.errorBody() != null) {
                                    errorMessage = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e("PAYMENT_ERROR", "Could not read error response", e);
                            }

                            Log.e(
                                    "PAYMENT_ERROR",
                                    "HTTP status: " + response.code()
                                            + ", Response: " + errorMessage
                            );

                            Ui.toast(
                                    BookingActivity.this,
                                    "Payment order failed. HTTP " + response.code()
                            );

                            return;
                        }

                        CreatePaymentResponse payment = response.body();

                        if (payment.orderId == null
                                || payment.keyId == null
                                || payment.amount <= 0) {

                            confirmButton.setEnabled(true);
                            Ui.toast(
                                    BookingActivity.this,
                                    "Invalid payment details."
                            );
                            return;
                        }

                        currentOrderId = payment.orderId;

                        openRazorpayCheckout(payment);
                    }

                    @Override
                    public void onFailure(
                            Call<CreatePaymentResponse> call,
                            Throwable t) {

                        confirmButton.setEnabled(true);

                        Log.e("PAYMENT_ERROR", "Order creation failed", t);

                        Ui.toast(
                                BookingActivity.this,
                                "Could not connect to backend."
                        );
                    }
                }
        );
    }

    private void checkAvailability() {

        // Ignore responses from older checks.
        int checkId = ++availabilityCheckId;

        if (availabilityCall != null) {
            availabilityCall.cancel();
        }

        isAvailabilityKnown = false;
        isSlotAvailable = false;
        confirmButton.setEnabled(false);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, startTimePicker.getHour());
        startCalendar.set(Calendar.MINUTE, startTimePicker.getMinute());
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Date startDate = startCalendar.getTime();

        if (!startDate.after(new Date())) {
            availabilityText.setText(
                    "Select a future start time."
            );
            return;
        }

        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.add(Calendar.HOUR_OF_DAY, selectedDuration);

        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                Locale.US
        );

        String start = sdf.format(startDate);
        String end = sdf.format(endCalendar.getTime());

        availabilityText.setText("Checking availability...");

        ParkingApi api = ApiClient.spring().create(ParkingApi.class);

        availabilityCall = api.checkAvailability(slotId, start, end);

        availabilityCall.enqueue(
                new Callback<AvailabilityResponse>() {

                    @Override
                    public void onResponse(
                            Call<AvailabilityResponse> call,
                            Response<AvailabilityResponse> response) {

                        if (checkId != availabilityCheckId) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            availabilityText.setText(
                                    "Could not check availability. Try again."
                            );

                            confirmButton.setEnabled(false);
                            return;
                        }

                        isAvailabilityKnown = true;
                        isSlotAvailable = response.body().available;

                        if (isSlotAvailable) {
                            availabilityText.setText(
                                    "✓ Available for selected time"
                            );
                            confirmButton.setEnabled(true);
                        } else {
                            availabilityText.setText(
                                    "✕ Already booked for this time. " +
                                            "Please select another time."
                            );
                            confirmButton.setEnabled(false);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<AvailabilityResponse> call,
                            Throwable t) {

                        if (checkId != availabilityCheckId) {
                            return;
                        }

                        if (call.isCanceled()) {
                            return;
                        }

                        isAvailabilityKnown = false;
                        isSlotAvailable = false;

                        availabilityText.setText(
                                "Could not check availability. Try again."
                        );

                        confirmButton.setEnabled(false);
                    }
                }
        );
    }


    private void openRazorpayCheckout(CreatePaymentResponse payment) {

        try {
            Checkout checkout = new Checkout();
            checkout.setKeyID(payment.keyId);

            JSONObject options = new JSONObject();

            options.put("name", "ParkNow");
            options.put("description", "Parking slot booking");
            options.put("order_id", payment.orderId);
            options.put("currency", payment.currency);
            options.put("amount", payment.amount);

            SessionManager sessionManager = new SessionManager(this);
            String email = sessionManager.getEmail();

            JSONObject prefill = new JSONObject();

            if (email != null && !email.isEmpty()) {
                prefill.put("email", email);
            }

            options.put("prefill", prefill);

            checkout.open(this, options);

        } catch (Exception e) {

            confirmButton.setEnabled(true);

            Log.e("PAYMENT_ERROR", "Could not open Razorpay", e);

            Ui.toast(this, "Unable to open payment screen.");
        }
    }


    private void createBooking(
            int slotId,
            int parkingLotId,
            double price
    ) {
        confirmButton.setEnabled(false);

        Calendar startCalendar = Calendar.getInstance();

        startCalendar.set(
                Calendar.HOUR_OF_DAY,
                startTimePicker.getHour()
        );

        startCalendar.set(
                Calendar.MINUTE,
                startTimePicker.getMinute()
        );

        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Date startDate = startCalendar.getTime();
        if (startDate.before(new Date())) {
            confirmButton.setEnabled(true);

            Ui.toast(
                    BookingActivity.this,
                    "Please select a future time."
            );

            return;
        }

        Calendar endCalendar = (Calendar) startCalendar.clone();

        endCalendar.add(Calendar.HOUR_OF_DAY, selectedDuration);

        Date endTime = endCalendar.getTime();


        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ssXXX",
                        Locale.US
                );

        String startTime = sdf.format(startDate);

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
                                            + formatPrice(price * selectedDuration)
                                            + " for " + selectedDuration
                                            + (selectedDuration == 1 ? " hour." : " hours.")
                            );

                            finish();

                            return;
                        }


                        String errorMessage = "Booking failed. Please try again.";

                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();

                                Log.e("BOOKING_ERROR", errorBody);

                                org.json.JSONObject json =
                                        new org.json.JSONObject(errorBody);

                                errorMessage = json.optString(
                                        "message",
                                        json.optString(
                                                "detail",
                                                "Booking failed. Please try again."
                                        )
                                );
                            }
                        } catch (Exception e) {
                            Log.e("BOOKING_ERROR", "Could not parse error", e);
                        }

                        Ui.toast(BookingActivity.this, errorMessage);

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
    @Override
    public void onPaymentSuccess(
            String razorpayPaymentID,
            PaymentData paymentData) {

        String orderId = paymentData.getOrderId();

        if (orderId == null || orderId.isEmpty()) {
            orderId = currentOrderId;
        }

        String paymentId = paymentData.getPaymentId();
        String signature = paymentData.getSignature();

        if (orderId == null || paymentId == null || signature == null) {
            Ui.toast(this, "Payment details are incomplete.");
            confirmButton.setEnabled(true);
            return;
        }

        VerifyPaymentRequest request = new VerifyPaymentRequest(
                orderId,
                paymentId,
                signature
        );

        ParkingApi api = ApiClient.spring().create(ParkingApi.class);

        api.verifyPayment(request).enqueue(
                new Callback<Booking>() {

                    @Override
                    public void onResponse(
                            Call<Booking> call,
                            Response<Booking> response) {

                        confirmButton.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            Ui.toast(
                                    BookingActivity.this,
                                    "Payment verified! Booking confirmed."
                            );
                            finish();
                        }
                        else {
                            String errorMessage = "No error details available";

                            try {
                                if (response.errorBody() != null) {
                                    errorMessage = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e("PAYMENT_VERIFY", "Could not read error", e);
                            }

                            Log.e("PAYMENT_VERIFY",
                                    "HTTP status: " + response.code()
                                            + ", Response: " + errorMessage);

                            Ui.toast(BookingActivity.this,
                                    "Verification failed: HTTP " + response.code());
                        }

                    }

                    @Override
                    public void onFailure(
                            Call<Booking> call,
                            Throwable t) {

                        confirmButton.setEnabled(true);

                        Log.e("PAYMENT_VERIFY", "Verification request failed", t);

                        Ui.toast(
                                BookingActivity.this,
                                "Could not verify payment."
                        );
                    }
                }
        );
    }

    @Override
    public void onPaymentError(
            int code,
            String description,
            PaymentData paymentData) {

        Ui.toast(this, "Payment failed: " + description);
    }
}