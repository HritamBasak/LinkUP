package com.example.guardify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TeamMembersAdapter extends RecyclerView.Adapter<TeamMembersAdapter.MemberViewHolder> {

    private Context context;
    private List<String> memberIds;

    public TeamMembersAdapter(Context context, List<String> memberIds) {
        this.context = context;
        this.memberIds = memberIds;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String uid = memberIds.get(position);

        // Fetch user details from Firestore
        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        List<String> skillsList = (List<String>) doc.get("skills");

                        holder.tvName.setText(name != null ? name : "Unnamed");

                        if (skillsList != null && !skillsList.isEmpty()) {
                            holder.tvSkills.setText("Skills: " + String.join(", ", skillsList));
                        } else {
                            holder.tvSkills.setText("Skills: N/A");
                        }

                    }
                });
    }

    @Override
    public int getItemCount() {
        return memberIds.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSkills;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvSkills = itemView.findViewById(R.id.tvSkills);
        }
    }
}
