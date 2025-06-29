package com.example.guardify;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.guardify.ui.Team;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private Context context;
    private List<Team> teamList;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public TeamAdapter(Context context, List<Team> teamList) {
        this.context = context;
        this.teamList = teamList;
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teamList.get(position);
        holder.tvName.setText(team.getName());
        holder.tvDesc.setText(team.getDescription());

        holder.cardTeam.setOnClickListener(v -> {
            String uid = auth.getCurrentUser().getUid();

            // Always check from Firestore for accurate member list
            db.collection("teams").document(team.getId()).get()
                    .addOnSuccessListener(docSnapshot -> {
                        if (docSnapshot.exists()) {
                            List<String> members = (List<String>) docSnapshot.get("members");
                            boolean isMember = members != null && members.contains(uid);

                            if (isMember) {
                                // Open details if already a member
                                Intent intent = new Intent(context, TeamDetailsActivity.class);
                                intent.putExtra("teamId", team.getId());
                                context.startActivity(intent);
                            } else {
                                // Ask for confirmation before joining
                                new android.app.AlertDialog.Builder(context)
                                        .setTitle("Join Team")
                                        .setMessage("Are you sure you want to join the team \"" + team.getName() + "\"?")
                                        .setPositiveButton("Join", (dialog, which) -> {
                                            db.collection("teams").document(team.getId())
                                                    .update("members", FieldValue.arrayUnion(uid))
                                                    .addOnSuccessListener(unused -> {
                                                        Toast.makeText(context, "Joined " + team.getName(), Toast.LENGTH_SHORT).show();
                                                        team.getMembers().add(uid); // Update local list
                                                        notifyItemChanged(position);

                                                        Intent intent = new Intent(context, TeamDetailsActivity.class);
                                                        intent.putExtra("teamId", team.getId());
                                                        context.startActivity(intent);
                                                    });
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        }
                    });
        });


    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;
        CardView cardTeam;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTeamName);
            tvDesc = itemView.findViewById(R.id.tvTeamDesc);
            cardTeam = itemView.findViewById(R.id.cardTeam);
        }
    }
}
