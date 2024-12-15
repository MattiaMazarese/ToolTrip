package com.example.tooltrip;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyGroupActivity extends AppCompatActivity {

    private TextView tvGroupName, tvGroupCreator, tvGroupCode, tvGroupCity, tvGroupProvince;
    private DatabaseReference groupRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        // Collegamento ai componenti UI
        tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupCreator = findViewById(R.id.tvGroupCreator);
        tvGroupCode = findViewById(R.id.tvGroupCode);
        tvGroupCity = findViewById(R.id.tvGroupCity);
        tvGroupProvince = findViewById(R.id.tvGroupProvince);

        // Firebase reference
        groupRef = FirebaseDatabase.getInstance().getReference("Groups");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Carica il gruppo
        loadMyGroup();
    }

    private void loadMyGroup() {
        groupRef.orderByChild("creatoreID").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                                Group group = groupSnapshot.getValue(Group.class);
                                if (group != null) {
                                    displayGroupData(group);
                                    return; // Esci dopo aver trovato il primo gruppo
                                }
                            }
                        } else {
                            Toast.makeText(MyGroupActivity.this, "Nessun gruppo trovato", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyGroupActivity.this, "Errore nel caricamento: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayGroupData(Group group) {
        tvGroupName.setText("Nome Gruppo: " + group.getNome());
        tvGroupCreator.setText("Creatore ID: " + group.getCreatoreID());
        tvGroupCode.setText("Codice: " + (group.getCodice() != null ? group.getCodice() : "Nessuno"));

        // Mostra città e provincia se disponibili
        if (group.getCreatore() != null && group.getCreatore().getAddress() != null) {
            tvGroupCity.setText("Città: " + group.getCreatore().getAddress().getCitta());
            tvGroupProvince.setText("Provincia: " + group.getCreatore().getAddress().getProvincia());
        } else {
            tvGroupCity.setText("Città: Non disponibile");
            tvGroupProvince.setText("Provincia: Non disponibile");
        }
    }
}
