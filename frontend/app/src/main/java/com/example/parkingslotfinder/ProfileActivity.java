
package com.example.parkingslotfinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_profile);

        SessionManager s = new SessionManager(this);

        String email = s.getEmail();

        TextView emailText = findViewById(R.id.emailText);
        emailText.setText(
                email != null && !email.isEmpty()
                        ? email
                        : "No email"
        );

        TextView nameText = findViewById(R.id.profileNameText);

        if (email != null && !email.isEmpty() && email.contains("@")) {
            nameText.setText(email.substring(0, email.indexOf("@")));
        }

        TextView phoneText = findViewById(R.id.phoneText);

        String phone = s.getPhone();

        phoneText.setText(
                phone != null && !phone.isEmpty()
                        ? phone
                        : "Phone number not available"
        );

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            s.clear();

            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(i);
        });
    }
}
