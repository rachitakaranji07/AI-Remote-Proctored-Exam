package com.example.airemoteproctoredexam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airemoteproctoredexam.network.ApiService;
import com.example.airemoteproctoredexam.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;

    private Button buttonLogin;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(v -> loginUser());

        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Please enter email and password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject loginData = new JsonObject();
        loginData.addProperty("email", email);
        loginData.addProperty("password", password);

        ApiService apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        Call<JsonObject> call = apiService.login(loginData);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call,
                                   Response<JsonObject> response) {

                if (response.isSuccessful() && response.body() != null) {

                    JsonObject responseBody = response.body();

                    if (responseBody.has("access_token")
                            && responseBody.has("user")) {

                        String token = responseBody
                                .get("access_token")
                                .getAsString();

                        JsonObject user = responseBody
                                .getAsJsonObject("user");

                        String role = user
                                .get("role")
                                .getAsString();

                        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("token", token)
                                .putString("role", role)
                                .apply();

                        Toast.makeText(LoginActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show();

                        Intent intent;

                        if (role.equalsIgnoreCase("student")) {

                            intent = new Intent(
                                    LoginActivity.this,
                                    StudentDashboardActivity.class);

                        } else if (role.equalsIgnoreCase("teacher")) {

                            intent = new Intent(
                                    LoginActivity.this,
                                    TeacherDashboardActivity.class);

                        } else if (role.equalsIgnoreCase("admin")) {

                            intent = new Intent(
                                    LoginActivity.this,
                                    AdminDashboardActivity.class);

                        } else {

                            Toast.makeText(LoginActivity.this,
                                    "Unknown user role",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(LoginActivity.this,
                                "Invalid login response",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(LoginActivity.this,
                            "Login failed. Code: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call,
                                  Throwable t) {

                Toast.makeText(LoginActivity.this,
                        "Connection Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}