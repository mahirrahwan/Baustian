package com.baust.baustian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class AllCoursesActivity extends AppCompatActivity {

    LinearLayout courseListLayout;
    DatabaseReference courseRef;
    String currentFacultyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_courses);

        courseListLayout = findViewById(R.id.courseListLayout);
        courseRef = FirebaseDatabase.getInstance().getReference("courses");

        // 🔐 Get current logged in faculty UID
        currentFacultyId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String facultyId = snap.child("facultyId").getValue(String.class);

                    // 🔍 Filter by current faculty
                    if (facultyId != null && facultyId.equals(currentFacultyId)) {
                        String courseId = snap.child("courseId").getValue(String.class);
                        String name = snap.child("courseName").getValue(String.class);
                        String level = snap.child("level").getValue(String.class);
                        String term = snap.child("term").getValue(String.class);
                        String dept = snap.child("department").getValue(String.class);

                        Button courseButton = new Button(AllCoursesActivity.this);
                        courseButton.setText("📘 " + name + " | L" + level + " T" + term + " | " + dept);
                        courseButton.setOnClickListener(v -> {
                            Intent intent = new Intent(AllCoursesActivity.this, CourseSectionActivity.class);
                            intent.putExtra("courseId", courseId);
                            startActivity(intent);
                        });

                        courseListLayout.addView(courseButton);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AllCoursesActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
