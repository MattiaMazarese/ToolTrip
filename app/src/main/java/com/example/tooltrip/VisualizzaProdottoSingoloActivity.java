package com.example.tooltrip;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class VisualizzaProdottoSingoloActivity extends AppCompatActivity {

    private TextView textViewNome, textViewDescrizione, textViewCategoria;
    private Button buttonPrestito;
    private DatabaseReference mDatabase;
    private String prestitoId = null;
    private String prestitoUserID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodotto_singolo);

        // Inizializza Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        textViewNome = findViewById(R.id.textViewNome);
        textViewDescrizione = findViewById(R.id.textViewDescrizione);
        textViewCategoria = findViewById(R.id.textViewCategoria);
        buttonPrestito = findViewById(R.id.buttonPrestito);

        // Ottieni i dati passati dall'intent
        String nome = getIntent().getStringExtra("itemNome");
        String descrizione = getIntent().getStringExtra("itemDescrizione");
        String categoria = getIntent().getStringExtra("itemCategoria");
        String itemID = getIntent().getStringExtra("itemID");

        // Ottieni il prestito dal database
        DatabaseReference prestitoRef = FirebaseDatabase.getInstance().getReference("Prestito");
        prestitoRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();

                // Ciclo attraverso tutti gli elementi di "Prestito"
                for (DataSnapshot prestitoSnapshot : dataSnapshot.getChildren()) {
                    // Ottieni i dati dai campi del prestito
                    if (Objects.equals(prestitoSnapshot.child("idOggetto").getValue(String.class), getIntent().getStringExtra("itemID"))) {
                        prestitoId = prestitoSnapshot.child("prestitoID").getValue(String.class);
                        prestitoUserID=prestitoSnapshot.child("idUtente").getValue(String.class);
                        break;  // Esci dal ciclo una volta trovato il prestito
                    }
                }

                // Dopo aver trovato il prestitoId, imposta il comportamento del pulsante
                setButtonAction(itemID); // Aggiungi questa riga per chiamare il metodo che gestisce il bottone
            } else {
                // Gestione errori nel caso la richiesta fallisca
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Errore durante il recupero dei dati: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Imposta i dati nelle TextView
        textViewNome.setText(nome);
        textViewDescrizione.setText(descrizione);
        textViewCategoria.setText(categoria);

        // Configurazione del menu tramite MenuHandler
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );
    }

    private void setButtonAction(String itemID) {
        // Imposta il comportamento del pulsante dopo che prestitoId è stato aggiornato
        if (itemPrestato()) {
            if (utentePrestitoItem()) {
                buttonPrestito.setText("Restituisci tool");
                buttonPrestito.setOnClickListener(v -> remouvePrestitoFromDatabase());
            } else {
                buttonPrestito.setText("tool non disponibile");
                buttonPrestito.setOnClickListener(v -> messaggioFinePrestito());
            }
        } else {
            buttonPrestito.setText("Prendi in prestito il tool");
            buttonPrestito.setOnClickListener(v -> addPrestitoToDatabase(itemID));
        }
    }

    private void messaggioFinePrestito() {
        // Verifica se il prestitoID è presente prima di accedere a Firebase
        if (prestitoId != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Prestito").child(prestitoId).child("dataFine");
            Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Il prestito finirà " + databaseReference.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean itemPrestato(){
        if(prestitoId==null){
            //Toast.makeText(VisualizzaProdottoSingoloActivity.this,"null: " + prestitoId,Toast.LENGTH_SHORT).show();
            return false;
        }
        //Toast.makeText(VisualizzaProdottoSingoloActivity.this,"not null: " + prestitoId,Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean utentePrestitoItem(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return false;
        }
        String userID = currentUser.getUid();
        if(userID.equals(prestitoUserID)){
            return true;
        }else{
            return false;
        }
    }

    private void addPrestitoToDatabase(String itemID) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a unique item ID
        prestitoId = mDatabase.push().getKey();

        String userID = currentUser.getUid();
        Prestito newPrestito=new Prestito(prestitoId,userID,itemID);

        // Save the item to Firebase Realtime Database
        if (prestitoId != null) {
            mDatabase.child("Prestito").child(prestitoId).setValue(newPrestito).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Prestito added successfully", Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Failed to add prestito. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Failed to generate item ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void remouvePrestitoFromDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Prestito").child(prestitoId);
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Prestito rimosso successfully", Toast.LENGTH_SHORT).show();
                recreate();
            } else {
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Failed to rimuovere prestito. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

