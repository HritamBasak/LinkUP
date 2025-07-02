package com.example.guardify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashSCreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser != null) {
                    FirebaseFirestore.getInstance().collection("users")
                            .document(firebaseUser.getUid())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // Proceed to Home
                                    startActivity(new Intent(SplashSCreen.this, HomeActivity.class));
                                } else {
                                    // User auth exists but user data is deleted
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(SplashSCreen.this, SignUpActivity.class));
                                }
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Fallback
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(SplashSCreen.this, SignUpActivity.class));
                                finish();
                            });
                } else {
                    startActivity(new Intent(SplashSCreen.this, SignUpActivity.class));
                    finish();
                }

            }
        },5000);
    }
}