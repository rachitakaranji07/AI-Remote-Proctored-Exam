package com.example.airemoteproctoredexam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherDashboardActivity extends AppCompatActivity {

    private Button buttonCreateExam,buttonViewExams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        buttonCreateExam = findViewById(R.id.buttonCreateExam);
        buttonViewExams = findViewById(R.id.buttonViewExams);
        buttonCreateExam.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TeacherDashboardActivity.this,
                    CreateExamActivity.class
            );

            startActivity(intent);
        });
        buttonViewExams.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TeacherDashboardActivity.this,
                    ExamListActivity.class
            );

            startActivity(intent);
        });
    }
}