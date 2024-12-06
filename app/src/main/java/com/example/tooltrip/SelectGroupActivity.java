package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SelectGroupActivity extends AppCompatActivity {

    private TextView tvRecommendedGroup;
    private RecyclerView recyclerViewGroups;
    private GroupAdapter groupAdapter;
    private List<Group> groupList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private String userCity;
    private String recommendedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        tvRecommendedGroup = findViewById(R.id.tvRecommendedGroup);
        recyclerViewGroups = findViewById(R.id.recyclerViewGroups);

        // Imposta RecyclerView
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new GroupAdapter(groupList, group -> {
            // Quando l'utente seleziona un gruppo
            updateUserGroup(group);
        });
        recyclerViewGroups.setAdapter(groupAdapter);

        // Recupera la città dell'utente (ad esempio da un campo Address)
        userCity = getIntent().getStringExtra("userCity");  // Passato da HomeActivity o dove è disponibile
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups");

        // Recupera tutti i gruppi dal database
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    groupList.add(group);

                    // Se il gruppo è della zona dell'utente, lo mostriamo prima
                    if (group.getCity().equals(userCity)) {
                        recommendedGroup = group.getNome();
                    }
                }

                // Aggiorna la lista dei gruppi
                groupAdapter.notifyDataSetChanged();

                // Mostra il gruppo raccomandato
                if (recommendedGroup != null) {
                    tvRecommendedGroup.setText("Gruppo raccomandato: " + recommendedGroup);
                    tvRecommendedGroup.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                Toast.makeText(SelectGroupActivity.this, "Errore nel recupero dei gruppi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserGroup(Group group) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.child("groupID").setValue(group.getGroupID()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Vai alla Home dopo aver selezionato il gruppo
                startActivity(new Intent(SelectGroupActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(SelectGroupActivity.this, "Errore nell'aggiornamento del gruppo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
