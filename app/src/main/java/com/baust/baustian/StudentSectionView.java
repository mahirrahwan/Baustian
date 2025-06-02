package com.baust.baustian;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class StudentSectionView extends AppCompatActivity {

    LinearLayout sectionContainer;
    String courseId, courseName;

    DatabaseReference sectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_section_view);

        sectionContainer = findViewById(R.id.sectionContainer);
        courseId = getIntent().getStringExtra("courseId");
        courseName = getIntent().getStringExtra("courseName");

        getSupportActionBar().setTitle(courseName);

        loadSection("CT");
        loadSection("Notice");
        loadSection("Slides");
        loadSection("Assignments");
    }

    private void loadSection(String sectionName) {
        TextView header = new TextView(this);
        header.setText("📂 " + sectionName);
        header.setTextSize(18);
        header.setPadding(0, 20, 0, 8);
        sectionContainer.addView(header);

        sectionRef = FirebaseDatabase.getInstance().getReference("course_sections")
                .child(courseId).child(sectionName);

        sectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    TextView none = new TextView(StudentSectionView.this);
                    none.setText("No data available");
                    sectionContainer.addView(none);
                    return;
                }

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String content = snap.getValue(String.class);
                    TextView tv = new TextView(StudentSectionView.this);
                    tv.setText("- " + content);
                    tv.setPadding(10, 6, 10, 6);
                    sectionContainer.addView(tv);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
