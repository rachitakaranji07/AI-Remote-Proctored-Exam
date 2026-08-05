package com.example.airemoteproctoredexam;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
public class StudentDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        Button buttonViewAvailableExams =
                findViewById(R.id.buttonViewAvailableExams);

        buttonViewAvailableExams.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StudentDashboardActivity.this,
                    AvailableExamsActivity.class
            );

            startActivity(intent);
        });
    }
}