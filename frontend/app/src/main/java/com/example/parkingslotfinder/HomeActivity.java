package com.example.parkingslotfinder;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        list = findViewById(R.id.parkingList);



        findViewById(R.id.notificationButton).setOnClickListener(v ->
                startActivity(
                        new Intent(
                                HomeActivity.this,
                                NotificationsActivity.class
                        )
                )
        );



        findViewById(R.id.profileButton).setOnClickListener(v ->
                startActivity(
                        new Intent(
                                HomeActivity.this,
                                ProfileActivity.class
                        )
                )
        );



        findViewById(R.id.parkingButton).setOnClickListener(v ->
                startActivity(
                        new Intent(
                                HomeActivity.this,
                                MyBookingsActivity.class
                        )
                )
        );



        findViewById(R.id.changeLocation).setOnClickListener(v ->
                Ui.toast(
                        HomeActivity.this,
                        "Location selection coming soon"
                )
        );



        findViewById(R.id.searchText).setOnClickListener(v ->
                Ui.toast(
                        HomeActivity.this,
                        "Search coming soon"
                )
        );



        findViewById(R.id.filterButton).setOnClickListener(v ->
                Ui.toast(
                        HomeActivity.this,
                        "Filters coming soon"
                )
        );

        findViewById(R.id.sortButton).setOnClickListener(v ->
                Ui.toast(
                        HomeActivity.this,
                        "Parking is sorted by distance"
                )
        );

        loadParkingLots();
    }



    private void loadParkingLots() {

        ParkingApi api =
                ApiClient.spring().create(
                        ParkingApi.class
                );

        api.getParkingLots().enqueue(
                new Callback<List<ParkingLot>>() {

                    @Override
                    public void onResponse(
                            Call<List<ParkingLot>> call,
                            Response<List<ParkingLot>> response) {

                        list.removeAllViews();

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            TextView error =
                                    new TextView(
                                            HomeActivity.this
                                    );

                            error.setText(
                                    "Could not load parking lots ("
                                            + response.code()
                                            + ")."
                            );

                            error.setTextColor(
                                    0xFFFFFFFF
                            );

                            error.setTextSize(
                                    15
                            );

                            list.addView(error);

                            return;
                        }

                        List<ParkingLot> lots =
                                response.body();

                        if (lots.isEmpty()) {

                            TextView empty =
                                    new TextView(
                                            HomeActivity.this
                                    );

                            empty.setText(
                                    "No parking lots available."
                            );

                            empty.setTextColor(
                                    0xFFFFFFFF
                            );

                            empty.setTextSize(
                                    16
                            );

                            list.addView(empty);

                            return;
                        }

                        for (ParkingLot lot : lots) {

                            addParkingCard(lot);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<ParkingLot>> call,
                            Throwable t) {

                        list.removeAllViews();

                        TextView error =
                                new TextView(
                                        HomeActivity.this
                                );

                        error.setText(
                                "Unable to connect to backend."
                        );

                        error.setTextColor(
                                0xFFFFFFFF
                        );

                        error.setTextSize(
                                15
                        );

                        list.addView(error);

                        Ui.toast(
                                HomeActivity.this,
                                "Backend error: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }



    private void addParkingCard(ParkingLot lot) {



        LinearLayout card =
                new LinearLayout(
                        HomeActivity.this
                );

        card.setOrientation(
                LinearLayout.HORIZONTAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        int padding =
                dp(12);

        card.setPadding(
                padding,
                padding,
                dp(10),
                padding
        );



        GradientDrawable cardBackground =
                new GradientDrawable();

        cardBackground.setColor(
                0xFF193A5A
        );

        cardBackground.setCornerRadius(
                dp(18)
        );

        card.setBackground(
                cardBackground
        );



        ImageView carIcon =
                new ImageView(
                        HomeActivity.this
                );

        carIcon.setImageResource(
                R.drawable.car_icon
        );

        carIcon.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        carIcon.setPadding(
                dp(5),
                dp(5),
                dp(5),
                dp(5)
        );

        GradientDrawable iconBackground =
                new GradientDrawable();

        iconBackground.setColor(
                0xFF315474
        );

        iconBackground.setCornerRadius(
                dp(12)
        );

        carIcon.setBackground(
                iconBackground
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(60),
                        dp(60)
                );

        iconParams.gravity =
                Gravity.CENTER_VERTICAL;

        card.addView(
                carIcon,
                iconParams
        );



        LinearLayout info =
                new LinearLayout(
                        HomeActivity.this
                );

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        info.setGravity(
                Gravity.CENTER_VERTICAL
        );

        info.setPadding(
                dp(14),
                0,
                dp(5),
                0
        );



        TextView title =
                new TextView(
                        HomeActivity.this
                );

        title.setText(
                lot.name != null
                        ? lot.name
                        : "Parking lot"
        );

        title.setTextColor(
                0xFFFFFFFF
        );

        title.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                16
        );

        title.setSingleLine(
                true
        );

        title.setEllipsize(
                TextUtils.TruncateAt.END
        );

        title.setGravity(
                Gravity.CENTER_VERTICAL
        );



        TextView location =
                new TextView(
                        HomeActivity.this
                );

        location.setText(
                lot.displayLocation()
        );

        location.setTextColor(
                0xFFD0D8E0
        );

        location.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                13
        );

        location.setSingleLine(
                true
        );

        location.setEllipsize(
                TextUtils.TruncateAt.END
        );

        location.setPadding(
                0,
                dp(5),
                0,
                0
        );

        info.addView(
                title
        );

        info.addView(
                location
        );

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );

        card.addView(
                info,
                infoParams
        );



        LinearLayout available =
                new LinearLayout(
                        HomeActivity.this
                );

        available.setOrientation(
                LinearLayout.VERTICAL
        );

        available.setGravity(
                Gravity.CENTER
        );



        TextView availableText =
                new TextView(
                        HomeActivity.this
                );

        availableText.setText(
                "Available"
        );

        availableText.setTextColor(
                0xFFD0D8E0
        );

        availableText.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                11
        );

        availableText.setGravity(
                Gravity.CENTER
        );

        availableText.setSingleLine(
                true
        );



        TextView number =
                new TextView(
                        HomeActivity.this
                );

        // Start with loading symbol
        number.setText(
                "..."
        );

        number.setTextColor(
                0xFF00FF88
        );

        number.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                16
        );

        number.setGravity(
                Gravity.CENTER
        );

        number.setIncludeFontPadding(
                false
        );

        number.setPadding(
                0,
                0,
                0,
                0
        );



        GradientDrawable circle =
                new GradientDrawable();

        circle.setShape(
                GradientDrawable.OVAL
        );

        circle.setColor(
                0x00193A5A
        );

        circle.setStroke(
                dp(2),
                0xFF00FF88
        );

        number.setBackground(
                circle
        );



        LinearLayout.LayoutParams numberParams =
                new LinearLayout.LayoutParams(
                        dp(50),
                        dp(50)
                );

        numberParams.gravity =
                Gravity.CENTER_HORIZONTAL;

        numberParams.topMargin =
                dp(4);


        LinearLayout.LayoutParams availableTextParams =
                new LinearLayout.LayoutParams(
                        dp(80),
                        dp(20)
                );

        available.addView(
                availableText,
                availableTextParams
        );



        available.addView(
                number,
                numberParams
        );



        LinearLayout.LayoutParams availableParams =
                new LinearLayout.LayoutParams(
                        dp(90),
                        LinearLayout.LayoutParams.MATCH_PARENT
                );

        availableParams.gravity =
                Gravity.CENTER_VERTICAL;

        availableParams.setMargins(
                0,
                0,
                dp(4),
                0
        );

        card.addView(
                available,
                availableParams
        );



        card.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            SlotActivity.class
                    );

            intent.putExtra(
                    "parkingLotId",
                    lot.id
            );

            intent.putExtra(
                    "parkingLotName",
                    lot.name
            );

            startActivity(
                    intent
            );
        });



        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(110)
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(14)
        );

        list.addView(
                card,
                cardParams
        );


        loadLiveSlotCount(
                lot.id,
                number
        );
    }


    private void loadLiveSlotCount(
            Integer parkingLotId,
            TextView number
    ) {

        if (parkingLotId == null) {

            number.setText(
                    "0"
            );

            return;
        }

        ParkingApi api =
                ApiClient.spring().create(
                        ParkingApi.class
                );

        api.getAvailableSlots(
                String.valueOf(parkingLotId)
        ).enqueue(
                new Callback<List<ParkingSlot>>() {

                    @Override
                    public void onResponse(
                            Call<List<ParkingSlot>> call,
                            Response<List<ParkingSlot>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            int count =
                                    response.body().size();

                            number.setText(
                                    String.valueOf(count)
                            );

                        } else {

                            number.setText(
                                    "0"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<ParkingSlot>> call,
                            Throwable t) {

                        number.setText(
                                "0"
                        );
                    }
                }
        );
    }

    private int dp(int value) {

        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (list != null) {
            loadParkingLots();
        }
    }
}