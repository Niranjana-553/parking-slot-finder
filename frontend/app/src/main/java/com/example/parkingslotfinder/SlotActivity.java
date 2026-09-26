package com.example.parkingslotfinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlotActivity extends AppCompatActivity {

    private LinearLayout list;

    private int lotId;
    private String parkingLotName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slots);


        list = findViewById(R.id.slotList);

        TextView title = findViewById(R.id.title);


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

        if (lotId == -1) {

            Ui.toast(
                    SlotActivity.this,
                    "Invalid parking lot."
            );

            return;
        }


        loadSlots(lotId);
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

                        List<ParkingSlot> slots =
                                response.body();



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


                            TextView slotView =
                                    Ui.text(
                                            SlotActivity.this,
                                            slotInfo.toString(),
                                            17
                                    );

                            slotView.setTextColor(
                                    0xFF000000
                            );

                            slotView.setBackgroundColor(
                                    0xFFFFFFFF
                            );


                            slotView.setOnClickListener(
                                    x -> {

                                        Intent intent =
                                                new Intent(
                                                        SlotActivity.this,
                                                        BookingActivity.class
                                                );


                                        intent.putExtra(
                                                "slotId",
                                                slot.id
                                        );


                                        intent.putExtra(
                                                "parkingLotId",
                                                lotId
                                        );


                                        intent.putExtra(
                                                "parkingLotName",
                                                parkingLotName
                                        );


                                        intent.putExtra(
                                                "slotNumber",
                                                slot.slotNumber
                                        );

                                        if (slot.price != null) {

                                            intent.putExtra(
                                                    "price",
                                                    slot.price
                                            );
                                        }

                                        startActivity(intent);
                                    }
                            );

                            LinearLayout.LayoutParams params =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );

                            params.setMargins(
                                    0,
                                    0,
                                    0,
                                    15
                            );

                            list.addView(
                                    slotView,
                                    params
                            );
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
}