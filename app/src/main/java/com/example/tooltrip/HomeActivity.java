package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnLogout;
    private Button btnAggiungiTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inizializzazione di Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Troviamo il bottone di logout
        btnLogout = findViewById(R.id.btnLogout);
        btnAggiungiTool= findViewById(R.id.btnAggiungiTool);

        // Impostiamo il listener per il bottone
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eseguiamo il logout
                mAuth.signOut();
                // Torniamo alla LoginActivity dopo il logout
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish(); // Evita che l'utente ritorni alla Home
            }
        });

        btnAggiungiTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AggiungiToolActivity.class));
                finish();
            }
        });
    }
}
