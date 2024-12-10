package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private EditText editTextNome, editTextDescrizione, editTextCategoria;
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
        editTextCategoria = findViewById(R.id.editTextCategoria);
        switchPubblico = findViewById(R.id.switchPubblico);
        buttonAggiungi = findViewById(R.id.buttonAggiungi);

        buttonAggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToDatabase();
            }
        });
    }

    private void addItemToDatabase() {
        // Get values from input fields
        String nome = editTextNome.getText().toString().trim();
        String descrizione = editTextDescrizione.getText().toString().trim();
        String categoria = editTextCategoria.getText().toString().trim();
        boolean pubblico = switchPubblico.isChecked();

        // Validate input fields
        if (nome.isEmpty()) {
            // Show error if nome is empty
            Toast.makeText(this, "Nome is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (descrizione.isEmpty()) {
            // Show error if descrizione is empty
            Toast.makeText(this, "Descrizione is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (categoria.isEmpty()) {
            // Show error if categoria is empty
            Toast.makeText(this, "Categoria is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the currently authenticated user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case if there is no authenticated user
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a User object with the current user's ID (you can add other details if needed)
        String userID = currentUser.getUid();
        User possesore = new User(userID,null,null,null,null,null); // Pass the userID to your User object

        // Create a unique item ID (you can use UUID or Firebase generated IDs)
        String itemId = mDatabase.push().getKey();

        // Create a new Item object
        Item newItem = new Item(itemId, nome, descrizione, categoria, possesore, pubblico);

        // Save the item to the Firebase Realtime Database
        if (itemId != null) {
            mDatabase.child("items").child(itemId).setValue(newItem).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // If the operation is successful
                    Toast.makeText(AggiungiToolActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, clear fields after successful submission
                    clearFields();
                } else {
                    // If the operation failed
                    Toast.makeText(AggiungiToolActivity.this, "Failed to add item. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case if itemId is null
            Toast.makeText(this, "Failed to generate item ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        // Clear input fields after successful submission
        editTextNome.setText("");
        editTextDescrizione.setText("");
        editTextCategoria.setText("");
        switchPubblico.setChecked(false);
    }
}





