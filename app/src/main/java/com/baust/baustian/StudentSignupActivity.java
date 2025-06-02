package com.baust.baustian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class StudentSignupActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText, studentIdEditText;
    Spinner departmentSpinner, levelSpinner, termSpinner;
    Button signupButton;

    FirebaseAuth mAuth;
    DatabaseReference databaseRef;

    String[] departments = {"CSE", "EEE", "ME", "CIVIL", "IPE", "ICT", "DBA", "AIS", "ENG"};
    String[] levels = {"1", "2", "3", "4"};
    String[] terms = {"I", "II"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_signup);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        studentIdEditText = findViewById(R.id.studentIdEditText);
        signupButton = findViewById(R.id.signupButton);

        departmentSpinner = findViewById(R.id.departmentSpinner);
        levelSpinner = findViewById(R.id.levelSpinner);
        termSpinner = findViewById(R.id.termSpinner);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("users/students");

        // Spinner data set
        departmentSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments));
        levelSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levels));
        termSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, terms));

        signupButton.setOnClickListener(view -> createStudentAccount());
    }

    private void createStudentAccount() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String studentId = studentIdEditText.getText().toString().trim();

        String department = departmentSpinner.getSelectedItem().toString();
        String level = levelSpinner.getSelectedItem().toString();
        String term = termSpinner.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || studentId.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if studentId already exists
        databaseRef.orderByChild("studentId").equalTo(studentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(StudentSignupActivity.this, "Student ID already used!", Toast.LENGTH_SHORT).show();
                        } else {
                            registerStudent(name, email, password, level, term, department, studentId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(StudentSignupActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerStudent(String name, String email, String password, String level, String term, String department, String studentId) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = mAuth.getCurrentUser().getUid();

                Student student = new Student(name, email, level, term, department, studentId);
                databaseRef.child(uid).setValue(student).addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        Toast.makeText(this, "Student registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Auth Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Student {
        public String name, email, level, term, department, studentId;

        public Student() {}

        public Student(String name, String email, String level, String term, String department, String studentId) {
            this.name = name;
            this.email = email;
            this.level = level;
            this.term = term;
            this.department = department;
            this.studentId = studentId;
        }
    }
}
