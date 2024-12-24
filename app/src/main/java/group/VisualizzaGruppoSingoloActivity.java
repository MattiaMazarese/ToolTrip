package group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class VisualizzaGruppoSingoloActivity extends AppCompatActivity {

    private TextView groupIDTextView;
    private TextView groupNameTextView;
    private TextView creatorIDTextView;
    private TextView membriTextView;
    private TextView codeTextView;
    private Button btnIscriviti;

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
        codeTextView.setText(code != null ? code : "N/A");

        View btnGroupChat = findViewById(R.id.btnGroupChat);
        
        // Imposta il listener per il pulsante "Chat di Gruppo"
        btnGroupChat.setOnClickListener(v -> {
            Intent intent = new Intent(VisualizzaGruppoSingoloActivity.this, GroupChatActivity.class);
            intent.putExtra("groupID", groupID); // Passa l'ID del gruppo
            startActivity(intent);
        });

        setButtonAction();


    }

    private void setButtonAction() {
        if (membri.contains(currentUserId())) {
            btnIscriviti.setText("Disiscrivi");
            btnIscriviti.setOnClickListener(v -> aggiornaIscrizione(true));
        } else {
            btnIscriviti.setText("Iscriviti");
            btnIscriviti.setOnClickListener(v -> aggiornaIscrizione(false));
        }
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
                // Torna alla schermata MyGroupActivity
                Intent intent = new Intent(VisualizzaGruppoSingoloActivity.this, MyGroupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Chiudi l'attività corrente
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
