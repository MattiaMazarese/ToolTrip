package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    /*private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inizializzazione FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Collegamento elementi grafici
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        // Gestione pulsante di accesso
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        // Gestione pulsante di registrazione
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Inserisci l'email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Inserisci la password");
            return;
        }

        // Autenticazione utente
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Accesso riuscito
                        Toast.makeText(LoginActivity.this, "Accesso effettuato!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        // Accesso fallito
                        Toast.makeText(LoginActivity.this, "Errore di accesso: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setupButtonAnimation(Button button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.98f).scaleY(0.98f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }*/
    // Variabili per i campi email e password e i pulsanti
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;

    // Riferimento a FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inizializza FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Collegamento dei campi di input e dei pulsanti
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        // Gestione del click sul pulsante di login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        // Gestione del click sul pulsante di registrazione
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Avvia l'attività di registrazione
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        // Ottieni l'email e la password dai campi di input
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Verifica che i campi non siano vuoti
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Inserisci l'email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Inserisci la password");
            return;
        }

        // Esegui l'autenticazione con Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login riuscito
                        Toast.makeText(LoginActivity.this, "Accesso effettuato!", Toast.LENGTH_SHORT).show();
                        // Avvia l'attività HomeActivity (ad esempio la home dell'app)
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        // Login fallito
                        Toast.makeText(LoginActivity.this, "Errore di accesso: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}

