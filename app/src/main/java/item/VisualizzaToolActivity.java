package item;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import menù.MenuHandler;
import com.example.tooltrip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class VisualizzaToolActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ToolAdapter toolAdapter;
    private List<Item> itemList;
    private List<Category> categoryList;
    private DatabaseReference mDatabase, categoryDatabase;

    private Button buttonMyTool;
    private Button buttonCreaTool;
    private Spinner spinnerCategoryFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_tool);

        // Inizializzazione pulsanti
        buttonCreaTool = findViewById(R.id.buttonCreateTool);
        buttonCreaTool.setOnClickListener(v -> {
            Intent intent = new Intent(VisualizzaToolActivity.this, AggiungiToolActivity.class);
            startActivity(intent);
        });
        buttonMyTool = findViewById(R.id.buttonMyTool);
        buttonMyTool.setOnClickListener(v -> {
            Intent intent = new Intent(VisualizzaToolActivity.this, ToolPersonaliUtente.class);
            startActivity(intent);
        });

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("items");

        // Spinner
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
        categoryList = new ArrayList<>();
        categoryDatabase = FirebaseDatabase.getInstance().getReference("categories");

        toolAdapter = new ToolAdapter(itemList);
        recyclerView.setAdapter(toolAdapter);

        // Caricamento iniziale
        loadCategories();
        loadItemsFromDatabase();

        // Set up menu
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );
    }

    private void loadCategories() {
        categoryDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryList.clear();
                categoryList.add(new Category("all", "Tutte le categorie", "")); // Opzione per tutte le categorie
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                setupCategorySpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(VisualizzaToolActivity.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categoryList) {
            categoryNames.add(category.getNome());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryFilter.setAdapter(adapter);

        spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterItemsByCategory(categoryList.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Non fare nulla
            }
        });
    }

    private void filterItemsByCategory(String categoryId) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);

                    if (item != null) {
                        Log.d("VisualizzaToolActivity", "Elemento trovato: " + item.getNome() + ", Categoria: " + item.getCategoriaId());
                    } else {
                        Log.e("VisualizzaToolActivity", "Elemento null trovato nel database.");
                    }

                    // Aggiungi solo gli oggetti validi
                    if (item != null && item.getCategoriaId() != null) {
                        if (categoryId.equals("all") || categoryId.equals(item.getCategoriaId())) {
                            if (item.isPubblico() && !item.getVisualizzaSoloGruppi()) {
                                itemList.add(item);
                                Log.d("VisualizzaToolActivity", "Elemento aggiunto: " + item.getNome());
                            } else {
                                Log.d("VisualizzaToolActivity", "Elemento escluso (non pubblico): " + item.getNome());
                            }
                        } else {
                            Log.d("VisualizzaToolActivity", "Elemento escluso (categoria non corrispondente): " + item.getNome());
                        }
                    }
                }
                toolAdapter.notifyDataSetChanged();

                Log.d("VisualizzaToolActivity", "Totale elementi visualizzati: " + itemList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(VisualizzaToolActivity.this, "Errore nel caricamento degli elementi.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadItemsFromDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null && item.isPubblico()) {
                        itemList.add(item);
                    }
                }
                toolAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(VisualizzaToolActivity.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
