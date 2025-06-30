package com.example.guardify;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.guardify.databinding.ActivityHomeBinding;
import com.example.guardify.ui.Team;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    private CircleImageView navProfileImage;
    private TextView navHeaderName, navHeaderEmail, navHeaderBio, navHeaderSkills;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private List<Team> teamList = new ArrayList<>();
    private TeamAdapter adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Already in HomeActivity, maybe close drawer
                binding.drawerLayout.closeDrawers();
                return true;
           } else if (id == R.id.feedbackfragment) {
                startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));// Optional, only if you don't want to keep HomeActivity in stack
                return true;
            } else if (id == R.id.morefragment) {
                startActivity(new Intent(HomeActivity.this, MoreActivity.class));
                return true;
            }
            else if (id == R.id.shareFragment) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage = "Check out this amazing app SkillBridge!\n\nDownload now: https://play.google.com/store/apps/details?id=" + getPackageName();
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share SkillBridge via"));
                return true;
            }
            else if(id == R.id.signOutfragment){
                auth.signOut();
                startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
                finish();
                return true;
            }

            binding.drawerLayout.closeDrawer(binding.navView); // Close drawer after selection
            return true;
        });



        // Setup Toolbar
        Toolbar toolbar = binding.appBarHome.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setTitle("LinkUp");

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // RecyclerView
        adapter = new TeamAdapter(this, teamList);
        binding.appBarHome.teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.appBarHome.teamRecyclerView.setAdapter(adapter);

        // Create Team Button
        binding.appBarHome.btnCreateTeam.setOnClickListener(v -> showCreateTeamDialog());

        // Header data
        View headerView = navigationView.getHeaderView(0);
        navProfileImage = headerView.findViewById(R.id.navProfileImage);
        navHeaderName = headerView.findViewById(R.id.navHeaderName);
        navHeaderEmail = headerView.findViewById(R.id.navHeaderEmail);
        navHeaderBio = headerView.findViewById(R.id.navHeaderBio);
        navHeaderSkills = headerView.findViewById(R.id.navHeaderSkills);

        LinearLayout btnEditProfile = headerView.findViewById(R.id.linearLayout);
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileSetupActivity.class);
            intent.putExtra("editMode", true);
            startActivity(intent);
        });

        loadUserData();
        loadTeamsFromFirestore();
    }


    private void showCreateTeamDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_create_team, null);
        EditText etTeamName = view.findViewById(R.id.etTeamName);
        EditText etTeamDesc = view.findViewById(R.id.etTeamDesc);

        new AlertDialog.Builder(this)
                .setTitle("Create New Team")
                .setView(view)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = etTeamName.getText().toString().trim();
                    String desc = etTeamDesc.getText().toString().trim();
                    String uid = auth.getCurrentUser().getUid();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Team name required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Team team = new Team(name, desc, uid, System.currentTimeMillis(), new ArrayList<>(List.of(uid)));
                    db.collection("teams").add(team).addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Team Created!", Toast.LENGTH_SHORT).show();
                        loadTeamsFromFirestore();
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadTeamsFromFirestore() {
        db.collection("teams")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    teamList.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        Team team = doc.toObject(Team.class);
                        if (team != null) {
                            team.setId(doc.getId());
                            teamList.add(team);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void loadUserData() {
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        String email = auth.getCurrentUser().getEmail();
                        String bio = doc.getString("bio");
                        String profileUrl = doc.getString("profileImageUrl");
                        List<String> skills = (List<String>) doc.get("skills");

                        navHeaderName.setText(name != null ? name : "N/A");
                        navHeaderEmail.setText(email != null ? email : "N/A");
                        navHeaderBio.setText(bio != null ? bio : "No bio");
                        navHeaderSkills.setText(skills != null && !skills.isEmpty()
                                ? "Skills: " + String.join(", ", skills)
                                : "Skills: N/A");

                        if (profileUrl != null && !profileUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(profileUrl)
                                    .placeholder(R.drawable.prof)
                                    .into(navProfileImage);
                        } else {
                            navProfileImage.setImageResource(R.drawable.prof);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
}
