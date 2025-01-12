package group;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tooltrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import menù.MenuHandler;

public class MyGroupActivity extends AppCompatActivity {

    private Button creaGruppo;
    private Button cercaGruppo;
    private RecyclerView cardMieiGruppi;
    private RecyclerView cardGruppiAggiunti;
    private List<Group> mieiGroupCreatiList, mieiGroupAggiuntiList;
    private GroupAdapter groupAdapterCreati, groupAdapterAggiunti;
    private String currentUserID;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        // Collegamenti ai pulsanti
        creaGruppo = findViewById(R.id.btn_crea_gruppo);
        cercaGruppo = findViewById(R.id.btn_cerca_gruppo);

        // Navigazione
        creaGruppo.setOnClickListener(v -> {
            Intent intent = new Intent(MyGroupActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        });

        cercaGruppo.setOnClickListener(v -> {
            Intent intent = new Intent(MyGroupActivity.this, SearchGroupActivity.class);
            startActivity(intent);
        });

        // Imposta RecyclerView per i gruppi creati
        cardMieiGruppi = findViewById(R.id.recycler_miei_gruppi);
        cardMieiGruppi.setLayoutManager(new LinearLayoutManager(this));

        // Imposta RecyclerView per i gruppi aggiunti
        cardGruppiAggiunti = findViewById(R.id.recycler_gruppi_aggiunti);
        cardGruppiAggiunti.setLayoutManager(new LinearLayoutManager(this));

        // Inizializzazione delle liste
        mieiGroupCreatiList = new ArrayList<>();
        mieiGroupAggiuntiList = new ArrayList<>();

        // Collegamento al database Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("Groups");

        // Adapter per RecyclerView
        groupAdapterCreati = new GroupAdapter(mieiGroupCreatiList);
        groupAdapterAggiunti = new GroupAdapter(mieiGroupAggiuntiList);

        cardMieiGruppi.setAdapter(groupAdapterCreati);
        cardGruppiAggiunti.setAdapter(groupAdapterAggiunti);

        // Autenticazione utente
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        currentUserID = currentUser.getUid();

        // Carica dati dal database
        loadItemsFromDatabase();

        // Configura il menu
        setUpMenu();
    }

    private void loadItemsFromDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mieiGroupCreatiList.clear();
                mieiGroupAggiuntiList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group != null && Objects.equals(group.getCreatoreID(), currentUserID)) {
                        mieiGroupCreatiList.add(group);
                    }
                    if (group != null && group.getMembri().contains(currentUserID)) {
                        mieiGroupAggiuntiList.add(group);
                    }
                }
                groupAdapterCreati.notifyDataSetChanged();
                groupAdapterAggiunti.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyGroupActivity.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpMenu() {
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );
    }
}