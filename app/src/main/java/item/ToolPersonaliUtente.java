package item;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import menù.MenuHandler;
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

public class ToolPersonaliUtente extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private ToolAdapter toolAdapter;
    private ToolAdapter toolAdapter2;
    private List<Item> itemList;
    private List<Item> itemList2;

    private List<String> idOggettiPresi;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_personali_utente);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        itemList2 = new ArrayList<>();

        idOggettiPresi = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("Prestito");

        toolAdapter = new ToolAdapter(itemList);
        toolAdapter2 = new ToolAdapter(itemList2);

        recyclerView.setAdapter(toolAdapter);
        recyclerView2.setAdapter(toolAdapter2);

        trovaOggettiPresiInPrestito();

        mDatabase = FirebaseDatabase.getInstance().getReference("items");
        loadItemsFromDatabase();

        // Set up the menu listeners using MenuHandler
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );

    }

    private void loadItemsFromDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(ToolPersonaliUtente.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
                String userID = currentUser.getUid();

                itemList.clear(); // Clear previous data
                itemList2.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null && Objects.equals(item.getPossesore().getUserID(), userID)) {
                        itemList.add(item);
                    }
                    if (item != null && idOggettiPresi.contains(item.getItemId())) {
                        itemList2.add(item);
                    }
                }
                toolAdapter.notifyDataSetChanged();
                toolAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ToolPersonaliUtente.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void trovaOggettiPresiInPrestito() {
        DatabaseReference prestitoRef = FirebaseDatabase.getInstance().getReference("Prestito");
        prestitoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(ToolPersonaliUtente.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                    return; // Interrompe il metodo se l'utente non è autenticato
                }

                String userID = currentUser.getUid();
                idOggettiPresi.clear(); // Svuota la lista prima di aggiungere nuovi dati

                // Itera attraverso i nodi figli di "Prestito"
                for (DataSnapshot prestitoSnapshot : dataSnapshot.getChildren()) {
                    String idUtente = prestitoSnapshot.child("idUtente").getValue(String.class);
                    String idOggetto = prestitoSnapshot.child("idOggetto").getValue(String.class);

                    if (idUtente != null && idOggetto != null && idUtente.equals(userID)) {
                        idOggettiPresi.add(idOggetto);
                    }
                }

                loadItemsFromDatabase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ToolPersonaliUtente.this, "Failed to load Prestito data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

