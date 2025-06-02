package com.baust.baustian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class AddCourseActivity extends AppCompatActivity {

    EditText courseNameEditText;
    Spinner levelSpinner, termSpinner, departmentSpinner;
    Button submitCourseBtn;

    String[] levels = {"1", "2", "3", "4"};
    String[] terms = {"I", "II"};
    String[] departments = {"CSE", "EEE", "ME", "CIVIL", "IPE", "ICT", "DBA", "AIS", "ENG"};

    DatabaseReference courseRef;
    String facultyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        courseNameEditText = findViewById(R.id.courseNameEditText);
        levelSpinner = findViewById(R.id.levelSpinner);
        termSpinner = findViewById(R.id.termSpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        submitCourseBtn = findViewById(R.id.submitCourseBtn);

        levelSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levels));
        termSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, terms));
        departmentSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments));

        facultyId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        courseRef = FirebaseDatabase.getInstance().getReference("courses");

        submitCourseBtn.setOnClickListener(v -> {
            String courseName = courseNameEditText.getText().toString().trim();
            String level = levelSpinner.getSelectedItem().toString();
            String term = termSpinner.getSelectedItem().toString();
            String department = departmentSpinner.getSelectedItem().toString();

            if (courseName.isEmpty()) {
                Toast.makeText(this, "Course name required", Toast.LENGTH_SHORT).show();
                return;
            }

            String courseId = courseRef.push().getKey();
            Course course = new Course(courseId, courseName, level, term, department, facultyId);

            courseRef.child(courseId).setValue(course)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, CourseSectionActivity.class);
                        intent.putExtra("courseId", courseId);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add course: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    public static class Course {
        public String courseId, courseName, level, term, department, facultyId;

        public Course() {}

        public Course(String courseId, String courseName, String level, String term, String department, String facultyId) {
            this.courseId = courseId;
            this.courseName = courseName;
            this.level = level;
            this.term = term;
            this.department = department;
            this.facultyId = facultyId;
        }
    }
}
