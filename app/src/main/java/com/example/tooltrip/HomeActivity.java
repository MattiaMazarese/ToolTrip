package com.example.tooltrip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView txtUserObjects;
    private TextView txtWelcome;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inizializzazione di Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Inizializzazione di Firebase Database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Troviamo le TextView per il messaggio di benvenuto e gli oggetti
        txtWelcome = findViewById(R.id.txtWelcome);
        txtUserObjects = findViewById(R.id.txtUserObjects);

        // Recuperiamo l'ID dell'utente loggato
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId != null) {
            // Ottieni i dati dell'utente dal nodo "users"
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Recupera i dati dell'utente dal database
                        String nome = dataSnapshot.child("nome").getValue(String.class);

                        // Se il nome esiste, impostiamolo, altrimenti usiamo il nome di display di Firebase Auth
                        if (nome != null) {
                            txtWelcome.setText("Bentornato, " + nome + "!");
                        } else {
                            // Usa il nome di display se il nome non è disponibile nel database
                            String username = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getDisplayName() : "Utente";
                            txtWelcome.setText("Bentornato, " + username + "!");
                        }
                    } else {
                        // Se l'utente non esiste nel database, usa il nome di display di Firebase Auth
                        String username = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getDisplayName() : "Utente";
                        txtWelcome.setText("Bentornato, " + username + "!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Errore nel recupero dati utente: " + databaseError.getMessage());
                    txtWelcome.setText("Bentornato, Utente!");
                }
            });
        }

        // Aggiungiamo una riga iniziale per indicare gli oggetti
        txtUserObjects.setText("Questi sono i tuoi oggetti:\n");

        // Recuperiamo gli oggetti dell'utente da Firebase
        DatabaseReference mItemsDatabase = FirebaseDatabase.getInstance().getReference("items");

        mItemsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> userObjects = new ArrayList<>();

                // Scorriamo tutti gli oggetti
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        // Otteniamo l'oggetto da Firebase
                        Item item = snapshot.getValue(Item.class);
                        if (item != null) {
                            // Aggiungiamo il nome e la categoria dell'oggetto alla lista
                            String itemDetails = item.getNome() + " - " + item.getCategoria();
                            userObjects.add(itemDetails);
                            Log.d("Firebase", "Oggetto recuperato: " + itemDetails);
                        } else {
                            Log.d("Firebase", "Oggetto vuoto trovato");
                        }
                    } catch (Exception e) {
                        Log.e("Firebase", "Errore nel recupero dell'oggetto: " + e.getMessage());
                    }
                }

                // Se non ci sono oggetti, mostriamo un messaggio
                if (userObjects.isEmpty()) {
                    txtUserObjects.setText("Questi sono i tuoi oggetti:\nNon hai oggetti.");
                } else {
                    // Organizzo gli oggetti in coppie (due per ogni riga)
                    StringBuilder objectsText = new StringBuilder("Questi sono i tuoi oggetti:\n");
                    for (int i = 0; i < userObjects.size(); i++) {
                        if (i % 2 == 0) {
                            objectsText.append(userObjects.get(i)); // Aggiungi il primo oggetto della coppia
                            if (i + 1 < userObjects.size()) {
                                objectsText.append(", ").append(userObjects.get(i + 1)); // Aggiungi il secondo oggetto della coppia
                            }
                            objectsText.append("\n"); // Vai a capo dopo ogni coppia
                        }
                    }

                    // Impostiamo il testo degli oggetti
                    txtUserObjects.setText(objectsText.toString());
                    Log.d("Firebase", "Oggetti visualizzati: " + objectsText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Errore nel recupero dati: " + databaseError.getMessage());
                txtUserObjects.setText("Errore nel recupero degli oggetti.");
            }
        });

        // Listener per l'icona Home
        findViewById(R.id.iconHome).setOnClickListener(v -> {
            // Reindirizza all'HomeActivity (la stessa activity in questo caso)
            startActivity(new Intent(HomeActivity.this, HomeActivity.class));
        });

        // Listener per l'icona AggiungiTool
        findViewById(R.id.iconAggiungiTool).setOnClickListener(v -> {
            // Reindirizza alla AggiungiToolActivity (assicurati che questa activity esista)
            startActivity(new Intent(HomeActivity.this, AggiungiToolActivity.class));
        });

        // Listener per l'icona VisualizzaTool
        findViewById(R.id.iconVisualizzaTool).setOnClickListener(v -> {
            // Reindirizza alla VisualizzaToolActivity (assicurati che questa activity esista)
            startActivity(new Intent(HomeActivity.this, VisualizzaToolActivity.class));
        });

        // Listener per l'icona Profilo
        findViewById(R.id.iconProfile).setOnClickListener(v -> {
            // Reindirizza alla ProfileActivity (assicurati che questa activity esista)
            startActivity(new Intent(HomeActivity.this, HomeActivity.class));
        });
    }
}
