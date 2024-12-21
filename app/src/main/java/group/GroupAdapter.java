package group;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tooltrip.R;

import java.util.ArrayList;
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
        holder.groupName.setText("* " + group.getNome());

        holder.btnAccess.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), VisualizzaGruppoSingoloActivity.class);
            intent.putExtra("groupID", group.getGroupID());
            intent.putExtra("groupNome", group.getNome());
            intent.putExtra("creatoreID", group.getCreatoreID());
            intent.putStringArrayListExtra("membri", new ArrayList<>(group.getMembri()));
            intent.putExtra("codice", group.getCodice());
            holder.itemView.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        Button btnAccess;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.tvGroupName);
            btnAccess = itemView.findViewById(R.id.btnAccess);
        }
    }
}
