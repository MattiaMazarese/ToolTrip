package com.example.tooltrip;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
/*
    // DA CAMBIARE COMPLETAMENTE ERA SOLO PER PROVA
    private EditText etItemName, etItemDescription, etItemAvailability;
    private Button btnSaveItem;

    // Riferimento al database
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inizializza il riferimento al database
        mDatabase = FirebaseDatabase.getInstance().getReference("items");

        // Collegamento elementi grafici
        etItemName = findViewById(R.id.et_item_name);
        etItemDescription = findViewById(R.id.et_item_description);
        etItemAvailability = findViewById(R.id.et_item_availability);
        btnSaveItem = findViewById(R.id.btn_save_item);

        // Gestione salvataggio dati
        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItemToDatabase();
            }
        });
    }

    private void saveItemToDatabase() {
        String itemName = etItemName.getText().toString().trim();
        String itemDescription = etItemDescription.getText().toString().trim();
        String itemAvailability = etItemAvailability.getText().toString().trim();

        if (TextUtils.isEmpty(itemName)) {
            etItemName.setError("Inserisci il nome dell'oggetto");
            return;
        }

        if (TextUtils.isEmpty(itemDescription)) {
            etItemDescription.setError("Inserisci una descrizione");
            return;
        }

        if (TextUtils.isEmpty(itemAvailability)) {
            etItemAvailability.setError("Inserisci la disponibilità");
            return;
        }

        // Generazione ID unico per l'oggetto
        String itemId = mDatabase.push().getKey();

        // Creazione oggetto Item
        Item item = new Item(itemId, itemName, itemDescription, itemAvailability);

        // Salvataggio oggetto nel database
        mDatabase.child(itemId).setValue(item)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(HomeActivity.this, "Oggetto salvato con successo!", Toast.LENGTH_SHORT).show();
                        etItemName.setText("");
                        etItemDescription.setText("");
                        etItemAvailability.setText("");
                    } else {
                        Toast.makeText(HomeActivity.this, "Errore nel salvataggio: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }*/
}
