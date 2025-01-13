package item;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VisualizzaProdottoSingoloActivity extends AppCompatActivity {

    private TextView textViewNome, textViewDescrizione, textViewCategoria;
    private Button buttonPrestito, buttonVisualizzaGruppo, buttonInvisibile;
    private RecyclerView recyclerViewRecensione;
    private List<Recensione> recensioneList;
    private RecensioneAdapter recensioneAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference categoryDatabase;
    private String prestitoId = null;
    private String itemID = null;
    private String possessoreID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodotto_singolo);

        // Inizializza componenti UI
        textViewNome = findViewById(R.id.textViewNome);
        textViewDescrizione = findViewById(R.id.textViewDescrizione);
        textViewCategoria = findViewById(R.id.textViewCategoria);
        buttonPrestito = findViewById(R.id.buttonPrestito);
        buttonVisualizzaGruppo = findViewById(R.id.buttonVisualizzaGruppo);
        buttonInvisibile = findViewById(R.id.buttonInvisibile);
        recyclerViewRecensione = findViewById(R.id.recyclerViewRecensione);

        // Configura RecyclerView
        recyclerViewRecensione.setLayoutManager(new LinearLayoutManager(this));
        recensioneList = new ArrayList<>();
        recensioneAdapter = new RecensioneAdapter(recensioneList);
        recyclerViewRecensione.setAdapter(recensioneAdapter);

        // Database references
        mDatabase = FirebaseDatabase.getInstance().getReference();
        categoryDatabase = FirebaseDatabase.getInstance().getReference("categories");

        // Ottieni dati dall'intent
        String nome = getIntent().getStringExtra("itemNome");
        String descrizione = getIntent().getStringExtra("itemDescrizione");
        String categoriaId = getIntent().getStringExtra("itemCategoria");
        itemID = getIntent().getStringExtra("itemID");
        possessoreID = getIntent().getStringExtra("possessoreID");

        // Imposta dati UI
        textViewNome.setText(nome);
        textViewDescrizione.setText(descrizione);

        // Recupera nome categoria
        loadCategoria(categoriaId);

        // Recupera recensioni
        loadRecensioniFromDatabase();

        // Gestione del pulsante prestito
        handlePrestitoButton();
    }

    private void loadCategoria(String categoriaId) {
        categoryDatabase.child(categoriaId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String categoriaNome = snapshot.child("nome").getValue(String.class);
                    textViewCategoria.setText(categoriaNome != null ? categoriaNome : "Categoria sconosciuta");
                } else {
                    textViewCategoria.setText("Categoria non trovata");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textViewCategoria.setText("Errore nel caricamento della categoria");
            }
        });
    }

    private void loadRecensioniFromDatabase() {
        DatabaseReference recensioniRef = mDatabase.child("Recensioni");
        recensioniRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recensioneList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Recensione recensione = child.getValue(Recensione.class);
                    if (recensione != null && recensione.getIdOggetto().equals(itemID)) {
                        recensioneList.add(recensione);
                    }
                }
                recensioneAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Errore nel caricamento delle recensioni", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePrestitoButton() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utente non autenticato", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        if (!Objects.equals(possessoreID, userId)) {
            buttonPrestito.setText("Richiedi in prestito");
            buttonPrestito.setOnClickListener(v -> addPrestitoToDatabase(itemID));
        } else {
            buttonPrestito.setText("Gestisci il tool");
            // Proprietario gestisce la visibilità o rimuove il tool
            buttonPrestito.setOnClickListener(v -> toggleToolVisibility());
        }
    }

    private void addPrestitoToDatabase(String itemID) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utente non autenticato", Toast.LENGTH_SHORT).show();
            return;
        }

        mostraDialogoSelezioneDate(itemID);
    }

    private void mostraDialogoSelezioneDate(String itemID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleziona le date del prestito");

        // Inflater del nuovo layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);
        DatePicker datePickerInizio = dialogView.findViewById(R.id.datePickerInizio);
        DatePicker datePickerFine = dialogView.findViewById(R.id.datePickerFine);

        // Imposta il layout nel dialog
        builder.setView(dialogView);

        // Listener per il pulsante Conferma
        builder.setPositiveButton(null, null); // Disabilita il comportamento automatico
        builder.setNegativeButton(null, null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button buttonAnnulla = dialog.findViewById(R.id.buttonAnnulla);
        Button buttonConferma = dialog.findViewById(R.id.buttonConferma);

        buttonAnnulla.setOnClickListener(v -> dialog.dismiss());

        buttonConferma.setOnClickListener(v -> {
            // Ottieni le date selezionate
            LocalDate dataInizio = LocalDate.of(datePickerInizio.getYear(), datePickerInizio.getMonth() + 1, datePickerInizio.getDayOfMonth());
            LocalDate dataFine = LocalDate.of(datePickerFine.getYear(), datePickerFine.getMonth() + 1, datePickerFine.getDayOfMonth());

            if (dataFine.isAfter(dataInizio) && dataFine.isBefore(dataInizio.plusDays(60))) {
                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                prestitoId = mDatabase.push().getKey();
                Prestito prestito = new Prestito(prestitoId, userId, itemID, dataInizio, dataFine);

                if (prestitoId != null) {
                    mDatabase.child("Prestito").child(prestitoId).setValue(prestito).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Richiesta di prestito inviata con successo", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, "Errore nell'invio della richiesta", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Periodo non valido. Seleziona massimo 60 giorni", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void toggleToolVisibility() {
        DatabaseReference toolRef = mDatabase.child("items").child(itemID).child("pubblico");
        toolRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Boolean isPublic = task.getResult().getValue(Boolean.class);
                toolRef.setValue(!isPublic).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(this, isPublic ? "Tool reso privato" : "Tool reso pubblico", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Errore durante l'aggiornamento della visibilità", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
