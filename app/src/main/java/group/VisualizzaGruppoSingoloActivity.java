package group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tooltrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import menù.MenuHandler;
import item.Item;
import item.ToolAdapter;

public class VisualizzaGruppoSingoloActivity extends AppCompatActivity {

    private TextView tvGroupNameTitle;
    private ListView listViewMembri;
    private Button btnIscriviti;
    private Button btnGroupChat;

    private RecyclerView recyclerViewGroupTools;
    private ToolAdapter toolAdapter;
    private List<Item> groupToolList;
    private DatabaseReference toolDatabase;

    private DatabaseReference mDatabase;

    private String groupID, groupName, creatorID, code;
    private List<String> membri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_gruppo);

        // Initialize views
        tvGroupNameTitle = findViewById(R.id.tvGroupNameTitle);
        listViewMembri = findViewById(R.id.listViewMembri);
        btnIscriviti = findViewById(R.id.bottoneIscrizione);
        btnGroupChat = findViewById(R.id.btnGroupChat);

        // RecyclerView for group tools
        recyclerViewGroupTools = findViewById(R.id.recyclerViewGroupTools);
        recyclerViewGroupTools.setLayoutManager(new LinearLayoutManager(this));
        groupToolList = new ArrayList<>();
        toolAdapter = new ToolAdapter(groupToolList);
        recyclerViewGroupTools.setAdapter(toolAdapter);

        toolDatabase = FirebaseDatabase.getInstance().getReference("items");

        mDatabase = FirebaseDatabase.getInstance().getReference("Groups");

        // Retrieve data passed via Intent
        groupID = getIntent().getStringExtra("groupID");
        groupName = getIntent().getStringExtra("groupNome");
        creatorID = getIntent().getStringExtra("creatoreID");
        membri = getIntent().getStringArrayListExtra("membri");
        code = getIntent().getStringExtra("codice");

        // Set group name title
        tvGroupNameTitle.setText(groupName != null ? groupName : "Nome del Gruppo");

        // Load group members into ListView
        loadGroupMembers();

        // Load group tools
        loadGroupTools();

        MenuHandler menuHandler = new MenuHandler(this,"viewTool");
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );




        // Set button actions
        setButtonAction();
    }

    private void loadGroupMembers() {
        if (membri != null && !membri.isEmpty()) {
            List<Member> memberList = new ArrayList<>();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

            for (String memberId : membri) {
                usersRef.child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String name = snapshot.child("nome").getValue(String.class);
                        String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                        Member member = new Member(name != null ? name : "Utente sconosciuto", profileImageUrl);
                        memberList.add(member);

                        // Update the ListView adapter
                        MemberAdapter adapter = new MemberAdapter(VisualizzaGruppoSingoloActivity.this, memberList);
                        listViewMembri.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle error
                    }
                });
            }
        } else {
            // No members found
            List<Member> noMembers = new ArrayList<>();
            noMembers.add(new Member("Nessun membro nel gruppo.", null));
            MemberAdapter adapter = new MemberAdapter(this, noMembers);
            listViewMembri.setAdapter(adapter);
        }
    }



    private void loadGroupTools() {
        toolDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                groupToolList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = dataSnapshot.getValue(Item.class);
                    if (item != null && item.isPubblico() && item.getVisualizzaSoloGruppi()) {
                        groupToolList.add(item);
                    }
                }
                toolAdapter.notifyDataSetChanged();

                // Show or hide the tools section based on the availability
                TextView tvToolsLabel = findViewById(R.id.tvToolsLabel);
                if (!groupToolList.isEmpty()) {
                    tvToolsLabel.setVisibility(View.VISIBLE);
                    recyclerViewGroupTools.setVisibility(View.VISIBLE);
                } else {
                    tvToolsLabel.setVisibility(View.GONE);
                    recyclerViewGroupTools.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(VisualizzaGruppoSingoloActivity.this,
                        "Errore nel caricamento dei tool.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setButtonAction() {
        boolean isUserMember = membri != null && membri.contains(currentUserId());

        if (isUserMember) {
            btnIscriviti.setText("Disiscriviti");
            btnIscriviti.setOnClickListener(v -> aggiornaIscrizione(true));
        } else {
            btnIscriviti.setText("Iscriviti");
            btnIscriviti.setOnClickListener(v -> {
                if (code != null && !code.isEmpty()) {
                    showCodeInputDialog(); // Show dialog to enter the code
                } else {
                    aggiornaIscrizione(false); // Directly subscribe if there's no code
                }
            });
        }

        // Set the "Chat di Gruppo" button
        btnGroupChat.setOnClickListener(v -> {
            if (isUserMember) {
                // Open group chat
                Intent intent = new Intent(VisualizzaGruppoSingoloActivity.this, GroupChatActivity.class);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Devi iscriverti al gruppo per accedere alla chat.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCodeInputDialog() {
        // Create the input dialog for the group code
        final EditText inputCode = new EditText(this);
        inputCode.setHint("Inserisci il codice del gruppo");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Codice del Gruppo")
                .setMessage("Questo gruppo richiede un codice per l'iscrizione.")
                .setView(inputCode)
                .setPositiveButton("Conferma", (dialog, which) -> {
                    String enteredCode = inputCode.getText().toString().trim();

                    if (enteredCode.equals(code)) {
                        aggiornaIscrizione(false); // Subscribe if the code is correct
                    } else {
                        Toast.makeText(this, "Codice non corretto. Riprova.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void aggiornaIscrizione(boolean isDisiscriviti) {
        String userId = currentUserId();

        if (userId == null) {
            Toast.makeText(this, "Utente non autenticato. Effettua il login.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isDisiscriviti) {
            membri.remove(userId);
            Toast.makeText(this, "Hai annullato l'iscrizione al gruppo", Toast.LENGTH_SHORT).show();
        } else {
            membri.add(userId);
            Toast.makeText(this, "Ora sei iscritto al gruppo", Toast.LENGTH_SHORT).show();
        }

        mDatabase.child(groupID).child("membri").setValue(membri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update the UI
                setButtonAction();
                loadGroupMembers();
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
