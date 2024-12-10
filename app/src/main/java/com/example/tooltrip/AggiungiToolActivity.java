package com.example.tooltrip;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AggiungiToolActivity extends AppCompatActivity {
    private EditText editTextNome, editTextDescrizione;
    private AutoCompleteTextView autoCompleteCategoria;
    private Switch switchPubblico;
    private Button buttonAggiungi;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_tool);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        editTextNome = findViewById(R.id.editTextNome);
        editTextDescrizione = findViewById(R.id.editTextDescrizione);
        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria);
        switchPubblico = findViewById(R.id.switchPubblico);
        buttonAggiungi = findViewById(R.id.buttonAggiungi);

        // Set up the dropdown menu
        String[] categorie = {"Elettronica", "Meccanica", "Informatica", "Altro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, categorie);
        autoCompleteCategoria.setAdapter(adapter);

        // Force the dropdown to show on click
        autoCompleteCategoria.setOnClickListener(v -> autoCompleteCategoria.showDropDown());

        // Force the dropdown to show on focus change as well
        autoCompleteCategoria.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteCategoria.showDropDown();
            }
        });

        // Set click listener for the button
        buttonAggiungi.setOnClickListener(v -> addItemToDatabase());
    }

    private void addItemToDatabase() {
        // Get values from input fields
        String nome = editTextNome.getText().toString().trim();
        String descrizione = editTextDescrizione.getText().toString().trim();
        String categoria = autoCompleteCategoria.getText().toString().trim();
        boolean pubblico = switchPubblico.isChecked();

        // Validate input fields
        if (nome.isEmpty()) {
            Toast.makeText(this, "Nome is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (descrizione.isEmpty()) {
            Toast.makeText(this, "Descrizione is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (categoria.isEmpty()) {
            Toast.makeText(this, "Categoria is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the currently authenticated user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a User object with the current user's ID
        String userID = currentUser.getUid();
        User possesore = new User(userID, null, null, null, null, null);

        // Create a unique item ID
        String itemId = mDatabase.push().getKey();

        // Create a new Item object
        Item newItem = new Item(itemId, nome, descrizione, categoria, possesore, pubblico);

        // Save the item to Firebase Realtime Database
        if (itemId != null) {
            mDatabase.child("items").child(itemId).setValue(newItem).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AggiungiToolActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(AggiungiToolActivity.this, "Failed to add item. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Failed to generate item ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextNome.setText("");
        editTextDescrizione.setText("");
        autoCompleteCategoria.setText("");
        switchPubblico.setChecked(false);
    }
}







