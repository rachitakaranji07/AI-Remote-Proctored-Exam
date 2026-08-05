package com.example.airemoteproctoredexam;

import android.content.Intent;
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
import android.app.AlertDialog;
import android.content.SharedPreferences;
public class ExamListActivity extends AppCompatActivity {

    private LinearLayout examContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);

        examContainer = findViewById(R.id.examContainer);

        loadExams();
    }

    private void loadExams() {

        ApiService apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        Call<JsonObject> call = apiService.getAllExams();

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    JsonObject responseBody = response.body();

                    if (responseBody.has("exams")) {

                        JsonArray exams =
                                responseBody.getAsJsonArray("exams");

                        displayExams(exams);
                    }

                } else {

                    Toast.makeText(
                            ExamListActivity.this,
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
                        ExamListActivity.this,
                        "Connection Error: "
                                + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void displayExams(JsonArray exams) {

        examContainer.removeAllViews();

        if (exams.size() == 0) {

            TextView emptyText = new TextView(this);

            emptyText.setText("No exams available.");
            emptyText.setTextSize(18);

            examContainer.addView(emptyText);

            return;
        }

        for (int i = 0; i < exams.size(); i++) {

            JsonObject exam =
                    exams.get(i).getAsJsonObject();

            int examId =
                    exam.get("id").getAsInt();

            String title =
                    exam.get("title").getAsString();

            String subject =
                    exam.get("subject").getAsString();

            int duration =
                    exam.get("duration").getAsInt();

            int totalMarks =
                    exam.get("total_marks").getAsInt();

            String startTime =
                    exam.get("start_time").getAsString();

            String endTime =
                    exam.get("end_time").getAsString();


            // -------------------------
            // EXAM INFORMATION
            // -------------------------

            TextView examView = new TextView(this);

            examView.setText(
                    title
                            + "\nSubject: " + subject
                            + "\nDuration: "
                            + duration + " minutes"
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


            // -------------------------
            // CLICK EXAM
            // MANAGE QUESTIONS
            // -------------------------

            Button manageQuestionsButton = new Button(this);
            manageQuestionsButton.setText("Manage Questions");

            manageQuestionsButton.setOnClickListener(v -> {

                Intent intent = new Intent(
                        ExamListActivity.this,
                        ManageQuestionsActivity.class
                );

                intent.putExtra("exam_id", examId);
                intent.putExtra("exam_title", title);

                startActivity(intent);
            });


            // -------------------------
            // EDIT EXAM BUTTON
            // -------------------------

            Button editButton =
                    new Button(this);

            editButton.setText(
                    "Edit Exam"
            );

            editButton.setOnClickListener(v -> {

                Intent intent = new Intent(
                        ExamListActivity.this,
                        EditExamActivity.class
                );

                intent.putExtra(
                        "exam_id",
                        examId
                );

                intent.putExtra(
                        "title",
                        title
                );

                intent.putExtra(
                        "subject",
                        subject
                );

                intent.putExtra(
                        "duration",
                        duration
                );

                intent.putExtra(
                        "total_marks",
                        totalMarks
                );

                intent.putExtra(
                        "start_time",
                        startTime
                );

                intent.putExtra(
                        "end_time",
                        endTime
                );

                startActivity(intent);
            });


            Button deleteButton = new Button(this);
            deleteButton.setText("Delete Exam");

            deleteButton.setOnClickListener(v -> {

                new AlertDialog.Builder(
                        ExamListActivity.this
                )
                        .setTitle("Delete Exam")
                        .setMessage(
                                "Are you sure you want to delete \""
                                        + title + "\"?"
                        )
                        .setPositiveButton(
                                "Delete",
                                (dialog, which) ->
                                        deleteExam(examId)
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .show();
            });
            // -------------------------
            // EXAM CONTAINER
            // -------------------------

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

            examItem.addView(examView);
            examItem.addView(manageQuestionsButton);
            examItem.addView(editButton);
            examItem.addView(deleteButton);
            examContainer.addView(
                    examItem
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh exams after editing
        if (examContainer != null) {
            loadExams();
        }
    }
    private void deleteExam(int examId) {

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

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        Call<JsonObject> call =
                apiService.deleteExam(
                        "Bearer " + token,
                        examId
                );

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response
            ) {

                if (response.isSuccessful()) {

                    Toast.makeText(
                            ExamListActivity.this,
                            "Exam Deleted Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Refresh exam list
                    loadExams();

                } else {

                    Toast.makeText(
                            ExamListActivity.this,
                            "Failed to delete exam. Code: "
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
                        ExamListActivity.this,
                        "Connection Error: "
                                + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}