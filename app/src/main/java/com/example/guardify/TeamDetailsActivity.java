package com.example.guardify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guardify.ui.Team;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeamDetailsActivity extends AppCompatActivity {

    private TextView tvTeamName, tvTeamDescription, tvCreatedBy;
    private RecyclerView memberRecyclerView;
    private MaterialButton btnLeaveTeam;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String teamId;
    private Team team;
    private TeamMembersAdapter membersAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        teamId = getIntent().getStringExtra("teamId");
        if (teamId == null) {
            Toast.makeText(this, "Team not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind views
        tvTeamName = findViewById(R.id.tvTeamName);
        tvTeamDescription = findViewById(R.id.tvTeamDescription);
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        memberRecyclerView = findViewById(R.id.memberRecyclerView);
        btnLeaveTeam = findViewById(R.id.btnLeaveTeam);

        memberRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnChat).setOnClickListener(v -> {
            Intent intent = new Intent(TeamDetailsActivity.this, ChatActivity.class);
            intent.putExtra("teamId", teamId);  // pass team ID
            startActivity(intent);
        });


        loadTeamDetails();
    }

    private void loadTeamDetails() {
        db.collection("teams").document(teamId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        team = doc.toObject(Team.class);
                        team.setId(doc.getId());

                        tvTeamName.setText(team.getName());
                        tvTeamDescription.setText(team.getDescription());

                        // Load creator name
                        db.collection("users").document(team.getCreatedBy())
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        String name = userDoc.getString("name");
                                        tvCreatedBy.setText("Created By: " + (name != null ? name : "Unknown"));
                                    }
                                });

                        membersAdapter = new TeamMembersAdapter(this, team.getMembers());
                        memberRecyclerView.setAdapter(membersAdapter);

                        btnLeaveTeam.setOnClickListener(v -> leaveTeam());

                    } else {
                        Toast.makeText(this, "Team not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load team", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void leaveTeam() {
        String uid = auth.getCurrentUser().getUid();

        if (!team.getMembers().contains(uid)) {
            Toast.makeText(this, "You are not a member", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Leave Team")
                .setMessage("Are you sure you want to leave this team?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("teams").document(teamId)
                            .update("members", FieldValue.arrayRemove(uid))
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Left the team", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
