package com.example.airemoteproctoredexam;

import android.content.SharedPreferences;
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

public class CreateExamActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editSubject;
    private EditText editDuration;
    private EditText editTotalMarks;
    private EditText editStartTime;
    private EditText editEndTime;

    private Button buttonSaveExam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exam);

        editTitle = findViewById(R.id.editTitle);
        editSubject = findViewById(R.id.editSubject);
        editDuration = findViewById(R.id.editDuration);
        editTotalMarks = findViewById(R.id.editTotalMarks);
        editStartTime = findViewById(R.id.editStartTime);
        editEndTime = findViewById(R.id.editEndTime);

        buttonSaveExam = findViewById(R.id.buttonSaveExam);

        buttonSaveExam.setOnClickListener(v -> createExam());
    }

    private void createExam() {

        String title = editTitle.getText().toString().trim();
        String subject = editSubject.getText().toString().trim();
        String durationText = editDuration.getText().toString().trim();
        String totalMarksText = editTotalMarks.getText().toString().trim();
        String startTime = editStartTime.getText().toString().trim();
        String endTime = editEndTime.getText().toString().trim();

        // Check empty fields
        if (title.isEmpty() ||
                subject.isEmpty() ||
                durationText.isEmpty() ||
                totalMarksText.isEmpty() ||
                startTime.isEmpty() ||
                endTime.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int duration;
        int totalMarks;

        try {

            duration = Integer.parseInt(durationText);
            totalMarks = Integer.parseInt(totalMarksText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Duration and Total Marks must be numbers",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Get JWT token saved during login
        SharedPreferences preferences =
                getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = preferences.getString("token", null);

        if (token == null) {

            Toast.makeText(
                    this,
                    "Login session not found. Please login again.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create JSON request
        JsonObject examData = new JsonObject();

        examData.addProperty("title", title);
        examData.addProperty("subject", subject);
        examData.addProperty("duration", duration);
        examData.addProperty("total_marks", totalMarks);
        examData.addProperty("start_time", startTime);
        examData.addProperty("end_time", endTime);

        ApiService apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        Call<JsonObject> call = apiService.createExam(
                "Bearer " + token,
                examData
        );

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response
            ) {

                if (response.isSuccessful() &&
                        response.body() != null) {

                    JsonObject responseBody = response.body();

                    boolean success =
                            responseBody.has("success") &&
                                    responseBody.get("success").getAsBoolean();

                    if (success) {

                        Toast.makeText(
                                CreateExamActivity.this,
                                "Exam Created Successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();

                    } else {

                        Toast.makeText(
                                CreateExamActivity.this,
                                "Failed to create exam",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                } else {

                    Toast.makeText(
                            CreateExamActivity.this,
                            "Failed to create exam. Code: "
                                    + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<JsonObject> call,
                    Throwable t
            ) {

                Toast.makeText(
                        CreateExamActivity.this,
                        "Connection Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}