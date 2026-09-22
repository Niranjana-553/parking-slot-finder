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

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        TextView registerText = findViewById(R.id.registerText);

        loginButton.setOnClickListener(v -> login());
        registerText.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String e = email.getText().toString().trim();
        String p = password.getText().toString();

        if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p)) {
            Ui.toast(this, "Enter email and password.");
            return;
        }

        loginButton.setEnabled(false);

        AuthApi api = ApiClient.supabase().create(AuthApi.class);
        api.login("password", new LoginRequest(e, p)).enqueue(new Callback<AuthResponse>() {
            @Override public void onResponse(Call<AuthResponse> call, Response<AuthResponse> r) {
                loginButton.setEnabled(true);
                if (r.isSuccessful() && r.body() != null && r.body().accessToken != null
                        && r.body().user != null) {
                    new SessionManager(LoginActivity.this).saveSession(
                            r.body().accessToken, r.body().user.id, r.body().user.email);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Ui.toast(LoginActivity.this, "Login failed. Check your credentials.");
                }
            }
            @Override public void onFailure(Call<AuthResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                Ui.toast(LoginActivity.this, "Network error: " + t.getMessage());
            }
        });
    }
}
