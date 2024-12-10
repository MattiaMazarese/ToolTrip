package com.example.tooltrip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class ProfileActivity extends AppCompatActivity {

    private TextView txtProfilo, txtNomeCognome;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Collegamento delle TextView e Button dal layout
        txtProfilo = findViewById(R.id.txtProfilo);
        txtNomeCognome = findViewById(R.id.txtNomeCognome);
        btnLogout = findViewById(R.id.btnLogout);

        // Imposta il titolo della pagina
        txtProfilo.setText("Profilo");

        // Inizializzazione di Firebase Auth e Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        // Recupero dell'ID utente corrente
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId != null) {
            // Recupero delle informazioni dall'utente nella tabella "Users"
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nome = dataSnapshot.child("nome").getValue(String.class);
                    String cognome = dataSnapshot.child("cognome").getValue(String.class);

                    // Verifica e imposta i valori trovati
                    if (nome != null && cognome != null) {
                        txtNomeCognome.setText(nome + " " + cognome);
                        Log.d("Firebase", "Utente trovato: " + nome + " " + cognome);
                    } else {
                        txtNomeCognome.setText("Nome utente non trovato.");
                        Log.e("Firebase", "Dati utente non trovati per l'ID: " + userId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    txtNomeCognome.setText("Errore nel caricamento del profilo.");
                    Log.e("Firebase", "Errore nel database: " + databaseError.getMessage());
                }
            });
        } else {
            txtNomeCognome.setText("Utente non autenticato.");
        }

        // Aggiungi il comportamento per il logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut(); // Esegui il logout
                Toast.makeText(ProfileActivity.this, "Logout effettuato con successo", Toast.LENGTH_SHORT).show();
                // Puoi anche avviare una nuova Activity per reindirizzare l'utente alla schermata di login
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Chiudi questa Activity
            }
        });

        // Configurazione del menu tramite MenuHandler
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );
    }
}
