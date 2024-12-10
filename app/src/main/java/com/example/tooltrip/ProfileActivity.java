package com.example.tooltrip;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private Button btnLogout;

    @SuppressLint("SetTextI18n")
    // Inizializza il bottone Logout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView txtProfilo = findViewById(R.id.txtProfilo);
        txtProfilo.setText("Profilo");


        btnLogout = findViewById(R.id.btnLogout);
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