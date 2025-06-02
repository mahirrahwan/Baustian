package com.baust.baustian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class FacultyDashboardActivity extends AppCompatActivity {

    Button addCourseButton, showCoursesButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);

        addCourseButton = findViewById(R.id.addCourseButton);
        showCoursesButton = findViewById(R.id.showCoursesButton);
        logoutButton = findViewById(R.id.logoutButton);

        addCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(FacultyDashboardActivity.this, AddCourseActivity.class);
            startActivity(intent);
        });

        showCoursesButton.setOnClickListener(v -> {
            Intent intent = new Intent(FacultyDashboardActivity.this, AllCoursesActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
