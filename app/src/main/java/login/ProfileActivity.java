package login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

    private TextView txtProfilo, txtNomeCognome, txtNumeroTelefono, txtIndirizzo, txtModifica;
    private Button btnLogout, btnSalva, btnModifica, btnDeleteAccount;
    private EditText editTextTelefono, editTextVia, editTextCivico, editTextCitta, editTextProvincia, editTextCAP;
    private LinearLayout editLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ImageView imgIconaProfilo;

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
        txtModifica = findViewById(R.id.txtModifica);
        btnLogout = findViewById(R.id.btnLogout);
        btnSalva = findViewById(R.id.btnSalva);
        btnModifica = findViewById(R.id.btnModifica);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        editTextVia = findViewById(R.id.editTextIndirizzo);
        editTextCivico = findViewById(R.id.editTextCivico);
        editTextCitta = findViewById(R.id.editTextCitta);
        editTextProvincia = findViewById(R.id.editTextProvincia);
        editTextCAP = findViewById(R.id.editTextCAP);
        editLayout = findViewById(R.id.editLayout);

        // Imposta il titolo della pagina
        txtProfilo.setText("Profilo");

        imgIconaProfilo = findViewById(R.id.imgIconaProfilo);

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

        // Aggiungi comportamento al pulsante Modifica
        btnModifica.setOnClickListener(v -> {
            if (editLayout.getVisibility() == View.GONE) {
                editLayout.setVisibility(View.VISIBLE); // Mostra il layout di modifica
                btnModifica.setText("Annulla Modifiche"); // Cambia il testo del pulsante
            } else {
                editLayout.setVisibility(View.GONE); // Nascondi il layout di modifica
                btnModifica.setText("Modifica"); // Ripristina il testo originale del pulsante
            }
        });

        // Aggiungi comportamento per il salvataggio delle modifiche
        btnSalva.setOnClickListener(v -> {
            String nuovoTelefono = editTextTelefono.getText().toString();
            String nuovoCivico = editTextCivico.getText().toString();
            String nuovaCitta = editTextCitta.getText().toString();
            String nuovaProvincia = editTextProvincia.getText().toString();
            String nuovoCAP = editTextCAP.getText().toString();
            String nuovaVia = editTextVia.getText().toString();

            // Verifica che almeno uno dei campi non sia vuoto
            if (!nuovoTelefono.isEmpty() ||
                    (!nuovoCivico.isEmpty() && !nuovaCitta.isEmpty() && !nuovaProvincia.isEmpty() && !nuovoCAP.isEmpty() && !nuovaVia.isEmpty())) {

                if (userId != null) {
                    if (!nuovoTelefono.isEmpty()) {
                        mDatabase.child(userId).child("numTelefono").setValue(nuovoTelefono);
                        txtNumeroTelefono.setText("Telefono: " + nuovoTelefono); // Aggiorna la UI
                    }

                    if (!nuovoCivico.isEmpty() && !nuovaCitta.isEmpty() && !nuovaProvincia.isEmpty() && !nuovoCAP.isEmpty() && !nuovaVia.isEmpty()) {
                        mDatabase.child(userId).child("address").child("via").setValue(nuovaVia);
                        mDatabase.child(userId).child("address").child("civico").setValue(nuovoCivico);
                        mDatabase.child(userId).child("address").child("citta").setValue(nuovaCitta);
                        mDatabase.child(userId).child("address").child("provincia").setValue(nuovaProvincia);
                        mDatabase.child(userId).child("address").child("cap").setValue(nuovoCAP);

                        String nuovoIndirizzo = nuovaVia + ", " + nuovoCivico + ", " + nuovaCitta + " (" + nuovaProvincia + ") " + nuovoCAP;
                        txtIndirizzo.setText("Indirizzo: " + nuovoIndirizzo); // Aggiorna la UI
                    }

                    Toast.makeText(ProfileActivity.this, "Modifiche salvate!", Toast.LENGTH_SHORT).show();
                    editLayout.setVisibility(View.GONE); // Nascondi il layout di modifica dopo il salvataggio
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Per favore, inserisci almeno un dato da modificare.", Toast.LENGTH_SHORT).show();
            }
            btnModifica.setText("Modifica");
        });

        // Aggiungi comportamento per il pulsante Logout
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Aggiungi comportamento per il pulsante "Cancella Account"
        btnDeleteAccount.setOnClickListener(v -> {
            if (userId != null) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Conferma Eliminazione Account")
                        .setMessage("Sei sicuro di voler cancellare il tuo account? Questa operazione è irreversibile.")
                        .setPositiveButton("Elimina", (dialog, which) -> {
                            // 1. Rimuovi i dati dell'utente dal database (Users, tools, groups, ecc.)
                            mDatabase.child("Users").child(userId).removeValue()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Rimuovi altri dati relativi all'utente (tools, groups, address, recensioni)
                                            mDatabase.child("tools").child(userId).removeValue()
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()) {
                                                            mDatabase.child("groups").child(userId).removeValue()
                                                                    .addOnCompleteListener(task3 -> {
                                                                        if (task3.isSuccessful()) {
                                                                            mDatabase.child("Addresses").child(userId).removeValue()
                                                                                    .addOnCompleteListener(task4 -> {
                                                                                        if (task4.isSuccessful()) {
                                                                                            mDatabase.child("Recensioni").child(userId).removeValue()
                                                                                                    .addOnCompleteListener(task5 -> {
                                                                                                        if (task5.isSuccessful()) {
                                                                                                            // 2. Elimina l'utente da Firebase Authentication
                                                                                                            mAuth.getCurrentUser().delete()
                                                                                                                    .addOnCompleteListener(task6 -> {
                                                                                                                        if (task6.isSuccessful()) {
                                                                                                                            // 3. Effettua il logout
                                                                                                                            FirebaseAuth.getInstance().signOut();

                                                                                                                            // Mostra un messaggio di successo
                                                                                                                            Toast.makeText(ProfileActivity.this, "Account eliminato con successo", Toast.LENGTH_SHORT).show();

                                                                                                                            // Reindirizza l'utente alla schermata di login
                                                                                                                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                                                                                                            startActivity(intent);
                                                                                                                            finish(); // Chiudi questa Activity
                                                                                                                        } else {
                                                                                                                            Log.e("FirebaseError", "Errore nell'eliminazione dell'account da Firebase Auth: " + task6.getException().getMessage());
                                                                                                                            Toast.makeText(ProfileActivity.this, "Errore nell'eliminazione dell'account", Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    });
                                                                                                        } else {
                                                                                                            Log.e("FirebaseError", "Errore nell'eliminazione delle recensioni: " + task5.getException().getMessage());
                                                                                                            Toast.makeText(ProfileActivity.this, "Errore nell'eliminazione delle recensioni", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                        } else {
                                                                                            Log.e("FirebaseError", "Errore nell'eliminazione dell'indirizzo: " + task4.getException().getMessage());
                                                                                            Toast.makeText(ProfileActivity.this, "Errore nell'eliminazione dell'indirizzo", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                        } else {
                                                                            Log.e("FirebaseError", "Errore nell'eliminazione dei gruppi: " + task3.getException().getMessage());
                                                                            Toast.makeText(ProfileActivity.this, "Errore nell'eliminazione dei gruppi", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        } else {
                                                            Log.e("FirebaseError", "Errore nell'eliminazione degli strumenti: " + task2.getException().getMessage());
                                                            Toast.makeText(ProfileActivity.this, "Errore nell'eliminazione degli strumenti", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Log.e("FirebaseError", "Errore nell'eliminazione dei dati dell'utente: " + task1.getException().getMessage());
                                            Toast.makeText(ProfileActivity.this, "Errore nell'eliminazione dei dati dell'utente", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .setNegativeButton("Annulla", null)
                        .show();
            } else {
                Toast.makeText(ProfileActivity.this, "Utente non autenticato", Toast.LENGTH_SHORT).show();
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
