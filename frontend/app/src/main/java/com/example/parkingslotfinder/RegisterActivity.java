package com.example.parkingslotfinder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText name, email, phone, password, confirmPassword;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> register());
        findViewById(R.id.loginText).setOnClickListener(v -> finish());
    }

    private void register() {
        String n = name.getText().toString().trim();
        String e = email.getText().toString().trim();
        String ph = phone.getText().toString().trim();
        String p = password.getText().toString();
        String cp = confirmPassword.getText().toString();

        if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p)) {
            Ui.toast(this, "Email and password are required.");
            return;
        }

        if (!ph.matches("\\d{10}")) {
            phone.setError("Enter a valid phone number");
            phone.requestFocus();
            return;
        }
        if (!p.equals(cp)) {
            Ui.toast(this, "Passwords do not match.");
            return;
        }

        registerButton.setEnabled(false);
        AuthApi api = ApiClient.supabase().create(AuthApi.class);
        api.signup(new SignupRequest(e, p, n, ph)).enqueue(new Callback<AuthResponse>() {
            @Override public void onResponse(Call<AuthResponse> call, Response<AuthResponse> r) {
                registerButton.setEnabled(true);
                if (r.isSuccessful()) {
                    if (r.body() != null && r.body().accessToken != null && r.body().user != null) {
                        SessionManager session =
                                new SessionManager(RegisterActivity.this);

                        session.saveSession(
                                r.body().accessToken,
                                r.body().user.id,
                                r.body().user.email
                        );

                        session.savePhone(ph);
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                    } else {
                        Ui.toast(RegisterActivity.this,
                                "Account created. Verify your email if required, then log in.");
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                    finish();
                } else {
                    String errorMsg = "Registration failed.";
                    try {
                        if (r.errorBody() != null) {
                            String rawError = r.errorBody().string();
                            if (r.code() == 429) {
                                errorMsg = "Rate limit reached. ";
                                if (rawError.contains("email")) {
                                    errorMsg += "Email limit exceeded.";
                                } else {
                                    errorMsg += "Please wait a few minutes.";
                                }
                            } else if (rawError.contains("weak_password")) {
                                errorMsg = "Password is too weak. Make sure it is at least 6 characters long.";
                            }  else if (rawError.contains("invalid_email")
                                || rawError.contains("invalid email")
                                || rawError.contains("email")) {

                            errorMsg = "Please enter a valid email address.";

                        } else {
                            errorMsg = "Registration failed. Please check your details and try again.";
                        }
                        } else {
                            errorMsg += " (Code: " + r.code() + ")";
                        }
                    } catch (Exception ignored) {}
                    Ui.toast(RegisterActivity.this, errorMsg);
                }
            }
            @Override public void onFailure(Call<AuthResponse> call, Throwable t) {
                registerButton.setEnabled(true);
                Ui.toast(RegisterActivity.this, "Network error: " + t.getMessage());
            }
        });
    }
}
