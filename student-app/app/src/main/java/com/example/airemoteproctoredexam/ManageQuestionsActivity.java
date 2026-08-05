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
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.app.AlertDialog;
public class ManageQuestionsActivity extends AppCompatActivity {

    private TextView textExamTitle;
    private Button buttonAddQuestion;
    private LinearLayout questionContainer;

    private int examId;
    private String examTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_questions);

        textExamTitle = findViewById(R.id.textExamTitle);
        buttonAddQuestion = findViewById(R.id.buttonAddQuestion);
        questionContainer = findViewById(R.id.questionContainer);

        // Get selected exam
        examId = getIntent().getIntExtra("exam_id", -1);
        examTitle = getIntent().getStringExtra("exam_title");

        if (examTitle != null) {
            textExamTitle.setText("Questions for: " + examTitle);
        }

        if (examId == -1) {
            Toast.makeText(
                    this,
                    "Invalid exam",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Open Add Question screen
        buttonAddQuestion.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManageQuestionsActivity.this,
                    AddQuestionActivity.class
            );

            intent.putExtra("exam_id", examId);
            intent.putExtra("exam_title", examTitle);

            startActivity(intent);
        });

        // Load questions
        loadQuestions();
    }

    private void loadQuestions() {

        ApiService apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        Call<JsonObject> call =
                apiService.getQuestionsByExam(examId);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    JsonObject responseBody = response.body();

                    if (responseBody.has("questions")) {

                        JsonArray questions =
                                responseBody.getAsJsonArray("questions");

                        displayQuestions(questions);
                    }

                } else {

                    Toast.makeText(
                            ManageQuestionsActivity.this,
                            "Failed to load questions. Code: "
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
                        ManageQuestionsActivity.this,
                        "Connection Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void displayQuestions(JsonArray questions) {

        questionContainer.removeAllViews();

        if (questions.size() == 0) {

            TextView emptyText = new TextView(this);
            emptyText.setText("No questions added yet.");
            emptyText.setTextSize(18);

            questionContainer.addView(emptyText);

            return;
        }

        for (int i = 0; i < questions.size(); i++) {

            JsonObject question =
                    questions.get(i).getAsJsonObject();
            int questionId =
                    question.get("id").getAsInt();
            String questionText =
                    question.get("question").getAsString();

            String optionA =
                    question.get("option_a").getAsString();

            String optionB =
                    question.get("option_b").getAsString();

            String optionC =
                    question.get("option_c").getAsString();

            String optionD =
                    question.get("option_d").getAsString();

            String correctAnswer =
                    question.get("correct_answer").getAsString();

            int marks =
                    question.get("marks").getAsInt();

            TextView questionView = new TextView(this);

            questionView.setText(
                    (i + 1) + ". " + questionText
                            + "\n\nA. " + optionA
                            + "\nB. " + optionB
                            + "\nC. " + optionC
                            + "\nD. " + optionD
                            + "\n\nCorrect Answer: " + correctAnswer
                            + "\nMarks: " + marks
            );

            questionView.setTextSize(17);
            questionView.setPadding(20, 20, 20, 20);
            Button editButton = new Button(this);
            editButton.setText("Edit Question");

            editButton.setOnClickListener(v -> {

                Intent intent = new Intent(
                        ManageQuestionsActivity.this,
                        EditQuestionActivity.class
                );

                intent.putExtra("question_id", questionId);
                intent.putExtra("question", questionText);
                intent.putExtra("option_a", optionA);
                intent.putExtra("option_b", optionB);
                intent.putExtra("option_c", optionC);
                intent.putExtra("option_d", optionD);
                intent.putExtra("correct_answer", correctAnswer);
                intent.putExtra("marks", marks);

                startActivity(intent);
            });
// Create Delete button
            Button deleteButton = new Button(this);
            deleteButton.setText("Delete Question");

            deleteButton.setOnClickListener(v -> {

                new AlertDialog.Builder(
                        ManageQuestionsActivity.this
                )
                        .setTitle("Delete Question")
                        .setMessage(
                                "Are you sure you want to delete this question?"
                        )
                        .setPositiveButton(
                                "Delete",
                                (dialog, which) ->
                                        deleteQuestion(questionId)
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .show();
            });

// Container for one question
            LinearLayout questionItem =
                    new LinearLayout(this);

            questionItem.setOrientation(
                    LinearLayout.VERTICAL
            );

            questionItem.setPadding(
                    0,
                    0,
                    0,
                    40
            );

            questionItem.addView(questionView);
            questionItem.addView(editButton);
            questionItem.addView(deleteButton);

            questionContainer.addView(questionItem);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload after returning from Add Question
        if (examId != -1) {
            loadQuestions();
        }
    }
    private void deleteQuestion(int questionId) {

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
                apiService.deleteQuestion(
                        "Bearer " + token,
                        questionId
                );

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response
            ) {

                if (response.isSuccessful()) {

                    Toast.makeText(
                            ManageQuestionsActivity.this,
                            "Question Deleted Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Reload questions
                    loadQuestions();

                } else {

                    Toast.makeText(
                            ManageQuestionsActivity.this,
                            "Failed to delete question. Code: "
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
                        ManageQuestionsActivity.this,
                        "Connection Error: "
                                + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}