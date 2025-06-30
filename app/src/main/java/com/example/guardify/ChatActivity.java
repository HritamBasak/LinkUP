package com.example.guardify;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guardify.ui.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolbar;
    private RecyclerView recyclerMessages;
    private EditText etMessage;
    private ImageButton btnSend;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String teamId;
    private String currentUserId;

    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        // Get team ID from intent
        teamId = getIntent().getStringExtra("teamId");
        if (teamId == null) {
            Toast.makeText(this, "Team not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind views
        chatToolbar = findViewById(R.id.chatToolbar);
        recyclerMessages = findViewById(R.id.recyclerMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Toolbar setup
        setSupportActionBar(chatToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Team Chat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(this, messageList, currentUserId);
        recyclerMessages.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());

        listenForMessages();
        markMessagesAsRead();
    }

    private void sendMessage() {
        String msg = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) return;

        // Fetch user's name from 'users' collection using UID
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String senderName = documentSnapshot.getString("name");
                    if (senderName == null) senderName = "You";  // fallback

                    DocumentReference newMsgRef = db.collection("teams")
                            .document(teamId)
                            .collection("messages")
                            .document();

                    Message message = new Message(currentUserId, senderName, msg, null, "sent");

                    newMsgRef.set(message)
                            .addOnSuccessListener(unused -> newMsgRef.update("timestamp", FieldValue.serverTimestamp()))
                            .addOnSuccessListener(unused -> etMessage.setText(""));
                });
    }




    private void listenForMessages() {
        db.collection("teams")
                .document(teamId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    messageList.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Message message = doc.toObject(Message.class);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    recyclerMessages.post(() -> recyclerMessages.scrollToPosition(messageList.size() - 1));

                });
    }

    private void markMessagesAsRead() {
        db.collection("teams")
                .document(teamId)
                .collection("messages")
                .whereEqualTo("status", "sent")
                .whereNotEqualTo("senderId", currentUserId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        doc.getReference().update("status", "read");
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}