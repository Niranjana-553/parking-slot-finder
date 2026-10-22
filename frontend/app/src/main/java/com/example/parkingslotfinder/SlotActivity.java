package com.example.parkingslotfinder;
import java.util.Comparator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.View;

public class SlotActivity extends AppCompatActivity {

    private LinearLayout list;
    private TextView ratingText;
    private android.widget.Button rateParkingButton;

    private int lotId;
    private String parkingLotName;
    private String parkingLotAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slots);


        list = findViewById(R.id.slotList);
        ratingText = findViewById(R.id.ratingText);
        rateParkingButton = findViewById(R.id.rateParkingButton);

        TextView title = findViewById(R.id.title);
        TextView addressText = findViewById(R.id.addressText);


        parkingLotName =
                getIntent().getStringExtra(
                        "parkingLotName"
                );

        lotId =
                getIntent().getIntExtra(
                        "parkingLotId",
                        -1
                );

        if (parkingLotName != null
                && !parkingLotName.isEmpty()) {

            title.setText(parkingLotName);

        } else {

            title.setText("Parking Slots");
        }

        parkingLotAddress =
                getIntent().getStringExtra("parkingLotAddress");

        if (parkingLotAddress != null
                && !parkingLotAddress.trim().isEmpty()) {
            addressText.setText(parkingLotAddress);
        } else {
            addressText.setVisibility(View.GONE);
        }

        if (lotId == -1) {

            Ui.toast(
                    SlotActivity.this,
                    "Invalid parking lot."
            );

            return;
        }
        loadRatingSummary();

        rateParkingButton.setOnClickListener(v -> showRatingDialog());
        loadSlots(lotId);

        ratingText.setOnClickListener(v -> showRatingDialog());
    }


    private void loadSlots(int lotId) {

        ParkingApi api =
                ApiClient.spring().create(
                        ParkingApi.class
                );

        api.getAvailableSlots(
                String.valueOf(lotId)
        ).enqueue(
                new Callback<List<ParkingSlot>>() {

                    @Override
                    public void onResponse(
                            Call<List<ParkingSlot>> call,
                            Response<List<ParkingSlot>> response) {

                        list.removeAllViews();


                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Ui.toast(
                                    SlotActivity.this,
                                    "Could not load slots ("
                                            + response.code()
                                            + ")."
                            );

                            return;
                        }

                        List<ParkingSlot> slots = response.body();

// Sort slots in natural ascending order: A1, A2, A3 ... A10
                        slots.sort((a, b) -> {
                            String first = a.slotNumber;
                            String second = b.slotNumber;

                            if (first == null) first = "";
                            if (second == null) second = "";

                            java.util.regex.Matcher m1 =
                                    java.util.regex.Pattern.compile("^(\\D*)(\\d+)$")
                                            .matcher(first.trim());

                            java.util.regex.Matcher m2 =
                                    java.util.regex.Pattern.compile("^(\\D*)(\\d+)$")
                                            .matcher(second.trim());

                            boolean match1 = m1.matches();
                            boolean match2 = m2.matches();

                            if (match1 && match2) {
                                int prefixCompare = m1.group(1).compareToIgnoreCase(m2.group(1));

                                if (prefixCompare != 0) {
                                    return prefixCompare;
                                }

                                int numberCompare = Integer.compare(
                                        Integer.parseInt(m1.group(2)),
                                        Integer.parseInt(m2.group(2))
                                );

                                if (numberCompare != 0) {
                                    return numberCompare;
                                }

                                return first.compareToIgnoreCase(second);
                            }

                            if (match1) return -1;
                            if (match2) return 1;

                            return first.compareToIgnoreCase(second);
                        });

                        if (slots.isEmpty()) {

                            TextView empty =
                                    Ui.text(
                                            SlotActivity.this,
                                            "No available slots.",
                                            16
                                    );

                            list.addView(empty);

                            return;
                        }


                        for (ParkingSlot slot : slots) {

                            String slotNumber =
                                    slot.slotNumber != null
                                            && !slot.slotNumber.trim().isEmpty()
                                            ? slot.slotNumber
                                            : "Not specified";

                            String status =
                                    slot.status != null
                                            ? slot.status
                                            : "AVAILABLE";


                            StringBuilder slotInfo =
                                    new StringBuilder();

                            slotInfo.append(
                                    "Slot "
                            );

                            slotInfo.append(
                                    slotNumber
                            );

                            slotInfo.append(
                                    "\nStatus: "
                            );

                            slotInfo.append(
                                    status
                            );


                            if (slot.price != null) {

                                slotInfo.append(
                                        "\nPrice: ₹"
                                );

                                slotInfo.append(
                                        formatPrice(slot.price)
                                );

                                slotInfo.append(
                                        "/hour"
                                );
                            }

                            LinearLayout slotCard = new LinearLayout(SlotActivity.this);
                            slotCard.setOrientation(LinearLayout.VERTICAL);
                            slotCard.setGravity(Gravity.CENTER_VERTICAL);
                            slotCard.setPadding(dp(16), dp(14), dp(16), dp(14));

// Card background matching the homepage
                            GradientDrawable cardBackground = new GradientDrawable();
                            cardBackground.setColor(0xFF193A5A);
                            cardBackground.setCornerRadius(dp(18));
                            slotCard.setBackground(cardBackground);

// Slot information
                            TextView slotView = Ui.text(
                                    SlotActivity.this,
                                    slotInfo.toString(),
                                    17
                            );
                            slotView.setTextColor(0xFFFFFFFF);
                            slotCard.addView(slotView);

// Preserve click-to-book functionality
                            slotCard.setOnClickListener(x -> {
                                Intent intent = new Intent(
                                        SlotActivity.this,
                                        BookingActivity.class
                                );

                                intent.putExtra("slotId", slot.id);
                                intent.putExtra("parkingLotId", lotId);
                                intent.putExtra("parkingLotName", parkingLotName);
                                intent.putExtra("slotNumber", slot.slotNumber);

                                if (slot.price != null) {
                                    intent.putExtra("price", slot.price);
                                }

                                startActivity(intent);
                            });

// Spacing between cards
                            LinearLayout.LayoutParams params =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );

                            params.setMargins(0, 0, 0, dp(12));

                            list.addView(slotCard, params);

                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<ParkingSlot>> call,
                            Throwable t) {

                        Ui.toast(
                                SlotActivity.this,
                                "Backend error: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }

    private void loadRatingSummary() {
        ParkingApi api = ApiClient.spring()
                .create(ParkingApi.class);

        api.getRatingSummary(lotId).enqueue(
                new Callback<RatingSummary>() {
                    @Override
                    public void onResponse(
                            Call<RatingSummary> call,
                            Response<RatingSummary> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            RatingSummary summary = response.body();

                            if (summary.ratingCount == 0) {
                                ratingText.setText(
                                        "No ratings yet"
                                );
                            } else {
                                ratingText.setText(
                                        String.format(
                                                java.util.Locale.US,
                                                "⭐ %.1f (%d reviews)",
                                                summary.averageRating,
                                                summary.ratingCount
                                        )
                                );
                            }
                        } else {
                            ratingText.setText("Rating unavailable");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<RatingSummary> call,
                            Throwable t) {
                        ratingText.setText("Rating unavailable");
                    }
                }
        );
    }

    private void showRatingDialog() {
        SessionManager session = new SessionManager(this);
        String userId = session.getUserId();

        if (userId == null || userId.isEmpty()) {
            Ui.toast(this, "Please log in to rate.");
            return;
        }

        String[] choices = {
                "⭐ 1 star",
                "⭐⭐ 2 stars",
                "⭐⭐⭐ 3 stars",
                "⭐⭐⭐⭐ 4 stars",
                "⭐⭐⭐⭐⭐ 5 stars"
        };

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Rate " + parkingLotName)
                .setSingleChoiceItems(choices, -1, (dialog, which) -> {
                    int rating = which + 1;
                    dialog.dismiss();

                    ParkingApi api = ApiClient.spring()
                            .create(ParkingApi.class);

                    api.submitRating(userId, lotId, rating).enqueue(
                            new Callback<java.util.Map<String, String>>() {
                                @Override
                                public void onResponse(
                                        Call<java.util.Map<String, String>> call,
                                        Response<java.util.Map<String, String>> response) {

                                    if (response.isSuccessful()) {
                                        Ui.toast(
                                                SlotActivity.this,
                                                "Rating submitted!"
                                        );
                                        loadRatingSummary();
                                    } else if (response.code() == 403) {
                                        Ui.toast(
                                                SlotActivity.this,
                                                "You need a confirmed booking to rate this parking lot."
                                        );
                                    } else {
                                        Ui.toast(
                                                SlotActivity.this,
                                                "Could not submit rating (" +
                                                        response.code() + ")."
                                        );
                                    }
                                }

                                @Override
                                public void onFailure(
                                        Call<java.util.Map<String, String>> call,
                                        Throwable t) {
                                    Ui.toast(
                                            SlotActivity.this,
                                            "Backend error: " + t.getMessage()
                                    );
                                }
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private String formatPrice(Double price) {

        if (price == null) {
            return "0";
        }

        if (price == price.longValue()) {

            return String.valueOf(
                    price.longValue()
            );
        }

        return String.format(
                java.util.Locale.US,
                "%.2f",
                price
        );
    }
    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }
}