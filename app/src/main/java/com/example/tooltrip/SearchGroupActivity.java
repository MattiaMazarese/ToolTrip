package com.example.tooltrip;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupActivity extends AppCompatActivity {

    private RecyclerView rvGroupList;
    private GroupAdapter groupAdapter;
    private DatabaseReference groupRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        rvGroupList = findViewById(R.id.rvGroupList);
        rvGroupList.setLayoutManager(new LinearLayoutManager(this));

        groupAdapter = new GroupAdapter(new ArrayList<>());
        rvGroupList.setAdapter(groupAdapter);

        groupRef = FirebaseDatabase.getInstance().getReference("Groups");

        loadAllGroups();
    }

    private void loadAllGroups() {
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Group> groups = new ArrayList<>();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    if (group != null) {
                        groups.add(group);
                    }
                }
                if (groups.isEmpty()) {
                    Toast.makeText(SearchGroupActivity.this, "Nessun gruppo trovato", Toast.LENGTH_SHORT).show();
                } else {
                    groupAdapter.updateGroups(groups);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchGroupActivity.this, "Errore nel caricamento dei gruppi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
