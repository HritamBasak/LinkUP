package com.example.guardify;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class FeedbackActivity extends AppCompatActivity {

    private ImageView shownFeedback;
    private TextInputEditText feedbackInput;
    private Button buttonFeedback;

    private LinearLayout angryLayout, sadLayout, neutralLayout, happyLayout, excitedLayout;

    private int selectedEmotionResId = -1;
    private String selectedEmotionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Bind views
        shownFeedback = findViewById(R.id.shown_feedback);
        feedbackInput = findViewById(R.id.textfeedback);
        buttonFeedback = findViewById(R.id.buttonfeedback);

        angryLayout = findViewById(R.id.angry);
        sadLayout = findViewById(R.id.sad);
        neutralLayout = findViewById(R.id.neutral);
        happyLayout = findViewById(R.id.happy);
        excitedLayout = findViewById(R.id.excited);

        // Handle emoji clicks
        setEmojiClickListener(angryLayout, R.drawable.angry, "Angry");
        setEmojiClickListener(sadLayout, R.drawable.sad, "Sad");
        setEmojiClickListener(neutralLayout, R.drawable.neutral, "Neutral");
        setEmojiClickListener(happyLayout, R.drawable.happy, "Happy");
        setEmojiClickListener(excitedLayout, R.drawable.star, "Excited");

        // Submit feedback
        buttonFeedback.setOnClickListener(v -> {
            String feedbackText = feedbackInput.getText().toString().trim();
            if (selectedEmotionResId == -1) {
                Toast.makeText(this, "Please select an emoji to rate your experience", Toast.LENGTH_SHORT).show();
                return;
            }
            if (feedbackText.isEmpty()) {
                Toast.makeText(this, "Please enter your feedback message", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Send feedback to Firebase or local DB here
            Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_LONG).show();
            feedbackInput.setText("");
            shownFeedback.setVisibility(View.INVISIBLE);
            selectedEmotionResId = -1;
        });
    }

    private void setEmojiClickListener(LinearLayout layout, int imageResId, String emotionName) {
        layout.setOnClickListener(v -> {
            shownFeedback.setVisibility(View.VISIBLE);
            shownFeedback.setImageResource(imageResId);
            selectedEmotionResId = imageResId;
            selectedEmotionName = emotionName;
            highlightSelected(layout);
        });
    }

    private void highlightSelected(LinearLayout selectedLayout) {
        // Reset backgrounds
        angryLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        sadLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        neutralLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        happyLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        excitedLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        // Highlight selected
        selectedLayout.setBackgroundResource(R.drawable.selected_emoji_background); // You can create this drawable
    }
}
