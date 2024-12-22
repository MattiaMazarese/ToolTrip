package group;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tooltrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class VisualizzaGruppoSingoloActivity extends AppCompatActivity {

    private TextView groupIDTextView;
    private TextView groupNameTextView;
    private TextView creatorIDTextView;
    private TextView membriTextView;
    private TextView codeTextView;
    private Button btnIscriviti;

    private DatabaseReference mDatabase;

    private String groupID,groupName,creatorID,code;
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
        btnIscriviti=findViewById(R.id.bottoneIscrizione);

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

        setButtonAction();
    }

    private void setButtonAction() {
        if(membri.contains(currentUserId())){
            btnIscriviti.setText("Disiscrivi");
            btnIscriviti.setOnClickListener(v ->iscrizione(true));
        }else{
            btnIscriviti.setText("Iscriviti");
            btnIscriviti.setOnClickListener(v ->iscrizione(false));
        }
    }

    private void iscrizione(boolean iscritto) {
        if(iscritto){
            membri.remove(currentUserId());
        }else {
            membri.add(currentUserId());
        }
        if(membri != null) {
            Toast.makeText(VisualizzaGruppoSingoloActivity.this, "entrato "+ membri.get(0), Toast.LENGTH_SHORT).show();
            /*mDatabase.child("Groups").child("membri").setValue(membri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(VisualizzaGruppoSingoloActivity.this, "Iscrizione added successfully", Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(VisualizzaGruppoSingoloActivity.this, "Failed to add iscrizione. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Failed to generate item ID", Toast.LENGTH_SHORT).show();

             */
        }


    }

    private String currentUserId(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
        String userID = currentUser.getUid();
        return userID;
    }
}
