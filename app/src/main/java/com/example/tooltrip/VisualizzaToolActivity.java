package com.example.tooltrip;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

    public class VisualizzaToolActivity extends AppCompatActivity {

        private DatabaseReference mDatabase;
        private TextView nome;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_visualizza_tool);

            // Inizializzazione del riferimento al database
            mDatabase = FirebaseDatabase.getInstance().getReference("items");

        }

    }