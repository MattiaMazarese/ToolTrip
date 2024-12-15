package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class SelectGroupActivity extends AppCompatActivity {

    private Button btnCreaGruppo, btnRicerca;
    private RecyclerView rvGroupList;
    private GroupAdapter groupAdapter;
    private DatabaseReference groupRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        btnCreaGruppo = findViewById(R.id.btnCreaGruppo);
        btnRicerca = findViewById(R.id.btnRicerca);
        rvGroupList = findViewById(R.id.rvGroupList);

        rvGroupList.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new GroupAdapter(new ArrayList<>());
        rvGroupList.setAdapter(groupAdapter);

        groupRef = FirebaseDatabase.getInstance().getReference("Groups");

        btnCreaGruppo.setOnClickListener(v -> {
            Intent intent = new Intent(SelectGroupActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        });

        btnRicerca.setOnClickListener(v -> {
            Intent intent = new Intent(SelectGroupActivity.this, SearchGroupActivity.class);
            startActivity(intent);
        });

    }
}
