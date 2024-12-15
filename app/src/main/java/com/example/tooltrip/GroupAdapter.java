package com.example.tooltrip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groups;

    public GroupAdapter(List<Group> groups) {
        this.groups = groups;
    }

    public void updateGroups(List<Group> newGroups) {
        this.groups = newGroups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.tvGroupName.setText("Nome: " + group.getNome());
        holder.tvGroupDetails.setText("Creatore: " + group.getCreatoreID());
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName, tvGroupDetails;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvGroupDetails = itemView.findViewById(R.id.tvGroupDetails);
        }
    }
}
