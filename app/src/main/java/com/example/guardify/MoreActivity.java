package com.example.guardify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MoreActivity extends AppCompatActivity {
    TextView aboutUs;
    TextView terms;
    TextView privacy;
    TextView share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_more);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        aboutUs = findViewById(R.id.aboutUs);
        terms = findViewById(R.id.terms);
        privacy = findViewById(R.id.privacy);
        share = findViewById(R.id.share);
        aboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });
        terms.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, TermsActivity.class);
            startActivity(intent);
        });
        privacy.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, PrivacyActivity.class);
            startActivity(intent);
        });
        share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMessage = "Check out this amazing app SkillBridge!\n\nDownload now: https://play.google.com/store/apps/details?id=" + getPackageName();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share SkillBridge via"));
        });

    }
}