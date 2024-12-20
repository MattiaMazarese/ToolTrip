package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNome, etCognome, etAnnoNascita, etNumTelefono, etCittà, etVia, etCivico, etCAP, etProvincia, etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth auth;
    private DatabaseReference userDatabaseReference, addressDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inizializzazione dei campi
        etNome = findViewById(R.id.etNome);
        etCognome = findViewById(R.id.etCognome);
        etAnnoNascita = findViewById(R.id.etAnnoNascita);
        etNumTelefono = findViewById(R.id.etNumTelefono);
        etCittà = findViewById(R.id.etCittà);
        etVia = findViewById(R.id.etVia);
        etCivico = findViewById(R.id.etCivico);
        etCAP = findViewById(R.id.etCAP);
        etProvincia = findViewById(R.id.etProvincia);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Firebase
        auth = FirebaseAuth.getInstance();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        addressDatabaseReference = FirebaseDatabase.getInstance().getReference("Addresses");

        // Bottone di Registrazione
        btnRegister.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        // Raccolta dati utente
        String nome = etNome.getText().toString();
        String cognome = etCognome.getText().toString();
        String annoNascita = etAnnoNascita.getText().toString();
        String numTelefono = etNumTelefono.getText().toString();
        String citta = etCittà.getText().toString();
        String via = etVia.getText().toString();
        String civico = etCivico.getText().toString();
        String CAP = etCAP.getText().toString();
        String provincia = etProvincia.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        // Controllo campi vuoti
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(cognome) || TextUtils.isEmpty(annoNascita) ||
                TextUtils.isEmpty(numTelefono) || TextUtils.isEmpty(citta) || TextUtils.isEmpty(via) ||
                TextUtils.isEmpty(civico) || TextUtils.isEmpty(CAP) || TextUtils.isEmpty(provincia) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creazione utente Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userID = auth.getCurrentUser().getUid();

                // Creazione e salvataggio del nodo Address
                String addressID = addressDatabaseReference.push().getKey();
                Address address = new Address(addressID, citta, via, civico, CAP, provincia);
                addressDatabaseReference.child(addressID).setValue(address).addOnCompleteListener(addressTask -> {
                    if (addressTask.isSuccessful()) {
                        // Creazione e salvataggio del nodo User
                        User user = new User(userID, nome, cognome, annoNascita, address, numTelefono);
                        userDatabaseReference.child(userID).setValue(user).addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                Toast.makeText(this, "Registrazione completata!", Toast.LENGTH_SHORT).show();

                                // Passa alla schermata di selezione del gruppo (SelectGroupActivity)
                                Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
                                intent.putExtra("userCity", citta); // Passa la città per raccomandare un gruppo
                                startActivity(intent); // Avvia la SelectGroupActivity
                                finish(); // Chiudi la schermata di registrazione
                            } else {
                                Toast.makeText(this, "Errore nel salvataggio dell'utente!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Errore nel salvataggio dell'indirizzo!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Registrazione fallita: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
