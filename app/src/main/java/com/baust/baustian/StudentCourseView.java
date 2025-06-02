package com.baust.baustian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class StudentCourseView extends AppCompatActivity {

    LinearLayout courseContainer;
    DatabaseReference userRef, courseRef;
    String studentLevel, studentTerm, studentDept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_course_view);

        courseContainer = findViewById(R.id.courseContainer);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference("users/students").child(uid);
        courseRef = FirebaseDatabase.getInstance().getReference("courses");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                studentLevel = snapshot.child("level").getValue(String.class);
                studentTerm = snapshot.child("term").getValue(String.class);
                studentDept = snapshot.child("department").getValue(String.class);
                loadCourses();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void loadCourses() {
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                courseContainer.removeAllViews();

                for (DataSnapshot courseSnap : snapshot.getChildren()) {
                    String courseId = courseSnap.child("courseId").getValue(String.class);
                    String name = courseSnap.child("courseName").getValue(String.class);
                    String level = courseSnap.child("level").getValue(String.class);
                    String term = courseSnap.child("term").getValue(String.class);
                    String dept = courseSnap.child("department").getValue(String.class);

                    if (level.equals(studentLevel) && term.equals(studentTerm) && dept.equals(studentDept)) {
                        Button btn = new Button(StudentCourseView.this);
                        btn.setText(name);
                        btn.setOnClickListener(v -> openSection(courseId, name));
                        courseContainer.addView(btn);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void openSection(String courseId, String name) {
        Intent intent = new Intent(this, StudentSectionView.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("courseName", name);
        startActivity(intent);
    }
}
