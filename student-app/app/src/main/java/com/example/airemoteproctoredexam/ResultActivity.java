package com.example.airemoteproctoredexam;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import android.content.Intent;
public class ResultActivity extends AppCompatActivity {

    private TextView textScore;
    private LinearLayout reviewContainer;
    private Button buttonFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textScore = findViewById(R.id.textScore);
        reviewContainer = findViewById(R.id.reviewContainer);
        buttonFinish = findViewById(R.id.buttonFinish);

        // Get score from TakeExamActivity
        int score = getIntent().getIntExtra(
                "score",
                0
        );

        int totalMarks = getIntent().getIntExtra(
                "total_marks",
                0
        );

        String reviewJson = getIntent().getStringExtra(
                "review"
        );

        // Display score
        textScore.setText(
                "Score: "
                        + score
                        + " / "
                        + totalMarks
        );

        // Display question review
        if (reviewJson != null && !reviewJson.isEmpty()) {

            try {

                JsonArray review =
                        JsonParser
                                .parseString(reviewJson)
                                .getAsJsonArray();

                displayReview(review);

            } catch (Exception e) {

                Toast.makeText(
                        this,
                        "Failed to load answer review",
                        Toast.LENGTH_LONG
                ).show();
            }
        }

        // Finish button
        buttonFinish.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultActivity.this,
                    StudentDashboardActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }


    private void displayReview(JsonArray review) {

        reviewContainer.removeAllViews();

        for (int i = 0; i < review.size(); i++) {

            JsonObject item =
                    review
                            .get(i)
                            .getAsJsonObject();


            // ----------------------------
            // GET QUESTION
            // ----------------------------

            String question =
                    item
                            .get("question")
                            .getAsString();


            // ----------------------------
            // GET STUDENT ANSWER
            // ----------------------------

            String selectedAnswer;

            if (item.get("selected_answer") == null
                    || item.get("selected_answer").isJsonNull()) {

                selectedAnswer = "Not Answered";

            } else {

                selectedAnswer =
                        item
                                .get("selected_answer")
                                .getAsString();
            }


            // ----------------------------
            // GET CORRECT ANSWER
            // ----------------------------

            String correctAnswer =
                    item
                            .get("correct_answer")
                            .getAsString();


            // ----------------------------
            // GET OPTIONS
            // ----------------------------

            String optionA =
                    item
                            .get("option_a")
                            .getAsString();

            String optionB =
                    item
                            .get("option_b")
                            .getAsString();

            String optionC =
                    item
                            .get("option_c")
                            .getAsString();

            String optionD =
                    item
                            .get("option_d")
                            .getAsString();


            // ----------------------------
            // GET CORRECT / INCORRECT
            // ----------------------------

            boolean isCorrect =
                    item
                            .get("is_correct")
                            .getAsBoolean();


            // ----------------------------
            // CONVERT STUDENT ANSWER
            // LETTER TO FULL OPTION
            // ----------------------------

            String selectedAnswerText;

            switch (selectedAnswer) {

                case "A":
                    selectedAnswerText =
                            "A. " + optionA;
                    break;

                case "B":
                    selectedAnswerText =
                            "B. " + optionB;
                    break;

                case "C":
                    selectedAnswerText =
                            "C. " + optionC;
                    break;

                case "D":
                    selectedAnswerText =
                            "D. " + optionD;
                    break;

                default:
                    selectedAnswerText =
                            "Not Answered";
                    break;
            }


            // ----------------------------
            // CONVERT CORRECT ANSWER
            // LETTER TO FULL OPTION
            // ----------------------------

            String correctAnswerText;

            switch (correctAnswer) {

                case "A":
                    correctAnswerText =
                            "A. " + optionA;
                    break;

                case "B":
                    correctAnswerText =
                            "B. " + optionB;
                    break;

                case "C":
                    correctAnswerText =
                            "C. " + optionC;
                    break;

                case "D":
                    correctAnswerText =
                            "D. " + optionD;
                    break;

                default:
                    correctAnswerText =
                            correctAnswer;
                    break;
            }


            // ----------------------------
            // STATUS
            // ----------------------------

            String status;

            if (isCorrect) {

                status = "Correct";

            } else {

                status = "Incorrect";
            }


            // ----------------------------
            // CREATE REVIEW TEXT
            // ----------------------------

            TextView questionReview =
                    new TextView(this);

            questionReview.setText(
                    "Question "
                            + (i + 1)
                            + ": "
                            + question

                            + "\n\nYour Answer: "
                            + selectedAnswerText

                            + "\nCorrect Answer: "
                            + correctAnswerText

                            + "\nStatus: "
                            + status
            );

            questionReview.setTextSize(18);

            questionReview.setPadding(
                    20,
                    20,
                    20,
                    40
            );

            reviewContainer.addView(
                    questionReview
            );
        }
    }
}