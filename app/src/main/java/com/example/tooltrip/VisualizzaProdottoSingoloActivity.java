package com.example.tooltrip;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class VisualizzaProdottoSingoloActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodotto_singolo);

        TextView textViewNome = findViewById(R.id.textViewNome);
        TextView textViewDescrizione = findViewById(R.id.textViewDescrizione);
        TextView textViewCategoria = findViewById(R.id.textViewCategoria);

        // Ottieni i dati passati dall'intent
        String nome = getIntent().getStringExtra("itemNome");
        String descrizione = getIntent().getStringExtra("itemDescrizione");
        String categoria = getIntent().getStringExtra("itemCategoria");

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
}

