package com.example.tooltrip;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    private Button btnLogout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Deve essere chiamato prima di findViewById


        // Inizializzazione di Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Inizializzazione di Firebase Database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("items");

        // Troviamo le TextView per il messaggio di benvenuto e gli oggetti
        TextView txtWelcome = findViewById(R.id.txtWelcome);
        txtUserObjects = findViewById(R.id.txtUserObjects);

        // Impostiamo il messaggio di benvenuto
        String username = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getDisplayName() : "Utente";
        txtWelcome.setText("Bentornato " + username);

        // Recuperiamo gli oggetti dell'utente da Firebase
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> userObjects = new ArrayList<>();

                // Scorriamo tutti gli oggetti
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        // Otteniamo l'oggetto da Firebase
                        Item item = snapshot.getValue(Item.class);
                        if (item != null&& item.isPubblico()) {
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

                if (userObjects.isEmpty()) {
                    txtUserObjects.setText("Questi sono gli oggetti pubblici:\nNon ci sono oggetti pubblici disponibili.");
                } else {
                    // Costruiamo il testo per mostrare gli oggetti, uno per ogni riga
                    StringBuilder objectsText = new StringBuilder("Questi sono gli oggetti pubblici:\n");
                    for (int i = 0; i < userObjects.size(); i++) {
                        objectsText.append(userObjects.get(i)); // Aggiungi il nome dell'oggetto
                        if (i + 1 < userObjects.size()) {
                            objectsText.append(",\n"); // Aggiungi una virgola e vai a capo
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



        // Set up the menu listeners using MenuHandler
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );
    }
}
