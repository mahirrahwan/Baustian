package com.baust.baustian;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class CourseSectionActivity extends AppCompatActivity {

    Button ctButton, noticeButton, slidesButton, assignmentButton;
    TextView ctInfo, noticeInfo, slidesInfo, assignmentInfo;
    String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_section);

        courseId = getIntent().getStringExtra("courseId");

        ctButton = findViewById(R.id.ctButton);
        noticeButton = findViewById(R.id.noticeButton);
        slidesButton = findViewById(R.id.slidesButton);
        assignmentButton = findViewById(R.id.assignmentButton);

        ctInfo = findViewById(R.id.ctInfo);
        noticeInfo = findViewById(R.id.noticeInfo);
        slidesInfo = findViewById(R.id.slidesInfo);
        assignmentInfo = findViewById(R.id.assignmentInfo);

        ctButton.setOnClickListener(v -> openSection("CT"));
        noticeButton.setOnClickListener(v -> openSection("Notice"));
        slidesButton.setOnClickListener(v -> openSection("Slides"));
        assignmentButton.setOnClickListener(v -> openSection("Assignments"));

        // Load data with hyperlink support
        loadSectionInfo("CT", ctInfo);
        loadSectionInfo("Notice", noticeInfo);
        loadSectionInfo("Slides", slidesInfo);
        loadSectionInfo("Assignments", assignmentInfo);
    }

    private void openSection(String sectionType) {
        Intent intent = new Intent(this, SectionUploadActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("sectionType", sectionType);
        startActivity(intent);
    }

    private void loadSectionInfo(String sectionType, TextView targetTextView) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("course_sections")
                .child(courseId)
                .child(sectionType);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                StringBuilder builder = new StringBuilder();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String text = child.getValue(String.class);
                    // Add bullet point and break line
                    builder.append("• ").append(text).append("<br>");
                }

                if (builder.length() == 0) {
                    targetTextView.setText("No " + sectionType + " added yet.");
                } else {
                    // Detect and hyperlink links
                    String htmlFormatted = builder.toString().replaceAll("(?i)(https?://\\S+)",
                            "<a href=\"$1\">$1</a>");
                    targetTextView.setText(Html.fromHtml(htmlFormatted));
                    targetTextView.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                targetTextView.setText("Failed to load " + sectionType);
            }
        });
    }
}
