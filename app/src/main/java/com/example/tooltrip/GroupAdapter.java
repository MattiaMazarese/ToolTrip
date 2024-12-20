package com.example.tooltrip;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;

    public GroupAdapter(List<Group> groups) {
        this.groupList = groups;
    }

    public void updateGroups(List<Group> newGroups) {
        this.groupList = newGroups;
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
        Group group = groupList.get(position);
        holder.tvGroupName.setText("Nome: " + group.getNome());
        holder.tvGroupDetails.setText("Creatore: " + group.getCreatoreID());
    }

    @Override
    public int getItemCount() {
        return groupList.size();
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

/*
package com.example.tooltrip;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ToolViewHolder> {
    private List<Item> itemList;

    public ToolAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public ToolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tool, parent, false);
        return new ToolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ToolViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.textViewNome.setText(item.getNome());
        holder.textViewDescrizione.setText(item.getDescrizione());

        // Set image based on category
        String category = item.getCategoria();
        int categoryImage = getCategoryImage(category);
        holder.imageViewCategory.setImageResource(categoryImage);

        holder.btnVisualizza.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), VisualizzaProdottoSingoloActivity.class);
            intent.putExtra("itemNome", item.getNome());
            intent.putExtra("itemDescrizione", item.getDescrizione());
            intent.putExtra("itemCategoria", item.getCategoria());
            intent.putExtra("itemID", item.getItemId());
            intent.putExtra("possessoreID", item.getPossesore().getUserID());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ToolViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNome, textViewDescrizione;
        Button btnVisualizza;
        ImageView imageViewCategory;

        public ToolViewHolder(View itemView) {
            super(itemView);
            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewDescrizione = itemView.findViewById(R.id.textViewDescrizione);
            btnVisualizza = itemView.findViewById(R.id.btnVisualizza);
            imageViewCategory = itemView.findViewById(R.id.imageViewCategory);
        }
    }

    // Map category to image resource
    private int getCategoryImage(String category) {
        switch (category) {
            case "Elettronica":
                return R.drawable.ic_elettronica; // Replace with actual image resource
            case "Meccanica":
                return R.drawable.ic_gear; // Replace with actual image resource
            case "Informatica":
                return R.drawable.ic_devices; // Replace with actual image resource
            case "Altro":
                return R.drawable.ic_altro; // Replace with actual image resource
            default:
                return R.drawable.ic_altro; // Default image if category is not found
        }
    }
}

*/
