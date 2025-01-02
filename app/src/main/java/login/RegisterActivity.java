package login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import group.Group;
import menù.HomeActivity;
import com.example.tooltrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import group.Address;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNome, etCognome, etAnnoNascita, etNumTelefono, etCittà, etVia, etCivico, etCAP, etProvincia, etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth auth;
    private DatabaseReference userDatabaseReference, addressDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inizializzazione dei campi
        etNome = findViewById(R.id.etNome);
        etCognome = findViewById(R.id.etCognome);
        etAnnoNascita = findViewById(R.id.etAnnoNascita);
        etNumTelefono = findViewById(R.id.etNumTelefono);
        etCittà = findViewById(R.id.etCittà);
        etVia = findViewById(R.id.etVia);
        etCivico = findViewById(R.id.etCivico);
        etCAP = findViewById(R.id.etCAP);
        etProvincia = findViewById(R.id.etProvincia);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Firebase
        auth = FirebaseAuth.getInstance();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        addressDatabaseReference = FirebaseDatabase.getInstance().getReference("Addresses");

        // Bottone di Registrazione
        btnRegister.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String nome = etNome.getText().toString().trim();
        String cognome = etCognome.getText().toString().trim();
        String annoNascita = etAnnoNascita.getText().toString().trim();
        String numTelefono = etNumTelefono.getText().toString().trim();
        String citta = etCittà.getText().toString().trim();
        String via = etVia.getText().toString().trim();
        String civico = etCivico.getText().toString().trim();
        String CAP = etCAP.getText().toString().trim();
        String provincia = etProvincia.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Controllo campi vuoti e validità
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(cognome) || TextUtils.isEmpty(annoNascita) ||
                TextUtils.isEmpty(numTelefono) || TextUtils.isEmpty(citta) || TextUtils.isEmpty(via) ||
                TextUtils.isEmpty(civico) || TextUtils.isEmpty(CAP) || TextUtils.isEmpty(provincia) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Altri controlli omessi per brevità...

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userID = auth.getCurrentUser().getUid();

                // Salva l'indirizzo
                String addressID = addressDatabaseReference.push().getKey();
                Address address = new Address(addressID, citta, via, civico, CAP, provincia);
                addressDatabaseReference.child(addressID).setValue(address).addOnCompleteListener(addressTask -> {
                    if (addressTask.isSuccessful()) {
                        // Salva l'utente
                        User user = new User(userID, nome, cognome, annoNascita, address, numTelefono);
                        userDatabaseReference.child(userID).setValue(user).addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                // Gestione gruppo pubblico della città
                                handleCityGroup(citta, userID);

                                Toast.makeText(this, "Registrazione completata!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Errore nel salvataggio dell'utente!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Errore nel salvataggio dell'indirizzo!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Registrazione fallita: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleCityGroup(String cityName, String userID) {
        DatabaseReference groupDatabase = FirebaseDatabase.getInstance().getReference("Groups");

        groupDatabase.orderByChild("nome").equalTo(cityName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Gruppo già esistente
                    for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                        Group existingGroup = groupSnapshot.getValue(Group.class);
                        if (existingGroup != null && existingGroup.getCodice() == null) { // Solo gruppi pubblici
                            existingGroup.aggiungiMembro(userID);
                            groupDatabase.child(existingGroup.getGroupID()).setValue(existingGroup);
                            break;
                        }
                    }
                } else {
                    // Crea un nuovo gruppo pubblico
                    String groupID = groupDatabase.push().getKey();
                    Group newGroup = new Group(groupID, cityName, userID);
                    groupDatabase.child(groupID).setValue(newGroup);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Errore durante la gestione del gruppo della città.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
