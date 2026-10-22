
package com.example.parkingslotfinder;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class AuthUser {
    public String id;
    public String email;

    @SerializedName("phone")
    public String phone;

    @SerializedName("user_metadata")
    public Map<String, Object> userMetadata;

    public String getPhoneNumber() {
        if (phone != null && !phone.isEmpty()) {
            return phone;
        }

        if (userMetadata != null && userMetadata.get("phone") != null) {
            return String.valueOf(userMetadata.get("phone"));
        }

        return null;
    }
}
