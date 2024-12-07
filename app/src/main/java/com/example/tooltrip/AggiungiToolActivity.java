package com.example.tooltrip;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AggiungiToolActivity extends AppCompatActivity {

    private EditText etNome, etDescrizione, etCategoria;
    private Switch switchPubblico;
    private Button btnSalvaItem;

    private DatabaseReference mDatabase;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgPreview;
    private Uri imageUri; // Per memorizzare l'URI dell'immagine selezionata

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_tool);


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

        imgPreview = findViewById(R.id.img_preview);
        Button btnSelezionaImmagine = findViewById(R.id.btn_seleziona_immagine);

        btnSelezionaImmagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaImmagine();
            }
        });
    }

    private void selezionaImmagine() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private void salvaItemNelDatabase() {
        String nome = etNome.getText().toString().trim();
        String descrizione = etDescrizione.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        boolean pubblico = switchPubblico.isChecked();

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

        String imagePath = imageUri != null ? imageUri.toString() : null; // Percorso immagine

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Verifica che l'utente sia autenticato
        if (currentUser != null) {
            String userID = currentUser.getUid();
            // Crea un oggetto User con i dati dell'utente
            User possessore = new User(userID, null, null, null, null, null);

            // Crea un ID per il nuovo item
            String itemId = mDatabase.push().getKey();

            // Crea l'oggetto Item da salvare
            Item nuovoItem = new Item(itemId, nome, descrizione, categoria, possessore, imagePath, pubblico);

            // Salva l'oggetto Item nel database
            mDatabase.child(itemId).setValue(nuovoItem)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AggiungiToolActivity.this, "Oggetto salvato con successo!", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(AggiungiToolActivity.this, "Errore nel salvataggio: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Utente non autenticato", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgPreview.setImageBitmap(bitmap); // Mostra l'immagine nell'ImageView
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Errore nel caricamento dell'immagine", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearFields() {
        etNome.setText("");
        etDescrizione.setText("");
        etCategoria.setText("");
        switchPubblico.setChecked(false);
        imgPreview.setImageDrawable(null);
        imageUri = null;
    }
}
