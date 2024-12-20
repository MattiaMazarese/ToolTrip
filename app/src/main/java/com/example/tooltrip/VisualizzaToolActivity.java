package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VisualizzaToolActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ToolAdapter toolAdapter;
    private List<Item> itemList;
    private DatabaseReference mDatabase;

    private Button buttonMyTool;
    private Button buttonCreaTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_tool);

        buttonCreaTool=findViewById(R.id.buttonCreateTool);
        buttonCreaTool.setOnClickListener(v -> {
            Intent intent = new Intent(VisualizzaToolActivity.this, AggiungiToolActivity.class);
            startActivity(intent);
        });
        buttonMyTool=findViewById(R.id.buttonMyTool);
        buttonMyTool.setOnClickListener(v -> {
            // Apri MyToolsActivity
            Intent intent = new Intent(VisualizzaToolActivity.this, ToolPersonaliUtente.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("items");

        toolAdapter = new ToolAdapter(itemList);
        recyclerView.setAdapter(toolAdapter);

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
                itemList.clear(); // Clear previous data
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
