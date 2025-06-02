package com.baust.baustian;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class SectionUploadActivity extends AppCompatActivity {

    EditText textInput;
    Button uploadButton;
    TextView sectionLabel;

    String courseId, sectionType;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_upload);

        textInput = findViewById(R.id.textInput);
        uploadButton = findViewById(R.id.uploadButton);
        sectionLabel = findViewById(R.id.sectionLabel);

        courseId = getIntent().getStringExtra("courseId");
        sectionType = getIntent().getStringExtra("sectionType");

        sectionLabel.setText("Upload to: " + sectionType);
        dbRef = FirebaseDatabase.getInstance()
                .getReference("course_sections")
                .child(courseId)
                .child(sectionType);

        uploadButton.setOnClickListener(v -> {
            String content = textInput.getText().toString().trim();

            if (content.isEmpty()) {
                Toast.makeText(this, "Please enter text or link", Toast.LENGTH_SHORT).show();
                return;
            }

            dbRef.push().setValue(content)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, sectionType + " uploaded", Toast.LENGTH_SHORT).show();
                        textInput.setText("");
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
