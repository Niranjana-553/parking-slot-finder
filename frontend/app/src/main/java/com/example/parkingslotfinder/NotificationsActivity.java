package com.example.parkingslotfinder;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class NotificationsActivity extends AppCompatActivity {
    private LinearLayout list;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_notifications);
        list = findViewById(R.id.notificationList);
        load();
    }
    private void load() {
        String uid = new SessionManager(this).getUserId();
        ApiClient.spring().create(ParkingApi.class).getNotifications(uid)
                .enqueue(new Callback<List<NotificationItem>>() {
            @Override public void onResponse(Call<List<NotificationItem>> c, Response<List<NotificationItem>> r) {
                list.removeAllViews();
                if (!r.isSuccessful() || r.body() == null) {
                    list.addView(Ui.text(NotificationsActivity.this, "Could not load notifications.", 16));
                    return;
                }
                for (NotificationItem n : r.body()) {
                    TextView item = Ui.text(NotificationsActivity.this,
                            (n.title != null ? n.title : "Notification") +
                            "\n" + (n.message != null ? n.message : ""), 16);
                    list.addView(item);
                    if (Boolean.FALSE.equals(n.isRead)) {
                        item.setOnClickListener(v -> {
                            ApiClient.spring().create(ParkingApi.class)
                                    .markNotificationRead(n.id)
                                    .enqueue(new Callback<NotificationItem>() {
                                @Override public void onResponse(Call<NotificationItem> c, Response<NotificationItem> r) {
                                    item.setAlpha(0.6f);
                                }
                                @Override public void onFailure(Call<NotificationItem> c, Throwable t) {}
                            });
                        });
                    }
                }
            }
            @Override public void onFailure(Call<List<NotificationItem>> c, Throwable t) {
                Ui.toast(NotificationsActivity.this, "Backend error: " + t.getMessage());
            }
        });
    }
}
