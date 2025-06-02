package com.baust.baustian;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class StudentDashboardActivity extends AppCompatActivity {

    LinearLayout courseListLayout;
    DatabaseReference userRef, courseRef;
    String uid, level, term, department;
    Button logoutButton;  // Logout button declare

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        courseListLayout = findViewById(R.id.courseListLayout);
        logoutButton = findViewById(R.id.logoutButton);  // find logout button in layout

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference("users/students").child(uid);
        courseRef = FirebaseDatabase.getInstance().getReference("courses");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                level = snapshot.child("level").getValue(String.class);
                term = snapshot.child("term").getValue(String.class);
                department = snapshot.child("department").getValue(String.class);

                loadCourses();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // Logout button click listener
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();  // Sign out from Firebase
            startActivity(new Intent(StudentDashboardActivity.this, LoginActivity.class));  // Go back to login
            finish();  // Close this activity
        });
    }

    private void loadCourses() {
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String courseId = snap.child("courseId").getValue(String.class);
                    String courseName = snap.child("courseName").getValue(String.class);
                    String cLevel = snap.child("level").getValue(String.class);
                    String cTerm = snap.child("term").getValue(String.class);
                    String cDept = snap.child("department").getValue(String.class);

                    if (level.equals(cLevel) && term.equals(cTerm) && department.equals(cDept)) {
                        addCourseView(courseId, courseName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void addCourseView(String courseId, String courseName) {
        TextView title = new TextView(this);
        title.setText("📘 " + courseName);
        title.setTextSize(18);
        title.setPadding(0, 24, 0, 8);

        TextView content = new TextView(this);
        content.setPadding(16, 0, 0, 16);
        content.setText("Loading content...");
        content.setMovementMethod(LinkMovementMethod.getInstance());

        courseListLayout.addView(title);
        courseListLayout.addView(content);

        String[] sections = {"CT", "Notice", "Slides", "Assignments"};

        StringBuilder allData = new StringBuilder();

        for (String section : sections) {
            DatabaseReference secRef = FirebaseDatabase.getInstance()
                    .getReference("course_sections")
                    .child(courseId)
                    .child(section);

            secRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    StringBuilder sectionText = new StringBuilder();
                    sectionText.append("🔹 <b>").append(section).append("</b><br>");

                    for (DataSnapshot child : snapshot.getChildren()) {
                        String text = child.getValue(String.class);
                        text = text.replaceAll("(?i)(https?://\\S+)", "<a href=\"$1\">$1</a>");
                        sectionText.append("• ").append(text).append("<br>");
                    }

                    if (!snapshot.hasChildren()) {
                        sectionText.append("• No ").append(section).append(" added yet.<br>");
                    }

                    allData.append(sectionText).append("<br>");
                    content.setText(Html.fromHtml(allData.toString()));
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    content.setText("Failed to load content.");
                }
            });
        }
    }
}
