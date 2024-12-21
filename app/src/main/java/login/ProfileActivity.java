package login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.tooltrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import menù.MenuHandler;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtProfilo, txtNomeCognome, txtNumeroTelefono, txtIndirizzo;
    private Button btnLogout, btnSalva;
    private EditText editTextTelefono, editTextVia, editTextCivico, editTextCitta, editTextProvincia, editTextCAP;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Collegamento delle TextView, EditText e Button dal layout
        txtProfilo = findViewById(R.id.txtProfilo);
        txtNomeCognome = findViewById(R.id.txtNomeCognome);
        txtNumeroTelefono = findViewById(R.id.txtNumeroTelefono);
        txtIndirizzo = findViewById(R.id.txtIndirizzo);
        btnLogout = findViewById(R.id.btnLogout);
        btnSalva = findViewById(R.id.btnSalva);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        editTextVia = findViewById(R.id.editTextIndirizzo); // Cambiato "editTextIndirizzo" in "editTextVia"
        editTextCivico = findViewById(R.id.editTextCivico);
        editTextCitta = findViewById(R.id.editTextCitta);
        editTextProvincia = findViewById(R.id.editTextProvincia);
        editTextCAP = findViewById(R.id.editTextCAP);

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
                    String telefono = dataSnapshot.child("numTelefono").getValue(String.class);
                    String via = dataSnapshot.child("address").child("via").getValue(String.class);
                    String civico = dataSnapshot.child("address").child("civico").getValue(String.class);
                    String citta = dataSnapshot.child("address").child("citta").getValue(String.class);
                    String provincia = dataSnapshot.child("address").child("provincia").getValue(String.class);
                    String cap = dataSnapshot.child("address").child("cap").getValue(String.class);

                    // Imposta i dati nel layout
                    if (nome != null && cognome != null) {
                        txtNomeCognome.setText("Nome: " + nome + " Cognome: " + cognome);
                    } else {
                        txtNomeCognome.setText("Nome utente non trovato.");
                    }

                    if (telefono != null) {
                        txtNumeroTelefono.setText("Telefono: " + telefono);
                    } else {
                        txtNumeroTelefono.setText("Telefono non disponibile.");
                    }

                    if (via != null && civico != null && citta != null && provincia != null) {
                        String indirizzo = via + ", " + civico + ", " + citta + " (" + provincia + ") " + cap;
                        txtIndirizzo.setText("Indirizzo: " + indirizzo);
                    } else {
                        txtIndirizzo.setText("Indirizzo non disponibile.");
                    }

                    Log.d("Firebase", "Utente trovato: " + nome + " " + cognome);
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

        // Aggiungi il comportamento per il salvataggio delle modifiche
        btnSalva.setOnClickListener(v -> {
            String nuovoTelefono = editTextTelefono.getText().toString();
            String nuovoCivico = editTextCivico.getText().toString();
            String nuovaCitta = editTextCitta.getText().toString();
            String nuovaProvincia = editTextProvincia.getText().toString();
            String nuovoCAP = editTextCAP.getText().toString();
            String nuovaVia = editTextVia.getText().toString(); // Cambiato "editTextIndirizzo" in "editTextVia"

            // Verifica che almeno uno dei campi non sia vuoto
            if (!nuovoTelefono.isEmpty() ||
                    (!nuovoCivico.isEmpty() && !nuovaCitta.isEmpty() && !nuovaProvincia.isEmpty() && !nuovoCAP.isEmpty() && !nuovaVia.isEmpty())) {

                if (userId != null) {
                    // Aggiorna solo il campo telefono se è stato modificato
                    if (!nuovoTelefono.isEmpty()) {
                        mDatabase.child(userId).child("numTelefono").setValue(nuovoTelefono);
                        txtNumeroTelefono.setText("Telefono: " + nuovoTelefono); // Aggiorna la UI
                    }

                    // Aggiornamento dell'indirizzo solo se tutti i campi sono compilati
                    if (!nuovoCivico.isEmpty() && !nuovaCitta.isEmpty() && !nuovaProvincia.isEmpty() && !nuovoCAP.isEmpty() && !nuovaVia.isEmpty()) {
                        mDatabase.child(userId).child("address").child("via").setValue(nuovaVia);
                        mDatabase.child(userId).child("address").child("civico").setValue(nuovoCivico);
                        mDatabase.child(userId).child("address").child("citta").setValue(nuovaCitta);
                        mDatabase.child(userId).child("address").child("provincia").setValue(nuovaProvincia);
                        mDatabase.child(userId).child("address").child("cap").setValue(nuovoCAP);

                        // Ricostruzione dell'indirizzo completo e aggiornamento della UI
                        String indirizzoCompleto = nuovaVia + ", " + nuovoCivico + ", " + nuovaCitta + " (" + nuovaProvincia + ") " + nuovoCAP;
                        txtIndirizzo.setText("Indirizzo: " + indirizzoCompleto); // Aggiorna la UI
                    } else {
                        // Se non tutti i campi dell'indirizzo sono compilati, non aggiornarli
                        Toast.makeText(ProfileActivity.this, "Completa tutti i campi dell'indirizzo", Toast.LENGTH_SHORT).show();
                    }

                    // Mostra un messaggio di conferma se almeno un campo è stato modificato
                    Toast.makeText(ProfileActivity.this, "Dati aggiornati con successo", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Nessun campo compilato, quindi non fare nulla
                Toast.makeText(ProfileActivity.this, "Nessun campo modificato", Toast.LENGTH_SHORT).show();
            }
        });

        // Aggiungi il comportamento per il logout
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut(); // Esegui il logout
            Toast.makeText(ProfileActivity.this, "Logout effettuato con successo", Toast.LENGTH_SHORT).show();

            // Avvia la schermata di login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Chiudi questa Activity
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
