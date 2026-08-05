package com.example.airemoteproctoredexam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airemoteproctoredexam.network.ApiService;
import com.example.airemoteproctoredexam.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;

    private RadioGroup radioGroupRole;
    private RadioButton radioStudent;
    private RadioButton radioTeacher;

    private Button buttonRegister;
    private Button buttonBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

        radioGroupRole = findViewById(R.id.radioGroupRole);
        radioStudent = findViewById(R.id.radioStudent);
        radioTeacher = findViewById(R.id.radioTeacher);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);

        buttonRegister.setOnClickListener(v -> registerUser());

        buttonBackToLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void registerUser() {

        String name =
                editName.getText().toString().trim();

        String email =
                editEmail.getText().toString().trim();

        String password =
                editPassword.getText().toString().trim();

        if (name.isEmpty()
                || email.isEmpty()
                || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String role;

        int selectedRoleId =
                radioGroupRole.getCheckedRadioButtonId();

        if (selectedRoleId == R.id.radioTeacher) {
            role = "teacher";
        } else {
            role = "student";
        }

        JsonObject registerData =
                new JsonObject();

        registerData.addProperty(
                "name",
                name
        );

        registerData.addProperty(
                "email",
                email
        );

        registerData.addProperty(
                "password",
                password
        );

        registerData.addProperty(
                "role",
                role
        );

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        Call<JsonObject> call =
                apiService.register(
                        registerData
                );

        buttonRegister.setEnabled(false);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response
            ) {

                buttonRegister.setEnabled(true);

                if (response.isSuccessful()
                        && response.body() != null) {

                    JsonObject body =
                            response.body();

                    boolean success =
                            body.has("success")
                                    && body.get("success")
                                    .getAsBoolean();

                    if (success) {

                        Toast.makeText(
                                RegisterActivity.this,
                                "Registration Successful. Please Login.",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();

                    } else {

                        String message =
                                body.has("message")
                                        ? body.get("message")
                                        .getAsString()
                                        : "Registration failed";

                        Toast.makeText(
                                RegisterActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                } else {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Registration failed. Code: "
                                    + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<JsonObject> call,
                    Throwable t
            ) {

                buttonRegister.setEnabled(true);

                Toast.makeText(
                        RegisterActivity.this,
                        "Connection Error: "
                                + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}