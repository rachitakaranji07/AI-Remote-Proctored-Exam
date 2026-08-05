package com.example.airemoteproctoredexam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.airemoteproctoredexam.network.ApiService;
import com.example.airemoteproctoredexam.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
public class TakeExamActivity extends AppCompatActivity {

    private TextView textExamTitle;
    private TextView textTimer;
    private TextView textQuestionNumber;
    private TextView textQuestion;

    private RadioGroup radioGroupOptions;

    private RadioButton radioOptionA;
    private RadioButton radioOptionB;
    private RadioButton radioOptionC;
    private RadioButton radioOptionD;

    private Button buttonNext;

    private int examId;
    private int duration;

    private JsonArray questions;

    private int currentQuestionIndex = 0;

    // Stores:
    // question_id -> selected answer
    private final Map<Integer, String> answers =
            new HashMap<>();

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_exam);

        textExamTitle =
                findViewById(R.id.textExamTitle);

        textTimer =
                findViewById(R.id.textTimer);

        textQuestionNumber =
                findViewById(R.id.textQuestionNumber);

        textQuestion =
                findViewById(R.id.textQuestion);

        radioGroupOptions =
                findViewById(R.id.radioGroupOptions);

        radioOptionA =
                findViewById(R.id.radioOptionA);

        radioOptionB =
                findViewById(R.id.radioOptionB);

        radioOptionC =
                findViewById(R.id.radioOptionC);

        radioOptionD =
                findViewById(R.id.radioOptionD);

        buttonNext =
                findViewById(R.id.buttonNext);


        // Get exam information
        examId =
                getIntent().getIntExtra(
                        "exam_id",
                        -1
                );

        String examTitle =
                getIntent().getStringExtra(
                        "exam_title"
                );

        duration =
                getIntent().getIntExtra(
                        "duration",
                        0
                );


        if (examId == -1) {

            Toast.makeText(
                    this,
                    "Invalid exam",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        textExamTitle.setText(examTitle);


        // Start timer
        startTimer();


        // Load questions
        loadQuestions();


        buttonNext.setOnClickListener(v -> {

            saveCurrentAnswer();

            if (questions == null) {
                return;
            }

            if (currentQuestionIndex
                    < questions.size() - 1) {

                currentQuestionIndex++;

                displayCurrentQuestion();

            } else {

                confirmSubmitExam();
            }
        });
    }


    private void startTimer() {

        long durationMillis =
                duration * 60L * 1000L;

        countDownTimer =
                new CountDownTimer(
                        durationMillis,
                        1000
                ) {

                    @Override
                    public void onTick(
                            long millisUntilFinished
                    ) {

                        long totalSeconds =
                                millisUntilFinished
                                        / 1000;

                        long minutes =
                                totalSeconds / 60;

                        long seconds =
                                totalSeconds % 60;

                        String time =
                                String.format(
                                        Locale.getDefault(),
                                        "%02d:%02d",
                                        minutes,
                                        seconds
                                );

                        textTimer.setText(
                                "Time Remaining: "
                                        + time
                        );
                    }

                    @Override
                    public void onFinish() {

                        textTimer.setText(
                                "Time Remaining: 00:00"
                        );

                        Toast.makeText(
                                TakeExamActivity.this,
                                "Time is over. Exam will be submitted.",
                                Toast.LENGTH_LONG
                        ).show();

                        submitExam();
                    }
                };

        countDownTimer.start();
    }


    private void loadQuestions() {

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

            finish();

            return;
        }


        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);


        Call<JsonObject> call =
                apiService.getStudentQuestions(
                        "Bearer " + token,
                        examId
                );


        call.enqueue(
                new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            Call<JsonObject> call,
                            Response<JsonObject> response
                    ) {

                        if (response.isSuccessful()
                                && response.body()
                                != null) {

                            JsonObject body =
                                    response.body();

                            if (body.has("questions")) {

                                questions =
                                        body.getAsJsonArray(
                                                "questions"
                                        );

                                if (questions.size()
                                        == 0) {

                                    Toast.makeText(
                                            TakeExamActivity.this,
                                            "No questions available for this exam.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    finish();

                                    return;
                                }

                                displayCurrentQuestion();
                            }

                        } else {

                            Toast.makeText(
                                    TakeExamActivity.this,
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
                                TakeExamActivity.this,
                                "Connection Error: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    private void displayCurrentQuestion() {

        JsonObject question =
                questions
                        .get(
                                currentQuestionIndex
                        )
                        .getAsJsonObject();


        textQuestionNumber.setText(
                "Question "
                        + (currentQuestionIndex + 1)
                        + " of "
                        + questions.size()
        );


        textQuestion.setText(
                question
                        .get("question")
                        .getAsString()
        );


        radioOptionA.setText(
                question
                        .get("option_a")
                        .getAsString()
        );

        radioOptionB.setText(
                question
                        .get("option_b")
                        .getAsString()
        );

        radioOptionC.setText(
                question
                        .get("option_c")
                        .getAsString()
        );

        radioOptionD.setText(
                question
                        .get("option_d")
                        .getAsString()
        );


        radioGroupOptions.clearCheck();


        // Restore previously selected answer
        int questionId =
                question
                        .get("id")
                        .getAsInt();

        String savedAnswer =
                answers.get(questionId);

        if (savedAnswer != null) {

            switch (savedAnswer) {

                case "A":
                    radioOptionA.setChecked(true);
                    break;

                case "B":
                    radioOptionB.setChecked(true);
                    break;

                case "C":
                    radioOptionC.setChecked(true);
                    break;

                case "D":
                    radioOptionD.setChecked(true);
                    break;
            }
        }


        // Last question changes NEXT to SUBMIT
        if (currentQuestionIndex
                == questions.size() - 1) {

            buttonNext.setText(
                    "Submit Exam"
            );

        } else {

            buttonNext.setText(
                    "Next"
            );
        }
    }


    private void saveCurrentAnswer() {

        if (questions == null
                || questions.size() == 0) {

            return;
        }


        JsonObject question =
                questions
                        .get(
                                currentQuestionIndex
                        )
                        .getAsJsonObject();


        int questionId =
                question
                        .get("id")
                        .getAsInt();


        int selectedId =
                radioGroupOptions
                        .getCheckedRadioButtonId();


        String selectedAnswer = null;


        if (selectedId
                == R.id.radioOptionA) {

            selectedAnswer = "A";

        } else if (selectedId
                == R.id.radioOptionB) {

            selectedAnswer = "B";

        } else if (selectedId
                == R.id.radioOptionC) {

            selectedAnswer = "C";

        } else if (selectedId
                == R.id.radioOptionD) {

            selectedAnswer = "D";
        }


        if (selectedAnswer != null) {

            answers.put(
                    questionId,
                    selectedAnswer
            );
        }
    }


    private void confirmSubmitExam() {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Submit Exam"
                )

                .setMessage(
                        "Are you sure you want to submit your exam?"
                )

                .setPositiveButton(
                        "Submit",
                        (dialog, which) ->
                                submitExam()
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .show();
    }


    private void submitExam() {

        saveCurrentAnswer();

        if (countDownTimer != null) {
            countDownTimer.cancel();
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

        // Create submission JSON
        JsonObject submissionData =
                new JsonObject();

        submissionData.addProperty(
                "exam_id",
                examId
        );

        JsonArray answersArray =
                new JsonArray();

        // Convert saved answers to JSON
        for (Map.Entry<Integer, String> entry
                : answers.entrySet()) {

            JsonObject answerObject =
                    new JsonObject();

            answerObject.addProperty(
                    "question_id",
                    entry.getKey()
            );

            answerObject.addProperty(
                    "selected_answer",
                    entry.getValue()
            );

            answersArray.add(answerObject);
        }

        submissionData.add(
                "answers",
                answersArray
        );

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        Call<JsonObject> call =
                apiService.submitExam(
                        "Bearer " + token,
                        submissionData
                );

        buttonNext.setEnabled(false);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(
                    Call<JsonObject> call,
                    Response<JsonObject> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    JsonObject body = response.body();

                    if (!body.has("result")) {

                        buttonNext.setEnabled(true);

                        Toast.makeText(
                                TakeExamActivity.this,
                                "Invalid response from server",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    JsonObject result =
                            body.getAsJsonObject("result");

                    int score =
                            result.get("score").getAsInt();

                    int totalMarks =
                            result.get("total_marks").getAsInt();

                    JsonArray review =
                            result.getAsJsonArray("review");

                    Intent resultIntent = new Intent(
                            TakeExamActivity.this,
                            ResultActivity.class
                    );

                    resultIntent.putExtra(
                            "score",
                            score
                    );

                    resultIntent.putExtra(
                            "total_marks",
                            totalMarks
                    );

                    resultIntent.putExtra(
                            "review",
                            review.toString()
                    );

                    startActivity(resultIntent);

                    finish();

                } else {

                    buttonNext.setEnabled(true);

                    Toast.makeText(
                            TakeExamActivity.this,
                            "Failed to submit exam. Code: "
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

                buttonNext.setEnabled(true);

                Toast.makeText(
                        TakeExamActivity.this,
                        "Connection Error: "
                                + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}