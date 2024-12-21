package login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import menù.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inizializza Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Controlla se l'utente è autenticato
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Utente non autenticato, vai alla schermata di login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // Impedisce all'utente di tornare indietro
        } else {
            // Utente autenticato, vai alla schermata principale (HomeActivity)
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
    }
}
