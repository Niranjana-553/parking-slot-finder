
package com.example.parkingslotfinder;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    private LinearLayout list;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_notifications);

        list = findViewById(R.id.notificationList);
        load();
    }

    private void load() {
        String uid = new SessionManager(this).getUserId();

        ApiClient.spring().create(ParkingApi.class)
                .getNotifications(uid)
                .enqueue(new Callback<List<NotificationItem>>() {

                    @Override
                    public void onResponse(
                            Call<List<NotificationItem>> c,
                            Response<List<NotificationItem>> r) {

                        list.removeAllViews();

                        if (!r.isSuccessful() || r.body() == null) {
                            TextView error = Ui.text(
                                    NotificationsActivity.this,
                                    "Could not load notifications.",
                                    16
                            );
                            error.setTextColor(Color.WHITE);
                            list.addView(error);
                            return;
                        }

                        if (r.body().isEmpty()) {
                            TextView empty = Ui.text(
                                    NotificationsActivity.this,
                                    "You're all caught up!\nNo notifications yet.",
                                    16
                            );
                            empty.setTextColor(Color.WHITE);
                            empty.setPadding(8, 24, 8, 24);
                            list.addView(empty);
                            return;
                        }

                        for (NotificationItem n : r.body()) {

                            LinearLayout card = new LinearLayout(
                                    NotificationsActivity.this
                            );
                            card.setOrientation(LinearLayout.VERTICAL);
                            card.setPadding(20, 18, 20, 18);

                            GradientDrawable background = new GradientDrawable();
                            background.setColor(Color.rgb(25, 58, 90));
                            background.setCornerRadius(20);
                            card.setBackground(background);

                            LinearLayout.LayoutParams cardParams =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );

                            cardParams.setMargins(0, 0, 0, 16);
                            card.setLayoutParams(cardParams);

                            TextView title = new TextView(
                                    NotificationsActivity.this
                            );
                            title.setText(
                                    n.title != null ? n.title : "Notification"
                            );
                            title.setTextColor(Color.WHITE);
                            title.setTextSize(17);
                            title.setTypeface(null, Typeface.BOLD);

                            TextView message = new TextView(
                                    NotificationsActivity.this
                            );
                            message.setText(
                                    n.message != null ? n.message : ""
                            );
                            message.setTextColor(Color.rgb(208, 216, 224));
                            message.setTextSize(14);
                            message.setPadding(0, 8, 0, 0);

                            card.addView(title);
                            card.addView(message);

                            boolean unread = Boolean.FALSE.equals(n.isRead);

                            if (unread) {
                                View indicator = new View(
                                        NotificationsActivity.this
                                );

                                indicator.setBackgroundColor(
                                        Color.rgb(0, 255, 136)
                                );

                                LinearLayout.LayoutParams indicatorParams =
                                        new LinearLayout.LayoutParams(36, 4);

                                indicatorParams.setMargins(0, 0, 0, 12);
                                card.addView(indicator, 0, indicatorParams);

                                card.setOnClickListener(v -> {
                                    ApiClient.spring()
                                            .create(ParkingApi.class)
                                            .markNotificationRead(n.id)
                                            .enqueue(new Callback<NotificationItem>() {

                                                @Override
                                                public void onResponse(
                                                        Call<NotificationItem> c,
                                                        Response<NotificationItem> r) {

                                                    if (r.isSuccessful()) {
                                                        card.setAlpha(0.6f);
                                                        card.setOnClickListener(null);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(
                                                        Call<NotificationItem> c,
                                                        Throwable t) {
                                                    // Keep the notification unchanged.
                                                }
                                            });
                                });
                            }

                            list.addView(card);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<NotificationItem>> c,
                            Throwable t) {

                        Ui.toast(
                                NotificationsActivity.this,
                                "Backend error: " + t.getMessage()
                        );
                    }
                });
    }
}
