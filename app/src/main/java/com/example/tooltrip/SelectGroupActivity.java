package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectGroupActivity extends AppCompatActivity {

    private TextView tvRecommendedGroup;
    private RecyclerView recyclerViewRecommendedGroups, recyclerViewAllGroups;
    private GroupAdapter recommendedGroupAdapter, allGroupAdapter;
    private List<Group> recommendedGroups = new ArrayList<>();
    private List<Group> allGroups = new ArrayList<>();
    private DatabaseReference groupReference, userReference;
    private String userProvince, userCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        // Collegamento agli elementi della UI
        tvRecommendedGroup = findViewById(R.id.tvRecommendedGroup);
        recyclerViewRecommendedGroups = findViewById(R.id.recyclerViewRecommendedGroups);
        recyclerViewAllGroups = findViewById(R.id.recyclerViewAllGroups);
        Button btnCercaGruppo = findViewById(R.id.btnCercaGruppo);
        Button btnCreaGruppo = findViewById(R.id.btnCreaGruppo);

        // Listener per i bottoni "Cerca Gruppo" e "Crea Gruppo"
        btnCercaGruppo.setOnClickListener(v -> {
            Intent intent = new Intent(SelectGroupActivity.this, SearchGroupActivity.class);
            startActivity(intent);
        });

        btnCreaGruppo.setOnClickListener(v -> {
            Intent intent = new Intent(SelectGroupActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        });

        // Configurazione RecyclerView per i gruppi raccomandati
        recyclerViewRecommendedGroups.setLayoutManager(new LinearLayoutManager(this));
        recommendedGroupAdapter = new GroupAdapter(recommendedGroups, group -> {
            // Logica per quando l'utente seleziona un gruppo raccomandato
            updateUserGroup(group);
        });
        recyclerViewRecommendedGroups.setAdapter(recommendedGroupAdapter);

        // Configurazione RecyclerView per tutti i gruppi
        recyclerViewAllGroups.setLayoutManager(new LinearLayoutManager(this));
        allGroupAdapter = new GroupAdapter(allGroups, group -> {
            // Logica per quando l'utente seleziona un gruppo generico
            updateUserGroup(group);
        });
        recyclerViewAllGroups.setAdapter(allGroupAdapter);

        // Recupera i dettagli dell'utente per determinare la provincia e la città
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null && currentUser.getAddress() != null) {
                    userProvince = currentUser.getAddress().getProvincia();
                    userCity = currentUser.getAddress().getCitta();
                    loadGroups(); // Carica i gruppi dopo aver recuperato i dati dell'utente
                } else {
                    Toast.makeText(SelectGroupActivity.this, "Errore nel recupero dei dati utente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SelectGroupActivity.this, "Errore nella connessione al database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metodo per caricare i gruppi dal database
    private void loadGroups() {
        groupReference = FirebaseDatabase.getInstance().getReference("Groups");
        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recommendedGroups.clear();
                allGroups.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group != null && group.getCreatore() != null && group.getCreatore().getAddress() != null) {
                        Address groupAddress = group.getCreatore().getAddress();
                        String groupProvince = groupAddress.getProvincia();
                        String groupCity = groupAddress.getCitta();

                        // Controlla se il gruppo è raccomandato
                        if (userProvince.equals(groupProvince)) {
                            if (userCity.equals(groupCity)) {
                                recommendedGroups.add(0, group); // Priorità alta per città
                            } else {
                                recommendedGroups.add(group); // Priorità per provincia
                            }
                        } else {
                            allGroups.add(group); // Altri gruppi
                        }
                    } else {
                        allGroups.add(group); // Se il gruppo non ha creatore o indirizzo, metti tra i generici
                    }
                }

                // Aggiorna i RecyclerView Adapter
                recommendedGroupAdapter.notifyDataSetChanged();
                allGroupAdapter.notifyDataSetChanged();

                // Mostra il titolo dei gruppi raccomandati solo se ci sono gruppi
                tvRecommendedGroup.setVisibility(recommendedGroups.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SelectGroupActivity.this, "Errore nel recupero dei gruppi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metodo per aggiornare l'ID del gruppo dell'utente
    private void updateUserGroup(Group group) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.child("groupID").setValue(group.getGroupID()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SelectGroupActivity.this, "Gruppo aggiornato con successo!", Toast.LENGTH_SHORT).show();
                // Puoi tornare alla HomeActivity o eseguire altre azioni
                startActivity(new Intent(SelectGroupActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(SelectGroupActivity.this, "Errore nell'aggiornamento del gruppo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
