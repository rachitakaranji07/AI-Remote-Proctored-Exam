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

public class EditQuestionActivity extends AppCompatActivity {

    private EditText editQuestion;
    private EditText editOptionA;
    private EditText editOptionB;
    private EditText editOptionC;
    private EditText editOptionD;
    private EditText editCorrectAnswer;
    private EditText editMarks;

    private Button buttonUpdateQuestion;

    private int questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        editQuestion = findViewById(R.id.editQuestion);
        editOptionA = findViewById(R.id.editOptionA);
        editOptionB = findViewById(R.id.editOptionB);
        editOptionC = findViewById(R.id.editOptionC);
        editOptionD = findViewById(R.id.editOptionD);
        editCorrectAnswer = findViewById(R.id.editCorrectAnswer);
        editMarks = findViewById(R.id.editMarks);

        buttonUpdateQuestion =
                findViewById(R.id.buttonUpdateQuestion);

        // Get existing question data
        questionId =
                getIntent().getIntExtra("question_id", -1);

        String question =
                getIntent().getStringExtra("question");

        String optionA =
                getIntent().getStringExtra("option_a");

        String optionB =
                getIntent().getStringExtra("option_b");

        String optionC =
                getIntent().getStringExtra("option_c");

        String optionD =
                getIntent().getStringExtra("option_d");

        String correctAnswer =
                getIntent().getStringExtra("correct_answer");

        int marks =
                getIntent().getIntExtra("marks", 0);

        // Fill existing values
        editQuestion.setText(question);
        editOptionA.setText(optionA);
        editOptionB.setText(optionB);
        editOptionC.setText(optionC);
        editOptionD.setText(optionD);
        editCorrectAnswer.setText(correctAnswer);
        editMarks.setText(String.valueOf(marks));

        buttonUpdateQuestion.setOnClickListener(
                v -> updateQuestion()
        );
    }

    private void updateQuestion() {

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

        SharedPreferences preferences =
                getSharedPreferences(
                        "UserPrefs",
                        MODE_PRIVATE
                );

        String token =
                preferences.getString("token", null);

        if (token == null) {

            Toast.makeText(
                    this,
                    "Login session not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        JsonObject questionData = new JsonObject();

        questionData.addProperty(
                "question",
                question
        );

        questionData.addProperty(
                "option_a",
                optionA
        );

        questionData.addProperty(
                "option_b",
                optionB
        );

        questionData.addProperty(
                "option_c",
                optionC
        );

        questionData.addProperty(
                "option_d",
                optionD
        );

        questionData.addProperty(
                "correct_answer",
                correctAnswer
        );

        questionData.addProperty(
                "marks",
                marks
        );

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        Call<JsonObject> call =
                apiService.updateQuestion(
                        "Bearer " + token,
                        questionId,
                        questionData
                );

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response
            ) {

                if (response.isSuccessful()) {

                    Toast.makeText(
                            EditQuestionActivity.this,
                            "Question Updated Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {

                    Toast.makeText(
                            EditQuestionActivity.this,
                            "Failed to update question. Code: "
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
                        EditQuestionActivity.this,
                        "Connection Error: "
                                + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}