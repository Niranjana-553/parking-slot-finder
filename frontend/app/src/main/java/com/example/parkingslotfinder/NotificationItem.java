package com.example.parkingslotfinder;

import com.google.gson.annotations.SerializedName;

public class NotificationItem {
    public String id;
    @SerializedName("user_id") public String userId;
    public String title;
    public String message;
    @SerializedName("is_read") public Boolean isRead;
    @SerializedName("created_at") public String createdAt;
}
