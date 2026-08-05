package com.example.airemoteproctoredexam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airemoteproctoredexam.network.ApiService;
import com.example.airemoteproctoredexam.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddQuestionActivity extends AppCompatActivity {

    private TextView textAddQuestionTitle;

    private EditText editQuestion;
    private EditText editOptionA;
    private EditText editOptionB;
    private EditText editOptionC;
    private EditText editOptionD;
    private EditText editCorrectAnswer;
    private EditText editMarks;

    private Button buttonSaveQuestion;

    private int examId;
    private String examTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        textAddQuestionTitle = findViewById(R.id.textAddQuestionTitle);

        editQuestion = findViewById(R.id.editQuestion);
        editOptionA = findViewById(R.id.editOptionA);
        editOptionB = findViewById(R.id.editOptionB);
        editOptionC = findViewById(R.id.editOptionC);
        editOptionD = findViewById(R.id.editOptionD);
        editCorrectAnswer = findViewById(R.id.editCorrectAnswer);
        editMarks = findViewById(R.id.editMarks);

        buttonSaveQuestion = findViewById(R.id.buttonSaveQuestion);

        // Get selected exam information
        examId = getIntent().getIntExtra("exam_id", -1);
        examTitle = getIntent().getStringExtra("exam_title");

        if (examTitle != null) {
            textAddQuestionTitle.setText(
                    "Add Question\n" + examTitle
            );
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

        buttonSaveQuestion.setOnClickListener(v -> addQuestion());
    }

    private void addQuestion() {

        String question =
                editQuestion.getText().toString().trim();

        String optionA =
                editOptionA.getText().toString().trim();

        String optionB =
                editOptionB.getText().toString().trim();

        String optionC =
                editOptionC.getText().toString().trim();

        String optionD =
                editOptionD.getText().toString().trim();

        String correctAnswer =
                editCorrectAnswer.getText()
                        .toString()
                        .trim()
                        .toUpperCase();

        String marksText =
                editMarks.getText().toString().trim();

        // Validate empty fields
        if (question.isEmpty()
                || optionA.isEmpty()
                || optionB.isEmpty()
                || optionC.isEmpty()
                || optionD.isEmpty()
                || correctAnswer.isEmpty()
                || marksText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Correct answer must be A, B, C, or D
        if (!correctAnswer.equals("A")
                && !correctAnswer.equals("B")
                && !correctAnswer.equals("C")
                && !correctAnswer.equals("D")) {

            Toast.makeText(
                    this,
                    "Correct answer must be A, B, C or D",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int marks;

        try {

            marks = Integer.parseInt(marksText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Marks must be a number",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Get JWT saved during login
        SharedPreferences preferences =
                getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token =
                preferences.getString("token", null);

        if (token == null) {

            Toast.makeText(
                    this,
                    "Login session not found. Please login again.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Build JSON request
        JsonObject questionData = new JsonObject();

        questionData.addProperty("exam_id", examId);
        questionData.addProperty("question", question);

        questionData.addProperty("option_a", optionA);
        questionData.addProperty("option_b", optionB);
        questionData.addProperty("option_c", optionC);
        questionData.addProperty("option_d", optionD);

        questionData.addProperty(
                "correct_answer",
                correctAnswer
        );

        questionData.addProperty("marks", marks);

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        Call<JsonObject> call =
                apiService.addQuestion(
                        "Bearer " + token,
                        questionData
                );

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    JsonObject responseBody =
                            response.body();

                    boolean success =
                            responseBody.has("success")
                                    && responseBody
                                    .get("success")
                                    .getAsBoolean();

                    if (success) {

                        Toast.makeText(
                                AddQuestionActivity.this,
                                "Question Added Successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();

                    } else {

                        Toast.makeText(
                                AddQuestionActivity.this,
                                "Failed to add question",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                } else {

                    Toast.makeText(
                            AddQuestionActivity.this,
                            "Failed to add question. Code: "
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
                        AddQuestionActivity.this,
                        "Connection Error: "
                                + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}