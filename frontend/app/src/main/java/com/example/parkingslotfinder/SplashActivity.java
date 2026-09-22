package com.example.parkingslotfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = new SessionManager(this);
            Intent i = new Intent(this,
                    session.isLoggedIn() ? HomeActivity.class : LoginActivity.class);
            startActivity(i);
            finish();
        }, 1200);
    }
}
