package com.example.guardify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.signInButton).setOnClickListener(v -> loginWithEmail());
        findViewById(R.id.goToSignUpText).setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );
    }

    private void loginWithEmail() {
        EditText email = findViewById(R.id.emailEditText);
        EditText pass = findViewById(R.id.passwordEditText);

        String emailText = email.getText().toString();
        String passText = pass.getText().toString();

        if (emailText.isEmpty() || passText.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkIfProfileExists(user.getUid());
                    } else {
                        Toast.makeText(SignInActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Sign-In failed", task.getException());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void checkIfProfileExists(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Intent intent;
                    if (documentSnapshot.exists()) {
                        // Profile exists → go to Home
                        intent = new Intent(SignInActivity.this, HomeActivity.class);
                    } else {
                        // Profile missing → go to Setup
                        intent = new Intent(SignInActivity.this, ProfileSetupActivity.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignInActivity.this, "Failed to check profile", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Profile check failed", e);
                });
    }
}
