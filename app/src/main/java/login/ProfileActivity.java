package login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;



import com.example.tooltrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import menù.MenuHandler;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtProfilo, txtNomeCognome, txtNumeroTelefono, txtIndirizzo, txtModifica;
    private Button btnLogout, btnSalva, btnModifica, btnDeleteAccount, btnModificaIcona;
    private EditText editTextTelefono, editTextVia, editTextCivico, editTextCitta, editTextProvincia, editTextCAP;
    private LinearLayout editLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ImageView imgIconaProfilo;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Collegamento delle TextView, EditText e Button dal layout
        txtProfilo = findViewById(R.id.txtProfilo);
        txtNomeCognome = findViewById(R.id.txtNomeCognome);
        txtNumeroTelefono = findViewById(R.id.txtNumeroTelefono);
        txtIndirizzo = findViewById(R.id.txtIndirizzo);
        txtModifica = findViewById(R.id.txtModifica);
        btnLogout = findViewById(R.id.btnLogout);
        btnSalva = findViewById(R.id.btnSalva);
        btnModifica = findViewById(R.id.btnModifica);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        editTextVia = findViewById(R.id.editTextIndirizzo);
        editTextCivico = findViewById(R.id.editTextCivico);
        editTextCitta = findViewById(R.id.editTextCitta);
        editTextProvincia = findViewById(R.id.editTextProvincia);
        editTextCAP = findViewById(R.id.editTextCAP);
        editLayout = findViewById(R.id.editLayout);

        txtProfilo.setText("Profilo");

        imgIconaProfilo = findViewById(R.id.imgIconaProfilo);

        btnModificaIcona = findViewById(R.id.btnModificaIcona);


        // Recupera la SharedPreferences
        SharedPreferences getPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String selectedIcon = getPreferences.getString("selectedIcon", "default");  // Default se non esiste

        // Imposta l'icona iniziale
        if ("Martello".equals(selectedIcon)) {
            imgIconaProfilo.setImageResource(R.drawable.hammer);
        } else if ("Pc".equals(selectedIcon)) {
            imgIconaProfilo.setImageResource(R.drawable.pc);
        } else if ("Scatola".equals(selectedIcon)) {
            imgIconaProfilo.setImageResource(R.drawable.box);
        } else {
            imgIconaProfilo.setImageResource(R.drawable.hammer);  // Imposta un'icona di default
        }


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId != null) {
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nome = dataSnapshot.child("nome").getValue(String.class);
                    String cognome = dataSnapshot.child("cognome").getValue(String.class);
                    String telefono = dataSnapshot.child("numTelefono").getValue(String.class);
                    String via = dataSnapshot.child("address").child("via").getValue(String.class);
                    String civico = dataSnapshot.child("address").child("civico").getValue(String.class);
                    String citta = dataSnapshot.child("address").child("citta").getValue(String.class);
                    String provincia = dataSnapshot.child("address").child("provincia").getValue(String.class);
                    String cap = dataSnapshot.child("address").child("cap").getValue(String.class);

                    // Imposta i dati nei TextView
                    txtNomeCognome.setText("Nome: " + (nome != null ? nome : "") + " Cognome: " + (cognome != null ? cognome : ""));
                    txtNumeroTelefono.setText("Telefono: " + (telefono != null ? telefono : "Non disponibile"));
                    txtIndirizzo.setText("Indirizzo: " + (via != null && civico != null && citta != null && provincia != null ? via + ", " + civico + ", " + citta + " (" + provincia + ") " + cap : "Non disponibile"));

                    // Imposta i dati nei campi EditText
                    editTextTelefono.setText(telefono != null ? telefono : "");
                    editTextVia.setText(via != null ? via : "");
                    editTextCivico.setText(civico != null ? civico : "");
                    editTextCitta.setText(citta != null ? citta : "");
                    editTextProvincia.setText(provincia != null ? provincia : "");
                    editTextCAP.setText(cap != null ? cap : "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    txtNomeCognome.setText("Errore nel caricamento del profilo.");
                    Log.e("Firebase", "Errore nel database: " + databaseError.getMessage());
                }
            });
        } else {
            txtNomeCognome.setText("Utente non autenticato.");
        }

        btnModificaIcona.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(ProfileActivity.this, btnModificaIcona);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.icon_selection_menu, popupMenu.getMenu());

            try {
                Field field = PopupMenu.class.getDeclaredField("mPopup");
                field.setAccessible(true);
                Object menuPopupHelper = field.get(popupMenu);
                Method setForceShowIcon = menuPopupHelper.getClass().getDeclaredMethod("setForceShowIcon", boolean.class);
                setForceShowIcon.invoke(menuPopupHelper, true);

                // Evita l'interferenza con il layout
                Method setOffset = menuPopupHelper.getClass().getDeclaredMethod("setOffset", int.class, int.class);
                setOffset.invoke(menuPopupHelper, 0, 0);

                // Set gravity to center
                Method setGravity = menuPopupHelper.getClass().getDeclaredMethod("setGravity", int.class);
                setGravity.invoke(menuPopupHelper, Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            } catch (Exception e) {
                e.printStackTrace();
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.iconHammer) {
                    imgIconaProfilo.setImageResource(R.drawable.hammer); // hammer.xml
                } else if (id == R.id.iconPC) {
                    imgIconaProfilo.setImageResource(R.drawable.pc); // pc.xml
                } else if (id == R.id.iconBox) {
                    imgIconaProfilo.setImageResource(R.drawable.box); // box.xml
                } else {
                    return false;
                }
                return true;
            });

            popupMenu.show();

        });






        btnModifica.setOnClickListener(v -> {
            if (editLayout.getVisibility() == View.GONE) {
                editLayout.setVisibility(View.VISIBLE);
                btnModifica.setText("Annulla Modifiche");
                btnModifica.setBackgroundTintList(ContextCompat.getColorStateList(ProfileActivity.this, R.color.deleteRed)); // Rosso
            } else {
                editLayout.setVisibility(View.GONE);
                btnModifica.setText("Modifica i tuoi dati");
                btnModifica.setBackgroundTintList(ContextCompat.getColorStateList(ProfileActivity.this, R.color.primary)); // Blu
            }
        });




        btnSalva.setOnClickListener(v -> {
            String nuovoTelefono = editTextTelefono.getText().toString();
            String nuovoCivico = editTextCivico.getText().toString();
            String nuovaCitta = editTextCitta.getText().toString();
            String nuovaProvincia = editTextProvincia.getText().toString();
            String nuovoCAP = editTextCAP.getText().toString();
            String nuovaVia = editTextVia.getText().toString();

            if (!nuovoTelefono.isEmpty() || (!nuovoCivico.isEmpty() && !nuovaCitta.isEmpty() && !nuovaProvincia.isEmpty() && !nuovoCAP.isEmpty() && !nuovaVia.isEmpty())) {
                if (userId != null) {
                    if (!nuovoTelefono.isEmpty()) {
                        mDatabase.child(userId).child("numTelefono").setValue(nuovoTelefono);
                        txtNumeroTelefono.setText("Telefono: " + nuovoTelefono);
                    }

                    if (!nuovoCivico.isEmpty() && !nuovaCitta.isEmpty() && !nuovaProvincia.isEmpty() && !nuovoCAP.isEmpty() && !nuovaVia.isEmpty()) {
                        mDatabase.child(userId).child("address").child("via").setValue(nuovaVia);
                        mDatabase.child(userId).child("address").child("civico").setValue(nuovoCivico);
                        mDatabase.child(userId).child("address").child("citta").setValue(nuovaCitta);
                        mDatabase.child(userId).child("address").child("provincia").setValue(nuovaProvincia);
                        mDatabase.child(userId).child("address").child("cap").setValue(nuovoCAP);

                        txtIndirizzo.setText("Indirizzo: " + nuovaVia + ", " + nuovoCivico + ", " + nuovaCitta + " (" + nuovaProvincia + ") " + nuovoCAP);
                    }

                    Toast.makeText(ProfileActivity.this, "Modifiche salvate!", Toast.LENGTH_SHORT).show();
                    editLayout.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Per favore, inserisci almeno un dato da modificare.", Toast.LENGTH_SHORT).show();
            }
            btnModifica.setText("Modifica");
            btnModifica.setBackgroundTintList(ContextCompat.getColorStateList(ProfileActivity.this, R.color.primary));
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            if (userId != null) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Conferma Eliminazione Account")
                        .setMessage("Sei sicuro di voler cancellare il tuo account? Questa operazione è irreversibile.")
                        .setPositiveButton("Elimina", (dialog, which) -> {
                            mDatabase.child(userId).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    mAuth.getCurrentUser().delete().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Account eliminato con successo", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });
                        })
                        .setNegativeButton("Annulla", null)
                        .show();
            }
        });

        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );
    }
}