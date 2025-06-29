package com.example.guardify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class ProfileSetupActivity extends AppCompatActivity {

    EditText etName, etBio, etSkills;
    Button btnSave;
    FirebaseFirestore db;
    FirebaseAuth auth;

    boolean isEditMode = false;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        etName = findViewById(R.id.etName);
        etBio = findViewById(R.id.etBio);
        etSkills = findViewById(R.id.etSkills);
        btnSave = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        // 👁️ Check if we're in edit mode
        isEditMode = getIntent().getBooleanExtra("editMode", false);

        if (isEditMode) {
            loadUserDataForEdit();
        }

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserDataForEdit() {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String bio = documentSnapshot.getString("bio");
                        List<String> skills = (List<String>) documentSnapshot.get("skills");

                        etName.setText(name != null ? name : "");
                        etBio.setText(bio != null ? bio : "");
                        etSkills.setText(skills != null ? String.join(",", skills) : "");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile for editing", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        String skillInput = etSkills.getText().toString().trim();

        if (name.isEmpty() || skillInput.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String defaultProfileUrl = ""; // No image for now

        User user = new User(
                uid,
                name,
                bio,
                Arrays.asList(skillInput.split(",")),
                defaultProfileUrl
        );

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileSetupActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
