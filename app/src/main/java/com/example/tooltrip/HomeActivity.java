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

        // Troviamo il GridLayout per oggetti pubblici
        GridLayout gridPublicObjects = findViewById(R.id.gridPublicObjects);

        // Recuperiamo solo gli oggetti pubblici dal database
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Item> publicObjects = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Item item = snapshot.getValue(Item.class);
                        if (item != null && item.isPubblico()) { // Verifica solo gli oggetti pubblici
                            publicObjects.add(item);
                        }
                    } catch (Exception e) {
                        Log.e("Firebase", "Errore nel recupero dell'oggetto: " + e.getMessage());
                    }
                }

                // Aggiungi gli oggetti pubblici al GridLayout
                for (Item item : publicObjects) {
                    Log.d("PublicObjects", "Numero di oggetti pubblici: " + publicObjects.size());
                    Log.d("PublicObjects", "Aggiungo oggetto: " + item.getNome());
                    addItemToGrid(gridPublicObjects, item);
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

    private void addItemToGrid(GridLayout grid, Item item) {
        // Inflate il layout della card
        View itemView = getLayoutInflater().inflate(R.layout.item_layout, grid, false);

        TextView txtItemName = itemView.findViewById(R.id.txtItemName);
        Button btnDiscover = itemView.findViewById(R.id.btnDiscover);

        // Imposta i dati nella card
        txtItemName.setText(item.getNome());

        btnDiscover.setOnClickListener(v -> {
            Intent intent = new Intent(itemView.getContext(), VisualizzaProdottoSingoloActivity.class);
            intent.putExtra("itemNome", item.getNome());
            intent.putExtra("itemDescrizione", item.getDescrizione());
            intent.putExtra("itemCategoria", item.getCategoria());
            itemView.getContext().startActivity(intent);
        });

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = GridLayout.LayoutParams.MATCH_PARENT; // Occupa tutta la larghezza
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.setMargins(8, 8, 8, 8); // Aggiungi margini per spaziatura

        itemView.setLayoutParams(layoutParams);


        // Aggiungi la vista al GridLayout
        grid.addView(itemView);
    }


}





