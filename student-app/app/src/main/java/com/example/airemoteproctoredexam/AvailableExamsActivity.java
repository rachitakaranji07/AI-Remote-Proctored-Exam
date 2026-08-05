package com.example.airemoteproctoredexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airemoteproctoredexam.network.ApiService;
import com.example.airemoteproctoredexam.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailableExamsActivity extends AppCompatActivity {

    private LinearLayout examContainer;
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_exams);

        examContainer =
                findViewById(R.id.examContainer);

        apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        SharedPreferences preferences =
                getSharedPreferences(
                        "UserPrefs",
                        MODE_PRIVATE
                );

        token =
                preferences.getString(
                        "token",
                        null
                );

        if (token == null) {

            Toast.makeText(
                    this,
                    "Login session not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        loadExams();
    }


    // =====================================================
    // LOAD ALL EXAMS
    // =====================================================

    private void loadExams() {

        apiService
                .getAllExams()
                .enqueue(
                        new Callback<JsonObject>() {

                            @Override
                            public void onResponse(
                                    Call<JsonObject> call,
                                    Response<JsonObject> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    JsonObject body =
                                            response.body();

                                    if (body.has("exams")) {

                                        JsonArray exams =
                                                body.getAsJsonArray(
                                                        "exams"
                                                );

                                        displayExams(exams);

                                    } else {

                                        Toast.makeText(
                                                AvailableExamsActivity.this,
                                                "No exam data received",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }

                                } else {

                                    Toast.makeText(
                                            AvailableExamsActivity.this,
                                            "Failed to load exams. Code: "
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
                                        AvailableExamsActivity.this,
                                        "Connection Error: "
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =====================================================
    // DISPLAY EXAMS
    // =====================================================

    private void displayExams(JsonArray exams) {

        examContainer.removeAllViews();


        if (exams.size() == 0) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "No exams available."
            );

            emptyText.setTextSize(18);

            examContainer.addView(
                    emptyText
            );

            return;
        }


        for (int i = 0;
             i < exams.size();
             i++) {


            JsonObject exam =
                    exams
                            .get(i)
                            .getAsJsonObject();


            int examId =
                    exam
                            .get("id")
                            .getAsInt();


            String title =
                    exam
                            .get("title")
                            .getAsString();


            String subject =
                    exam
                            .get("subject")
                            .getAsString();


            int duration =
                    exam
                            .get("duration")
                            .getAsInt();


            int totalMarks =
                    exam
                            .get("total_marks")
                            .getAsInt();


            // ---------------------------------------------
            // EXAM INFORMATION
            // ---------------------------------------------

            TextView examView =
                    new TextView(this);


            examView.setText(
                    title
                            + "\nSubject: "
                            + subject

                            + "\nDuration: "
                            + duration
                            + " minutes"

                            + "\nTotal Marks: "
                            + totalMarks
            );


            examView.setTextSize(18);


            examView.setPadding(
                    20,
                    20,
                    20,
                    20
            );


            // ---------------------------------------------
            // EXAM BUTTON
            // ---------------------------------------------

            Button examButton =
                    new Button(this);


            // Initially show checking
            examButton.setText(
                    "Checking..."
            );

            examButton.setEnabled(
                    false
            );


            // ---------------------------------------------
            // EXAM ITEM CONTAINER
            // ---------------------------------------------

            LinearLayout examItem =
                    new LinearLayout(this);


            examItem.setOrientation(
                    LinearLayout.VERTICAL
            );


            examItem.setPadding(
                    0,
                    0,
                    0,
                    40
            );


            examItem.addView(
                    examView
            );


            examItem.addView(
                    examButton
            );


            examContainer.addView(
                    examItem
            );


            // ---------------------------------------------
            // CHECK IF STUDENT ALREADY SUBMITTED
            // ---------------------------------------------

            checkSubmissionStatus(
                    examId,
                    title,
                    duration,
                    totalMarks,
                    examButton
            );
        }
    }


    // =====================================================
    // CHECK SUBMISSION STATUS
    // =====================================================

    private void checkSubmissionStatus(
            int examId,
            String title,
            int duration,
            int totalMarks,
            Button examButton
    ) {


        apiService
                .getExamResult(
                        "Bearer " + token,
                        examId
                )
                .enqueue(
                        new Callback<JsonObject>() {

                            @Override
                            public void onResponse(
                                    Call<JsonObject> call,
                                    Response<JsonObject> response
                            ) {


                                if (response.isSuccessful()
                                        && response.body() != null) {


                                    JsonObject body =
                                            response.body();


                                    boolean submitted =
                                            body.has("submitted")
                                                    && body
                                                    .get("submitted")
                                                    .getAsBoolean();


                                    // ---------------------------------
                                    // ALREADY SUBMITTED
                                    // ---------------------------------

                                    if (submitted) {


                                        examButton.setText(
                                                "View Result"
                                        );


                                        examButton.setEnabled(
                                                true
                                        );


                                        examButton.setOnClickListener(
                                                v -> {

                                                    JsonObject result =
                                                            body.getAsJsonObject(
                                                                    "result"
                                                            );


                                                    int score =
                                                            result
                                                                    .get("score")
                                                                    .getAsInt();


                                                    int resultTotalMarks =
                                                            result
                                                                    .get("total_marks")
                                                                    .getAsInt();


                                                    JsonArray review =
                                                            result
                                                                    .getAsJsonArray(
                                                                            "review"
                                                                    );


                                                    Intent intent =
                                                            new Intent(
                                                                    AvailableExamsActivity.this,
                                                                    ResultActivity.class
                                                            );


                                                    intent.putExtra(
                                                            "score",
                                                            score
                                                    );


                                                    intent.putExtra(
                                                            "total_marks",
                                                            resultTotalMarks
                                                    );


                                                    intent.putExtra(
                                                            "review",
                                                            review.toString()
                                                    );


                                                    startActivity(
                                                            intent
                                                    );
                                                }
                                        );


                                    } else {


                                        // ---------------------------------
                                        // NOT SUBMITTED
                                        // ---------------------------------

                                        examButton.setText(
                                                "Start Exam"
                                        );


                                        examButton.setEnabled(
                                                true
                                        );


                                        examButton.setOnClickListener(
                                                v -> {


                                                    Intent intent =
                                                            new Intent(
                                                                    AvailableExamsActivity.this,
                                                                    TakeExamActivity.class
                                                            );


                                                    intent.putExtra(
                                                            "exam_id",
                                                            examId
                                                    );


                                                    intent.putExtra(
                                                            "exam_title",
                                                            title
                                                    );


                                                    intent.putExtra(
                                                            "duration",
                                                            duration
                                                    );


                                                    intent.putExtra(
                                                            "total_marks",
                                                            totalMarks
                                                    );


                                                    startActivity(
                                                            intent
                                                    );
                                                }
                                        );
                                    }


                                } else {


                                    examButton.setText(
                                            "Unable to Check Status"
                                    );


                                    examButton.setEnabled(
                                            false
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<JsonObject> call,
                                    Throwable t
                            ) {


                                examButton.setText(
                                        "Connection Error"
                                );


                                examButton.setEnabled(
                                        false
                                );
                            }
                        }
                );
    }
}