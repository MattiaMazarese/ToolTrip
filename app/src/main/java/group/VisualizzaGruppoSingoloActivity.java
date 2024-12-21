package group;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tooltrip.R;

import java.util.List;

public class VisualizzaGruppoSingoloActivity extends AppCompatActivity {

    private TextView groupIDTextView;
    private TextView groupNameTextView;
    private TextView creatorIDTextView;
    private TextView membriTextView;
    private TextView codeTextView;

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

        // Recupera i dati passati tramite l'Intent
        String groupID = getIntent().getStringExtra("groupID");
        String groupName = getIntent().getStringExtra("groupNome");
        String creatorID = getIntent().getStringExtra("creatoreID");
        List<String> membri = getIntent().getStringArrayListExtra("membri");
        String code = getIntent().getStringExtra("codice");

        // Imposta i dati nei TextView
        groupIDTextView.setText(groupID != null ? groupID : "N/A");
        groupNameTextView.setText(groupName != null ? groupName : "N/A");
        creatorIDTextView.setText(creatorID != null ? creatorID : "N/A");
        membriTextView.setText(membri != null ? membri.toString() : "N/A");
        codeTextView.setText(code != null ? code : "N/A");
    }
}
