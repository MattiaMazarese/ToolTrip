package login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import menù.HomeActivity;
import com.example.tooltrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        // Controllo campi vuoti
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(cognome) || TextUtils.isEmpty(annoNascita) ||
                TextUtils.isEmpty(numTelefono) || TextUtils.isEmpty(citta) || TextUtils.isEmpty(via) ||
                TextUtils.isEmpty(civico) || TextUtils.isEmpty(CAP) || TextUtils.isEmpty(provincia) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Controllo specifici
        if (!annoNascita.matches("\\d{4}")) {
            etAnnoNascita.setError("Inserisci un anno valido (es: 1990)");
            return;
        }
        if (!numTelefono.matches("\\d{10}")) {
            etNumTelefono.setError("Inserisci un numero di telefono valido (10 cifre)");
            return;
        }
        if (!CAP.matches("\\d{5}")) {
            etCAP.setError("Inserisci un CAP valido (5 cifre)");
            return;
        }
        if (!civico.matches("\\d+")) {
            etCivico.setError("Inserisci un numero valido per il civico");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Inserisci un'email valida");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("La password deve avere almeno 6 caratteri");
            return;
        }

        // Creazione utente Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userID = auth.getCurrentUser().getUid();

                // Creazione e salvataggio del nodo Address
                String addressID = addressDatabaseReference.push().getKey();
                Address address = new Address(addressID, citta, via, civico, CAP, provincia);
                addressDatabaseReference.child(addressID).setValue(address).addOnCompleteListener(addressTask -> {
                    if (addressTask.isSuccessful()) {
                        // Creazione e salvataggio del nodo User
                        User user = new User(userID, nome, cognome, annoNascita, address, numTelefono);
                        userDatabaseReference.child(userID).setValue(user).addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                Toast.makeText(this, "Registrazione completata!", Toast.LENGTH_SHORT).show();

                                // Passa alla schermata di selezione del gruppo (SelectGroupActivity)
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                intent.putExtra("userCity", citta); // Passa la città per raccomandare un gruppo
                                startActivity(intent); // Avvia la SelectGroupActivity
                                finish(); // Chiudi la schermata di registrazione
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
}
