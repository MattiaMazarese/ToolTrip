package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText etGroupName, etGroupCode;
    private Switch switchGroupVisibility;
    private Button btnCreateGroup;
    private DatabaseReference databaseReference, usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        etGroupName = findViewById(R.id.etGroupName);
        etGroupCode = findViewById(R.id.etGroupCode);
        switchGroupVisibility = findViewById(R.id.switchGroupVisibility);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);

        databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        usersReference = FirebaseDatabase.getInstance().getReference("Users");

        // Mostra/nascondi il campo per il codice in base allo stato dello switch
        switchGroupVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etGroupCode.setVisibility(EditText.GONE); // Nascondi il campo per i gruppi pubblici
                etGroupCode.setText(""); // Cancella il valore del codice
            } else {
                etGroupCode.setVisibility(EditText.VISIBLE); // Mostra il campo per i gruppi privati
            }
        });

        btnCreateGroup.setOnClickListener(v -> {
            String groupName = etGroupName.getText().toString().trim();
            String groupCode = etGroupCode.getText().toString().trim(); // Potrebbe essere vuoto
            boolean isPublic = switchGroupVisibility.isChecked(); // Ottieni lo stato dello switch

            if (groupName.isEmpty()) {
                Toast.makeText(CreateGroupActivity.this, "Inserisci il nome del gruppo", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPublic && groupCode.isEmpty()) {
                Toast.makeText(CreateGroupActivity.this, "Inserisci il codice per i gruppi privati", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();
            String currentUserId = auth.getCurrentUser().getUid();

            // Recupera i dettagli completi dell'utente corrente
            usersReference.child(currentUserId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot snapshot = task.getResult();

                    // Recupera i dati dell'utente
                    String nome = snapshot.child("nome").getValue(String.class);
                    String cognome = snapshot.child("cognome").getValue(String.class);
                    String annoNascita = snapshot.child("annoNascita").getValue(String.class);
                    Address address = snapshot.child("address").getValue(Address.class);
                    String numTelefono = snapshot.child("numTelefono").getValue(String.class);

                    // Crea l'oggetto User con i dati recuperati
                    User creatore = new User(currentUserId, nome, cognome, annoNascita, address, numTelefono);

                    // Crea un ID univoco per il gruppo
                    String groupID = databaseReference.push().getKey();

                    // Crea il gruppo
                    Group group = new Group(groupID, groupName,creatore); // La città non è più usata
                    if (!isPublic) {
                        group.setCodice(groupCode); // Imposta il codice solo per i gruppi privati
                    }

                    // Salva il gruppo nel database
                    databaseReference.child(groupID).setValue(group).addOnCompleteListener(groupTask -> {
                        if (groupTask.isSuccessful()) {
                            Toast.makeText(CreateGroupActivity.this, "Gruppo creato con successo!", Toast.LENGTH_SHORT).show();

                            // Apri la schermata Home
                            Intent intent = new Intent(CreateGroupActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CreateGroupActivity.this, "Errore durante la creazione del gruppo", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CreateGroupActivity.this, "Errore nel recupero dei dati utente", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
