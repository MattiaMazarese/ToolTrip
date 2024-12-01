package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inizializzazione FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Collegamento elementi grafici
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);

        // Gestione pulsante di registrazione
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Controllo dei campi
                if (TextUtils.isEmpty(name)) {
                    etName.setError("Inserisci il nome");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Inserisci l'email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Inserisci la password");
                    return;
                }
                if (password.length() < 6) {
                    etPassword.setError("La password deve contenere almeno 6 caratteri");
                    return;
                }

                // Registrazione utente
                registerUser(name, email, password);
            }
        });
    }

    // Metodo per registrare un utente
    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Ottieni ID utente
                            String userId = user.getUid();

                            // Salva i dati dell'utente nel database
                            saveUserData(userId, name, email);

                            // Passa alla HomeActivity
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();
                        }
                    } else {
                        // Gestione degli errori
                        Toast.makeText(RegisterActivity.this, "Errore: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Metodo per salvare i dati dell'utente nel Realtime Database
    private void saveUserData(String userId, String name, String email) {
        // Ottieni riferimento al database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // Crea un oggetto utente
        User user = new User(name, email);

        // Salva i dati
        usersRef.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("RealtimeDB", "Dati salvati con successo!");
                    } else {
                        Log.e("RealtimeDB", "Errore nel salvataggio dei dati.", task.getException());
                    }
                });
    }

    // Classe per rappresentare un utente
    public static class User {
        public String name;
        public String email;

        public User() {
            // Costruttore vuoto richiesto da Firebase
        }

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
