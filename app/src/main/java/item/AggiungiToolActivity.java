package item;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import menù.MenuHandler;
import com.example.tooltrip.R;
import login.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AggiungiToolActivity extends AppCompatActivity {
    private EditText editTextNome, editTextDescrizione;
    private AutoCompleteTextView autoCompleteCategoria;
    private Switch switchPubblico,switchVisualizzaGruppi;
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
        switchVisualizzaGruppi = findViewById(R.id.switchVisualizzazioneGruppi);

        // Load categories from database
        loadCategoriesFromDatabase();

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

        // Configurazione del menu tramite MenuHandler
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );

    }

    private void loadCategoriesFromDatabase() {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories");
        categoryRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> categorie = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null && category.getNome() != null) {
                        categorie.add(category.getNome());
                    }
                }

                // Set up the adapter for the AutoCompleteTextView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, categorie);
                autoCompleteCategoria.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemToDatabase() {
        // Get values from input fields
        String nome = editTextNome.getText().toString().trim();
        String descrizione = editTextDescrizione.getText().toString().trim();
        String categoriaNome = autoCompleteCategoria.getText().toString().trim();
        boolean pubblico = switchPubblico.isChecked();
        boolean visualizzazioneGruppo=switchVisualizzaGruppi.isChecked();

        // Validate input fields
        if (nome.isEmpty()) {
            Toast.makeText(this, "Nome is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (descrizione.isEmpty()) {
            Toast.makeText(this, "Descrizione is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (categoriaNome.isEmpty()) {
            Toast.makeText(this, "Categoria is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the currently authenticated user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Map categoriaNome -> categoriaId
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories");
        categoryRef.orderByChild("nome").equalTo(categoriaNome).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String categoriaId = null;
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    categoriaId = snapshot.getKey();
                    break;
                }

                if (categoriaId != null) {
                    // Create a User object with the current user's ID
                    String userID = currentUser.getUid();
                    User possesore = new User(userID, null, null, null, null, null);

                    // Create a unique item ID
                    String itemId = mDatabase.push().getKey();

                    // Create a new Item object
                    Item newItem = new Item(itemId, nome, descrizione, categoriaId, possesore, pubblico,visualizzazioneGruppo);

                    // Save the item to Firebase Realtime Database
                    if (itemId != null) {
                        mDatabase.child("items").child(itemId).setValue(newItem).addOnCompleteListener(itemTask -> {
                            if (itemTask.isSuccessful()) {
                                Toast.makeText(AggiungiToolActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                                clearFields();
                            } else {
                                Toast.makeText(AggiungiToolActivity.this, "Failed to add item. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Failed to generate item ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Invalid category selected.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Category does not exist.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        editTextNome.setText("");
        editTextDescrizione.setText("");
        autoCompleteCategoria.setText("");
        switchPubblico.setChecked(false);
    }


}
