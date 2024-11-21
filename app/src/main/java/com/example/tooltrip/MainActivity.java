package com.example.tooltrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the buttons by their IDs
        Button primaryButton = findViewById(R.id.primary_button);
        Button secondaryButton = findViewById(R.id.secondary_button);

        // Set click listener for the primary button
        primaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Condividi un oggetto" action
                Toast.makeText(MainActivity.this, "Condividi un oggetto clicked!", Toast.LENGTH_SHORT).show();

                // Example: Launch a new activity or share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out Tool Trip!");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        // Set click listener for the secondary button
        secondaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Scopri di più" action
                Toast.makeText(MainActivity.this, "Scopri di più clicked!", Toast.LENGTH_SHORT).show();


            }
        });
    }
}
