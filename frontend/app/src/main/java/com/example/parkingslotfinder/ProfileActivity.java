package com.example.parkingslotfinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_profile);

        SessionManager s = new SessionManager(this);
        ((TextView)findViewById(R.id.emailText)).setText(
                s.getEmail() != null ? s.getEmail() : "No email");
        ((TextView)findViewById(R.id.userIdText)).setText(
                "User ID: " + (s.getUserId() != null ? s.getUserId() : "-"));

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            s.clear();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}
