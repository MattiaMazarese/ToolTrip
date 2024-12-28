package item;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class VisualizzaProdottoSingoloActivity extends AppCompatActivity {

    private TextView textViewNome, textViewDescrizione, textViewCategoria;
    private Button buttonPrestito,buttonInvisibile;
    private RecyclerView recyclerViewRecensione;
    private List<Recensione> recensioneList;

    private RecensioneAdapter recensioneAdapter;
    private DatabaseReference mDatabase;
    private String prestitoId = null;
    private String recensioneId =null;
    private String prestitoUserID = null;
    private String itemID = null;
    private String possessoreID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodotto_singolo);

        textViewNome = findViewById(R.id.textViewNome);
        textViewDescrizione = findViewById(R.id.textViewDescrizione);
        textViewCategoria = findViewById(R.id.textViewCategoria);
        buttonPrestito = findViewById(R.id.buttonPrestito);
        buttonInvisibile=findViewById(R.id.buttonInvisibile);
        recyclerViewRecensione=findViewById(R.id.recyclerViewRecensione);

        recyclerViewRecensione.setLayoutManager(new LinearLayoutManager(this));
        recensioneList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Recensioni");
        recensioneAdapter = new RecensioneAdapter(recensioneList);
        recyclerViewRecensione.setAdapter(recensioneAdapter);

        loadRecensioniFromDatabase();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Ottieni i dati passati dall'intent
        String nome = getIntent().getStringExtra("itemNome");
        String descrizione = getIntent().getStringExtra("itemDescrizione");
        String categoria = getIntent().getStringExtra("itemCategoria");
        itemID = getIntent().getStringExtra("itemID");
        possessoreID=getIntent().getStringExtra("possessoreID");



        // Ottieni il prestito dal database
        DatabaseReference prestitoRef = FirebaseDatabase.getInstance().getReference("Prestito");
        prestitoRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();

                // Ciclo attraverso tutti gli elementi di "Prestito"
                for (DataSnapshot prestitoSnapshot : dataSnapshot.getChildren()) {
                    // Ottieni i dati dai campi del prestito
                    if (Objects.equals(prestitoSnapshot.child("idOggetto").getValue(String.class), getIntent().getStringExtra("itemID"))) {
                        prestitoId = prestitoSnapshot.child("prestitoID").getValue(String.class);
                        prestitoUserID=prestitoSnapshot.child("idUtente").getValue(String.class);
                        break;  // Esci dal ciclo una volta trovato il prestito
                    }
                }

                // Dopo aver trovato il prestitoId, imposta il comportamento del pulsante
                setButtonAction(); // Aggiungi questa riga per chiamare il metodo che gestisce il bottone
            } else {
                // Gestione errori nel caso la richiesta fallisca
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Errore durante il recupero dei dati: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Imposta i dati nelle TextView
        textViewNome.setText(nome);
        textViewDescrizione.setText(descrizione);
        textViewCategoria.setText(categoria);

        // Configurazione del menu tramite MenuHandler
        MenuHandler menuHandler = new MenuHandler(this);
        menuHandler.setUpMenuListeners(
                findViewById(R.id.iconHome),
                findViewById(R.id.iconAggiungiTool),
                findViewById(R.id.iconGroup),
                findViewById(R.id.iconProfile)
        );
    }

    private void loadRecensioniFromDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recensioneList.clear(); // Clear previous data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recensione recensione = snapshot.getValue(Recensione.class);
                    if (recensione != null && recensione.getIdOggetto().equals(itemID)) {
                        recensioneList.add(recensione);
                    }
                }
                recensioneAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setButtonAction() {
        // Imposta il comportamento del pulsante dopo che prestitoId è stato aggiornato
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
        String userID = currentUser.getUid();
        if(!Objects.equals(this.possessoreID, userID)) {
            if (itemPrestato()) {
                if (utentePrestitoItem()) {
                    buttonPrestito.setText("Restituisci tool");
                    buttonPrestito.setOnClickListener(v -> remouvePrestitoFromDatabase());
                } else {
                    buttonPrestito.setText("tool non disponibile");
                    buttonPrestito.setOnClickListener(v -> messaggioFinePrestito());
                }
            } else {
                buttonPrestito.setText("Prendi in prestito il tool");
                buttonPrestito.setOnClickListener(v -> addPrestitoToDatabase(this.itemID));
            }
        }else{
            if(itemPrestato()){
                buttonPrestito.setText("Tool attualmente prestato");
            }else{
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items").child(itemID).child("pubblico");
                databaseReference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Boolean valorePubblico = task.getResult().getValue(Boolean.class);
                        if(valorePubblico.equals(true)){
                            buttonPrestito.setText("Rendi tool privato");
                            buttonPrestito.setOnClickListener(v -> rendiToolPrivato(false));
                        }else{
                            buttonPrestito.setText("Rendi tool pubblico");
                            buttonPrestito.setOnClickListener(v -> rendiToolPrivato(true));
                        }
                    } else {
                        Log.e("Error", "Errore nel recupero del valore", task.getException());
                    }
                });
                buttonInvisibile.setVisibility(View.VISIBLE);
                buttonInvisibile.setClickable(true);
                buttonInvisibile.setText(("Rimuovi tool"));
                buttonInvisibile.setOnClickListener(v -> rimuoviTool());
            }
        }
    }

    private void rimuoviTool() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items").child(itemID);
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "tool rimosso successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Failed to rimuovere tool. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rendiToolPrivato(boolean val) {
        // Verifica il valore prima di scrivere nel database
        Log.d("RendiToolPrivato", "Valore che stai cercando di settare: " + val);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items").child(itemID).child("pubblico");
        databaseReference.setValue(val).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(val){
                    Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Tool diventato pubblico successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Tool diventato privato successfully", Toast.LENGTH_SHORT).show();
                }
                recreate();
            } else {
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Failed to privatizzare tool. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void messaggioFinePrestito() {
        // Verifica se il prestitoID è presente prima di accedere a Firebase
        if (prestitoId != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Prestito").child(prestitoId).child("dataFine");
            Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Il prestito finirà " + databaseReference.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean itemPrestato(){
        if(prestitoId==null){
            //Toast.makeText(VisualizzaProdottoSingoloActivity.this,"null: " + prestitoId,Toast.LENGTH_SHORT).show();
            return false;
        }
        //Toast.makeText(VisualizzaProdottoSingoloActivity.this,"not null: " + prestitoId,Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean utentePrestitoItem(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return false;
        }
        String userID = currentUser.getUid();
        if(userID.equals(prestitoUserID)){
            return true;
        }else{
            return false;
        }
    }

    private void addPrestitoToDatabase(String itemID) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a unique item ID
        prestitoId = mDatabase.push().getKey();

        String userID = currentUser.getUid();
        Prestito newPrestito=new Prestito(prestitoId,userID,itemID);

        // Save the item to Firebase Realtime Database
        if (prestitoId != null) {
            mDatabase.child("Prestito").child(prestitoId).setValue(newPrestito).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Prestito added successfully", Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Failed to add prestito. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Failed to generate item ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void remouvePrestitoFromDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Prestito").child(prestitoId);
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Mostra un dialogo per aggiungere una recensione
                mostraDialogoRecensione();
            } else {
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Failed to rimuovere prestito. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRecensioneToDatabase(String recensioneText) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        recensioneId = mDatabase.push().getKey();
        String userID = currentUser.getUid();
        Recensione newRecensione = new Recensione(recensioneId, userID, itemID, recensioneText);

        if (recensioneId != null) {
            mDatabase.child("Recensioni").child(recensioneId).setValue(newRecensione).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Recensione aggiunta con successo", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Errore nell'aggiungere la recensione. Riprova.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Errore nella generazione dell'ID recensione", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostraDialogoRecensione() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aggiungi Recensione");

        // Configura un EditText per inserire la recensione
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

        // Configura i pulsanti
        builder.setPositiveButton("Invia", (dialog, which) -> {
            String recensioneText = input.getText().toString().trim();
            if (!recensioneText.isEmpty()) {
                addRecensioneToDatabase(recensioneText);
            } else {
                Toast.makeText(this, "Recensione vuota non aggiunta.", Toast.LENGTH_SHORT).show();
            }
            recreate();
        });

        builder.setNegativeButton("Annulla", (dialog, which) -> {
            dialog.cancel();
            recreate();
        });

        builder.show();
    }

}

