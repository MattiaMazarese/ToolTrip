package item;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.DatePicker;

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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;

public class VisualizzaProdottoSingoloActivity extends AppCompatActivity {

    private TextView textViewNome, textViewDescrizione, textViewCategoria;
    private Button buttonPrestito, buttonInvisibile, buttonVisualizzaGruppo;
    private RecyclerView recyclerViewRecensione;
    private List<Recensione> recensioneList;
    private RecensioneAdapter recensioneAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference categoryDatabase;
    private String prestitoId = null;
    private String recensioneId = null;
    private String prestitoUserID = null;
    private String itemID = null;
    private String possessoreID = null;
    private Boolean accettazionePrestito;

    private String dataFinePrestito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodotto_singolo);

        textViewNome = findViewById(R.id.textViewNome);
        textViewDescrizione = findViewById(R.id.textViewDescrizione);
        textViewCategoria = findViewById(R.id.textViewCategoria);
        buttonPrestito = findViewById(R.id.buttonPrestito);
        buttonInvisibile = findViewById(R.id.buttonInvisibile);
        recyclerViewRecensione = findViewById(R.id.recyclerViewRecensione);
        buttonVisualizzaGruppo = findViewById(R.id.buttonVisualizzaGruppo);
        buttonInvisibile = findViewById(R.id.buttonInvisibile);
        recyclerViewRecensione = findViewById(R.id.recyclerViewRecensione);
        buttonVisualizzaGruppo = findViewById(R.id.buttonVisualizzaGruppo);

        recyclerViewRecensione.setLayoutManager(new LinearLayoutManager(this));
        recensioneList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Recensioni");
        recensioneAdapter = new RecensioneAdapter(recensioneList);
        recyclerViewRecensione.setAdapter(recensioneAdapter);

        // Inizializzazione di mDatabase
        mDatabase = FirebaseDatabase.getInstance().getReference("Recensioni");
        // Carica recensioni dal database
        loadRecensioniFromDatabase();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        categoryDatabase = FirebaseDatabase.getInstance().getReference("categories");

        // Ottieni i dati passati dall'intent
        String nome = getIntent().getStringExtra("itemNome");
        String descrizione = getIntent().getStringExtra("itemDescrizione");
        String categoria = getIntent().getStringExtra("itemCategoria");
        String categoriaId = getIntent().getStringExtra("itemCategoria");
        itemID = getIntent().getStringExtra("itemID");
        possessoreID = getIntent().getStringExtra("possessoreID");
        possessoreID = getIntent().getStringExtra("possessoreID");

        // Recupera il nome della categoria
        categoryDatabase.child(categoriaId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String categoriaNome = dataSnapshot.child("nome").getValue(String.class);
                    textViewCategoria.setText(categoriaNome != null ? categoriaNome : "Categoria sconosciuta");
                } else {
                    textViewCategoria.setText("Categoria non trovata");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                textViewCategoria.setText("Errore nel caricamento della categoria");
            }
        });

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
                        prestitoUserID = prestitoSnapshot.child("idUtente").getValue(String.class);
                        accettazionePrestito = prestitoSnapshot.child("accettazione").getValue(Boolean.class);
                        dataFinePrestito=prestitoSnapshot.child("dataRichiestaFine").child("dayOfMonth").getValue(Long.class).toString()+"/"+prestitoSnapshot.child("dataRichiestaFine").child("monthValue").getValue(Long.class).toString()+"/"+prestitoSnapshot.child("dataRichiestaFine").child("year").getValue(Long.class).toString();
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
        MenuHandler menuHandler = new MenuHandler(this,"tool");
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
        if (!Objects.equals(this.possessoreID, userID)) {
            if (itemPrestato()) {
                if (utentePrestitoItem() && accettazionePrestito) {
                    buttonPrestito.setText("Restituisci tool");
                    buttonPrestito.setOnClickListener(v -> remouvePrestitoFromDatabase());
                } else if (utentePrestitoItem() && !accettazionePrestito) {
                    buttonPrestito.setText("Annulla richiesta prestito");
                    buttonPrestito.setOnClickListener(v -> remouvePrestitoFromDatabase());
                } else {
                    buttonPrestito.setText("tool non disponibile");
                    buttonPrestito.setOnClickListener(v -> messaggioFinePrestito());
                }
            } else {
                buttonPrestito.setText("Prendi in prestito il tool");
                buttonPrestito.setOnClickListener(v -> mostraDialogoSelezioneDate(this.itemID));
            }
        } else {
            if (itemPrestato() && !accettazionePrestito) {
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                usersRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        for (DataSnapshot uSnapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(uSnapshot.child("userID").getValue(String.class), prestitoUserID)) {
                                buttonPrestito.setText("Accetta di prestare a "+uSnapshot.child("nome").getValue(String.class)+" "+uSnapshot.child("cognome").getValue(String.class)+" fino al "+dataFinePrestito);
                                break;
                            }
                        }
                    } else {
                        Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Errore durante il recupero dei dati: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                buttonPrestito.setOnClickListener(v -> accettaPrestito());
            } else if (itemPrestato() && accettazionePrestito) {
                buttonPrestito.setText("Tool attualmente prestato");
            } else {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items").child(itemID).child("pubblico");
                databaseReference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Boolean valorePubblico = task.getResult().getValue(Boolean.class);
                        if (valorePubblico.equals(true)) {
                            buttonPrestito.setText("Rendi tool privato");
                            buttonPrestito.setOnClickListener(v -> rendiToolPrivato(false, "pubblico"));
                        } else {
                            buttonPrestito.setText("Rendi tool pubblico");
                            buttonPrestito.setOnClickListener(v -> rendiToolPrivato(true, "pubblico"));
                        }
                    } else {
                        Log.e("Error", "Errore nel recupero del valore", task.getException());
                    }
                });
                databaseReference = FirebaseDatabase.getInstance().getReference("items").child(itemID).child("visualizzaSoloGruppi");
                databaseReference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Boolean valoreVisualizzaPubblico = task.getResult().getValue(Boolean.class);
                        if (valoreVisualizzaPubblico.equals(true)) {
                            buttonVisualizzaGruppo.setText("Rendi tool visualizzabile ovunque");
                            buttonVisualizzaGruppo.setOnClickListener(v -> rendiToolPrivato(false, "visualizzaSoloGruppi"));
                            buttonVisualizzaGruppo.setVisibility(View.VISIBLE);
                            buttonVisualizzaGruppo.setClickable(true);
                        } else {
                            buttonVisualizzaGruppo.setText("Rendi tool visualizzabile solo sui gruppi");
                            buttonVisualizzaGruppo.setOnClickListener(v -> rendiToolPrivato(true, "visualizzaSoloGruppi"));
                            buttonVisualizzaGruppo.setVisibility(View.VISIBLE);
                            buttonVisualizzaGruppo.setClickable(true);
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



    private void accettaPrestito() {
        DatabaseReference prestitoRef = FirebaseDatabase.getInstance().getReference("Prestito");
        prestitoRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                // Ciclo attraverso tutti gli elementi di "Prestito"
                for (DataSnapshot prestitoSnapshot : dataSnapshot.getChildren()) {
                    // Ottieni i dati dai campi del prestito
                    if (Objects.equals(prestitoSnapshot.child("prestitoID").getValue(String.class), prestitoId)) {
                        // Modifica il valore di "accettazione"
                        prestitoSnapshot.getRef().child("accettazione").setValue(true)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Prestito accettato!", Toast.LENGTH_SHORT).show();
                                        recreate();
                                    } else {
                                        Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Errore durante l'aggiornamento: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        break;  // Esci dal ciclo una volta trovato il prestito
                    }
                }
            } else {
                // Gestione errori nel caso la richiesta fallisca
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Errore durante il recupero dei dati: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void rendiToolPrivato(boolean val, String path) {
        // Verifica il valore prima di scrivere nel database
        Log.d("RendiToolPrivato", "Valore che stai cercando di settare: " + val);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items").child(itemID).child(path);
        databaseReference.setValue(val).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                    Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Operazione avvenuta con successo", Toast.LENGTH_SHORT).show();
                recreate();
            } else {
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "operazione fallita. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void messaggioFinePrestito() {
        // Verifica se il prestitoID è presente prima di accedere a Firebase
        if (prestitoId != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Prestito").child(prestitoId).child("dataRichiestaFine");
            Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Prestito non concluso", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean itemPrestato() {
        if (prestitoId == null) {
            //Toast.makeText(VisualizzaProdottoSingoloActivity.this,"null: " + prestitoId,Toast.LENGTH_SHORT).show();
            return false;
        }
        //Toast.makeText(VisualizzaProdottoSingoloActivity.this,"not null: " + prestitoId,Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean utentePrestitoItem() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return false;
        }
        String userID = currentUser.getUid();
        if (userID.equals(prestitoUserID)) {
            return true;
        } else {
            return false;
        }
    }

    private void addPrestitoToDatabase(String itemID, LocalDate dataInizio,LocalDate dataFine) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a unique item ID
        prestitoId = mDatabase.push().getKey();

        String userID = currentUser.getUid();
        Prestito newPrestito = new Prestito(prestitoId, userID, itemID,dataInizio,dataFine);

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
            if (task.isSuccessful() && accettazionePrestito) {
                // Mostra un dialogo per aggiungere una recensione
                mostraDialogoRecensione();
            } else if (task.isSuccessful()) {
                Toast.makeText(VisualizzaProdottoSingoloActivity.this, "Richiesta rimossa con successo", Toast.LENGTH_SHORT).show();
                recreate();
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

    private void mostraDialogoSelezioneDate(String itemID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleziona la data di fine del prestito");

        // Inflating il layout personalizzato del dialogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);
        DatePicker datePickerInizio = dialogView.findViewById(R.id.datePickerInizio);
        DatePicker datePickerFine = dialogView.findViewById(R.id.datePickerFine);

        // Imposta la data di inizio su oggi e disabilita il DatePicker per la data di inizio
        LocalDate oggi = LocalDate.now();
        datePickerInizio.updateDate(oggi.getYear(), oggi.getMonthValue() - 1, oggi.getDayOfMonth());
        datePickerInizio.setEnabled(false);

        builder.setView(dialogView);
        builder.setPositiveButton(null, null); // Disabilita il comportamento automatico
        builder.setNegativeButton(null, null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button buttonAnnulla = dialog.findViewById(R.id.buttonAnnulla);
        Button buttonConferma = dialog.findViewById(R.id.buttonConferma);

        buttonAnnulla.setOnClickListener(v -> dialog.dismiss());

        buttonConferma.setOnClickListener(v -> {
            // Ottieni la data selezionata per la fine
            LocalDate dataInizio = oggi;
            LocalDate dataFine = LocalDate.of(datePickerFine.getYear(), datePickerFine.getMonth() + 1, datePickerFine.getDayOfMonth());

            if (dataFine.isAfter(dataInizio) && dataFine.isBefore(dataInizio.plusDays(60))) {
                addPrestitoToDatabase(itemID, dataInizio, dataFine);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "La data di fine deve essere entro 60 giorni da oggi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
