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

public class EditExamActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editSubject;
    private EditText editDuration;
    private EditText editTotalMarks;
    private EditText editStartTime;
    private EditText editEndTime;

    private Button buttonUpdateExam;

    private int examId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exam);

        editTitle = findViewById(R.id.editTitle);
        editSubject = findViewById(R.id.editSubject);
        editDuration = findViewById(R.id.editDuration);
        editTotalMarks = findViewById(R.id.editTotalMarks);
        editStartTime = findViewById(R.id.editStartTime);
        editEndTime = findViewById(R.id.editEndTime);

        buttonUpdateExam =
                findViewById(R.id.buttonUpdateExam);

        // Get existing exam information
        examId =
                getIntent().getIntExtra("exam_id", -1);

        String title =
                getIntent().getStringExtra("title");

        String subject =
                getIntent().getStringExtra("subject");

        int duration =
                getIntent().getIntExtra("duration", 0);

        int totalMarks =
                getIntent().getIntExtra("total_marks", 0);

        String startTime =
                getIntent().getStringExtra("start_time");

        String endTime =
                getIntent().getStringExtra("end_time");

        // Fill existing exam information
        editTitle.setText(title);
        editSubject.setText(subject);
        editDuration.setText(String.valueOf(duration));
        editTotalMarks.setText(String.valueOf(totalMarks));
        editStartTime.setText(startTime);
        editEndTime.setText(endTime);

        buttonUpdateExam.setOnClickListener(
                v -> updateExam()
        );
    }

    private void updateExam() {

        String title =
                editTitle.getText().toString().trim();

        String subject =
                editSubject.getText().toString().trim();

        String durationText =
                editDuration.getText().toString().trim();

        String totalMarksText =
                editTotalMarks.getText().toString().trim();

        String startTime =
                editStartTime.getText().toString().trim();

        String endTime =
                editEndTime.getText().toString().trim();

        if (title.isEmpty()
                || subject.isEmpty()
                || durationText.isEmpty()
                || totalMarksText.isEmpty()
                || startTime.isEmpty()
                || endTime.isEmpty()) {

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

            duration =
                    Integer.parseInt(durationText);

            totalMarks =
                    Integer.parseInt(totalMarksText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Duration and Total Marks must be numbers",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SharedPreferences preferences =
                getSharedPreferences(
                        "UserPrefs",
                        MODE_PRIVATE
                );

        String token =
                preferences.getString(
                        "token",
                        null
                );

        if (token == null) {

            Toast.makeText(
                    this,
                    "Login session not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        JsonObject examData =
                new JsonObject();

        examData.addProperty(
                "title",
                title
        );

        examData.addProperty(
                "subject",
                subject
        );

        examData.addProperty(
                "duration",
                duration
        );

        examData.addProperty(
                "total_marks",
                totalMarks
        );

        examData.addProperty(
                "start_time",
                startTime
        );

        examData.addProperty(
                "end_time",
                endTime
        );

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        Call<JsonObject> call =
                apiService.updateExam(
                        "Bearer " + token,
                        examId,
                        examData
                );

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response
            ) {

                if (response.isSuccessful()) {

                    Toast.makeText(
                            EditExamActivity.this,
                            "Exam Updated Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {

                    Toast.makeText(
                            EditExamActivity.this,
                            "Failed to update exam. Code: "
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

                Toast.makeText(
                        EditExamActivity.this,
                        "Connection Error: "
                                + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}