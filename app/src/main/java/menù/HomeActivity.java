package menù;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import item.VisualizzaProdottoSingoloActivity;
import menù.MenuHandler;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView txtWelcome;
    private GridLayout gridPublicObjects;

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
        gridPublicObjects = findViewById(R.id.gridPublicObjects);

        // Carica dati utente e oggetti pubblici
        loadUserData();
        loadPublicObjects();

        // Configura il menu
        setupMenu();
    }

    /**
     * Carica il nome utente dal database e aggiorna il messaggio di benvenuto.
     */
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
                Log.e("HomeActivity", "Errore caricamento nome utente: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Carica gli oggetti pubblici dal database e li aggiunge al GridLayout.
     */
    private void loadPublicObjects() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Item> publicObjects = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Item item = snapshot.getValue(Item.class);
                        if (item != null && item.isPubblico()) {
                            publicObjects.add(item);
                        }
                    } catch (Exception e) {
                        Log.e("HomeActivity", "Errore nel recupero dell'oggetto: " + e.getMessage());
                    }
                }

                for (Item item : publicObjects) {
                    addItemToGrid(item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeActivity", "Errore caricamento oggetti pubblici: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Aggiunge un oggetto al GridLayout.
     *
     * @param item L'oggetto da aggiungere.
     */
    private void addItemToGrid(Item item) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_layout, gridPublicObjects, false);

        TextView txtItemName = itemView.findViewById(R.id.txtItemName);
        Button btnDiscover = itemView.findViewById(R.id.btnDiscover);

        txtItemName.setText(item.getNome());

        btnDiscover.setOnClickListener(v -> {
            Intent intent = new Intent(this, VisualizzaProdottoSingoloActivity.class);
            intent.putExtra("itemNome", item.getNome());
            intent.putExtra("itemDescrizione", item.getDescrizione());
            intent.putExtra("itemCategoria", item.getCategoria());
            startActivity(intent);
        });

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.setMargins(8, 8, 8, 8);

        itemView.setLayoutParams(layoutParams);
        gridPublicObjects.addView(itemView);
    }

    /**
     * Configura i listener per il menu.
     */
    private void setupMenu() {
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );
    }
}
