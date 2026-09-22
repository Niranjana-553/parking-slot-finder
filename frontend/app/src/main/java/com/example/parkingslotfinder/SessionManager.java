package com.example.parkingslotfinder;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFS = "parknow_session";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String USER_ID = "user_id";
    private static final String EMAIL = "email";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String userId, String email) {
        prefs.edit()
                .putString(ACCESS_TOKEN, token)
                .putString(USER_ID, userId)
                .putString(EMAIL, email)
                .apply();
    }

    public String getAccessToken() { return prefs.getString(ACCESS_TOKEN, null); }
    public String getUserId() { return prefs.getString(USER_ID, null); }
    public String getEmail() { return prefs.getString(EMAIL, null); }
    public boolean isLoggedIn() {
        return getAccessToken() != null && !getAccessToken().isEmpty()
                && getUserId() != null && !getUserId().isEmpty();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
