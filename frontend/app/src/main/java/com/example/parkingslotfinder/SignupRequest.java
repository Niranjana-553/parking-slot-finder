package com.example.parkingslotfinder;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class SignupRequest {
    public String email;
    public String password;
    @SerializedName("data")
    public Map<String, String> data = new HashMap<>();

    public SignupRequest(String email, String password, String name, String phone) {
        this.email = email;
        this.password = password;
        if (name != null) data.put("full_name", name);
        if (phone != null) data.put("phone", phone);
    }
}
