package group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tooltrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import menù.MenuHandler;

public class VisualizzaGruppoSingoloActivity extends AppCompatActivity {

    private TextView groupIDTextView;
    private TextView groupNameTextView;
    private TextView creatorIDTextView;
    private TextView membriTextView;
    private TextView codeTextView;
    private Button btnIscriviti;
    private View btnGroupChat; // Aggiunto come variabile di classe

    private DatabaseReference mDatabase;

    private String groupID, groupName, creatorID, code;
    private List<String> membri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_gruppo);

        // Collegamento con gli elementi del layout
        groupIDTextView = findViewById(R.id.tvGroupIDValue);
        groupNameTextView = findViewById(R.id.tvGroupNameValue);
        creatorIDTextView = findViewById(R.id.tvCreatoreIDValue);
        membriTextView = findViewById(R.id.tvMembriValue);
        codeTextView = findViewById(R.id.tvCodiceValue);
        btnIscriviti = findViewById(R.id.bottoneIscrizione);
        btnGroupChat = findViewById(R.id.btnGroupChat); // Collegamento

        mDatabase = FirebaseDatabase.getInstance().getReference("Groups");

        // Recupera i dati passati tramite l'Intent
        groupID = getIntent().getStringExtra("groupID");
        groupName = getIntent().getStringExtra("groupNome");
        creatorID = getIntent().getStringExtra("creatoreID");
        membri = getIntent().getStringArrayListExtra("membri");
        code = getIntent().getStringExtra("codice");

        // Imposta i dati nei TextView
        groupIDTextView.setText(groupID != null ? groupID : "N/A");
        groupNameTextView.setText(groupName != null ? groupName : "N/A");
        creatorIDTextView.setText(creatorID != null ? creatorID : "N/A");
        membriTextView.setText(membri != null ? membri.toString() : "N/A");
        codeTextView.setVisibility(View.GONE);

        // Listener sempre attivo per il pulsante "Chat di Gruppo"
        btnGroupChat.setOnClickListener(v -> {
            if (membri != null && membri.contains(currentUserId())) {
                // Apri la chat del gruppo solo se l'utente è iscritto
                Intent intent = new Intent(VisualizzaGruppoSingoloActivity.this, GroupChatActivity.class);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
            } else {
                // Mostra un messaggio se l'utente non è iscritto
                Toast.makeText(this, "Devi iscriverti al gruppo per accedere alla chat.", Toast.LENGTH_SHORT).show();
            }
        });

        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );

        // Imposta il comportamento dei bottoni
        setButtonAction();
    }

    private void setButtonAction() {
        boolean isUserMember = membri != null && membri.contains(currentUserId());

        if (isUserMember) {
            btnIscriviti.setText("Disiscrivi");
            btnIscriviti.setOnClickListener(v -> aggiornaIscrizione(true));
            codeTextView.setVisibility(View.VISIBLE);
            codeTextView.setText(code != null ? code : "N/A");
        } else {
            btnIscriviti.setText("Iscriviti");
            btnIscriviti.setOnClickListener(v -> {
                if (code != null && !code.isEmpty()) {
                    showCodeInputDialog(); // Mostra il dialogo per inserire il codice
                } else {
                    aggiornaIscrizione(false); // Iscriviti direttamente se non c'è codice
                }
            });
            codeTextView.setVisibility(View.GONE);
        }

        // Aggiorna lo stato visivo del pulsante "Chat di Gruppo"
        btnGroupChat.setEnabled(isUserMember);
    }

    private void showCodeInputDialog() {
        // Crea il layout per l'input del codice
        final EditText inputCode = new EditText(this);
        inputCode.setHint("Inserisci il codice del gruppo");

        // Crea il dialogo
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Codice del Gruppo")
                .setMessage("Questo gruppo richiede un codice per l'iscrizione.")
                .setView(inputCode)
                .setPositiveButton("Conferma", (dialog, which) -> {
                    String enteredCode = inputCode.getText().toString().trim();

                    if (enteredCode.equals(code)) {
                        aggiornaIscrizione(false); // Iscriviti se il codice è corretto
                    } else {
                        Toast.makeText(this, "Codice non corretto. Riprova.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .show();
    }




    private void aggiornaIscrizione(boolean iscritto) {
        String userId = currentUserId();

        if (userId == null) {
            Toast.makeText(this, "Utente non autenticato. Effettua il login.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (iscritto) {
            membri.remove(userId);
            Toast.makeText(this, "Hai annullato l'iscrizione al gruppo", Toast.LENGTH_SHORT).show();
        } else {
            membri.add(userId);
            Toast.makeText(this, "Ora sei iscritto al gruppo", Toast.LENGTH_SHORT).show();
        }

        mDatabase.child(groupID).child("membri").setValue(membri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(VisualizzaGruppoSingoloActivity.this, MyGroupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Errore durante l'aggiornamento dell'iscrizione.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String currentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utente non autenticato", Toast.LENGTH_SHORT).show();
            return null;
        }
        return currentUser.getUid();
    }
}
