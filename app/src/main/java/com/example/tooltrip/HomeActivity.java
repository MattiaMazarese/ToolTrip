package com.example.tooltrip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inizializzazione di Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Inizializzazione di Firebase Database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("items");

        // Troviamo la TextView per il messaggio di benvenuto
        TextView txtWelcome = findViewById(R.id.txtWelcome);

        // Ottieni l'UID dell'utente
        String userID = mAuth.getCurrentUser().getUid();

        // Ottieni una referenza al database Realtime
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Esegui una query per ottenere i dati dell'utente
        database.child("Users").child(userID).child("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Controlla se i dati esistono
                if (dataSnapshot.exists()) {
                    // Ottieni il nome dell'utente
                    String userName = dataSnapshot.getValue(String.class);

                    // Imposta il nome dell'utente nel TextView
                    txtWelcome.setText("Bentornato " + userName);
                } else {
                    // Se i dati non esistono
                    txtWelcome.setText("Utente non trovato");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // In caso di errore nella lettura
                txtWelcome.setText("Errore nel recupero dei dati");
            }
        });

        // Troviamo i GridLayout per oggetti pubblici e privati
        GridLayout gridPublicObjects = findViewById(R.id.gridPublicObjects);
        GridLayout gridMyObjects = findViewById(R.id.gridMyObjects);

        // Recuperiamo gli oggetti dal database
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Item> publicObjects = new ArrayList<>();
                List<Item> myObjects = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Item item = snapshot.getValue(Item.class);
                        if (item != null) {
                            if (item.isPubblico()) {
                                publicObjects.add(item);
                            } else if (item.getPossesore().getUserID().equals(userID)) {
                                myObjects.add(item);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Firebase", "Errore nel recupero dell'oggetto: " + e.getMessage());
                    }
                }

                // Aggiungi gli oggetti pubblici
                for (Item item : publicObjects) {
                    addItemToGrid(gridPublicObjects, item);
                }

                // Aggiungi i miei oggetti
                for (Item item : myObjects) {
                    addItemToGrid(gridMyObjects, item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Errore nel recupero dati: " + databaseError.getMessage());
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

        // Metodo per aggiungere un oggetto al GridLayout
        private void addItemToGrid(GridLayout grid, Item item) {
            // Inflating a custom view for each item (CardView)
            View itemView = getLayoutInflater().inflate(R.layout.item_layout, null);

            TextView txtItemName = itemView.findViewById(R.id.txtItemName);
            Button btnDiscover = itemView.findViewById(R.id.btnDiscover);

            // Impostare il nome dell'oggetto
            txtItemName.setText(item.getNome());

            btnDiscover.setOnClickListener(v -> {
                // Creare un Intent per aprire VisualizzaProdottoSingolo
                Intent intent = new Intent(itemView.getContext(), VisualizzaProdottoSingoloActivity.class);

                intent.putExtra("itemNome", item.getNome());
                intent.putExtra("itemDescrizione", item.getDescrizione());
                intent.putExtra("itemCategoria", item.getCategoria());
                itemView.getContext().startActivity(intent);
            });



            // Aggiungere la vista al GridLayout
            grid.addView(itemView);
        }
    }




