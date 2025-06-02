package com.baust.baustian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SignupTypeActivity extends AppCompatActivity {

    Button studentButton, facultyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_type);

        studentButton = findViewById(R.id.studentButton);
        facultyButton = findViewById(R.id.facultyButton);

        studentButton.setOnClickListener(v -> {
            startActivity(new Intent(SignupTypeActivity.this, StudentSignupActivity.class));
        });

        facultyButton.setOnClickListener(v -> {
            startActivity(new Intent(SignupTypeActivity.this, FacultySignupActivity.class));
        });
    }
}
