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

import android.widget.Button;


public class SelectGroupActivity extends AppCompatActivity {

    private TextView tvRecommendedGroup;
    private RecyclerView recyclerViewGroups;
    private GroupAdapter groupAdapter;
    private List<Group> groupList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private String userCity;
    private String recommendedGroup;

    private void updateUserGroup(Group group) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Ottieni l'ID dell'utente attuale
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Aggiorna l'ID del gruppo selezionato nel nodo dell'utente
        userRef.child("groupID").setValue(group.getGroupID()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SelectGroupActivity.this, "Gruppo aggiornato con successo!", Toast.LENGTH_SHORT).show();
                // Puoi aggiungere un'azione, come tornare alla HomeActivity
                finish(); // Torna alla schermata precedente
            } else {
                Toast.makeText(SelectGroupActivity.this, "Errore nell'aggiornamento del gruppo", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        tvRecommendedGroup = findViewById(R.id.tvRecommendedGroup);
        recyclerViewGroups = findViewById(R.id.recyclerViewGroups);

        Button btnCercaGruppo = findViewById(R.id.btnCercaGruppo);
        Button btnCreaGruppo = findViewById(R.id.btnCreaGruppo);

        btnCercaGruppo.setOnClickListener(v -> {
            Intent intent = new Intent(SelectGroupActivity.this, SearchGroupActivity.class);
            startActivity(intent);
        });

        btnCreaGruppo.setOnClickListener(v -> {
            Intent intent = new Intent(SelectGroupActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        });


        // Impostazioni RecyclerView già esistenti
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new GroupAdapter(groupList, group -> {
            // Quando l'utente seleziona un gruppo dalla lista
            updateUserGroup(group);
        });
        recyclerViewGroups.setAdapter(groupAdapter);

        userCity = getIntent().getStringExtra("userCity");
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups");

        // Recupera tutti i gruppi dal database (logica già esistente)
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    groupList.add(group);

                    if (group.getCity().equals(userCity)) {
                        recommendedGroup = group.getNome();
                    }
                }

                groupAdapter.notifyDataSetChanged();

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
}
