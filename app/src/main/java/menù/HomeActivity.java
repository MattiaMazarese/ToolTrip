package menù;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tooltrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import item.Item;
import item.ToolAdapter;
import item.VisualizzaProdottoSingoloActivity;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView txtWelcome;
    private GridLayout gridPublicObjects;
    private RecyclerView recyclerViewTools;
    private ToolAdapter toolAdapter;
    private List<Item> itemList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inizializzazione Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("items");

        // Inizializzazione layout
        txtWelcome = findViewById(R.id.txtWelcome);
        recyclerViewTools = findViewById(R.id.recyclerViewTools);

        // Imposta layout manager per RecyclerView
        recyclerViewTools.setLayoutManager(new GridLayoutManager(this, 2));

        // Configura adattatore
        itemList = new ArrayList<>();
        toolAdapter = new ToolAdapter(itemList);
        recyclerViewTools.setAdapter(toolAdapter);

        // Carica dati utente e strumenti pubblici
        loadUserData();
        loadPublicObjects();

        // Configura il menu
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );
    }

    private void loadUserData() {
        String userID = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("nome");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    txtWelcome.setText("Bentornato " + userName);
                } else {
                    txtWelcome.setText("Utente non trovato");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                txtWelcome.setText("Errore nel recupero dei dati");
            }
        });
    }

    private void loadPublicObjects() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear(); // Svuota la lista corrente
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null && item.isPubblico()) {
                        itemList.add(item);
                    }
                }
                toolAdapter.notifyDataSetChanged(); // Notifica all'adattatore
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Errore nel caricamento degli strumenti.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
