package com.example.tooltrip;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AggiungiItemActivity extends AppCompatActivity {

  /*  private EditText etNome, etDescrizione, etCategoria;
    private Switch switchPubblico;
    private Button btnSalvaItem;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_item);

        // Collegamento a Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("items");

        // Collegamento agli elementi della UI
        etNome = findViewById(R.id.et_nome);
        etDescrizione = findViewById(R.id.et_descrizione);
        etCategoria = findViewById(R.id.et_categoria);
        switchPubblico = findViewById(R.id.switch_pubblico);
        btnSalvaItem = findViewById(R.id.btn_salva_item);

        // Gestione del pulsante di salvataggio
        btnSalvaItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvaItemNelDatabase();
            }
        });
    }

    private void salvaItemNelDatabase() {
        String nome = etNome.getText().toString().trim();
        String descrizione = etDescrizione.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        boolean pubblico = switchPubblico.isChecked();

        // Validazione dei campi
        if (TextUtils.isEmpty(nome)) {
            etNome.setError("Inserisci il nome");
            return;
        }
        if (TextUtils.isEmpty(descrizione)) {
            etDescrizione.setError("Inserisci una descrizione");
            return;
        }
        if (TextUtils.isEmpty(categoria)) {
            etCategoria.setError("Inserisci una categoria");
            return;
        }

        // Generazione ID univoco per l'oggetto
        String itemId = mDatabase.push().getKey();

        // Creazione di un oggetto Item
        Item nuovoItem = new Item(itemId, nome, descrizione, categoria, null, pubblico);

        // Salvataggio nel database
        mDatabase.child(itemId).setValue(nuovoItem)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AggiungiItemActivity.this, "Oggetto salvato con successo!", Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Toast.makeText(AggiungiItemActivity.this, "Errore nel salvataggio: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void clearFields() {
        etNome.setText("");
        etDescrizione.setText("");
        etCategoria.setText("");
        switchPubblico.setChecked(false);
    }*/
}
