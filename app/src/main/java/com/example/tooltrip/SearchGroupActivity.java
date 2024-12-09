package com.example.tooltrip;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGroups;
    private GroupAdapter groupAdapter;
    private List<Group> groupList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        recyclerViewGroups = findViewById(R.id.recyclerViewGroups);

        // Configura RecyclerView
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new GroupAdapter(groupList, group -> {
            Toast.makeText(SearchGroupActivity.this, "Hai selezionato il gruppo: " + group.getNome(), Toast.LENGTH_SHORT).show();
        });
        recyclerViewGroups.setAdapter(groupAdapter);

        // Recupera gruppi dal database
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    groupList.add(group);
                }
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                Toast.makeText(SearchGroupActivity.this, "Errore nel recupero dei gruppi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
