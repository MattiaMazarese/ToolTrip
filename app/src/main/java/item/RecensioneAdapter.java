// RecensioneAdapter.java
package item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tooltrip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class RecensioneAdapter extends RecyclerView.Adapter<RecensioneAdapter.RecensioneViewHolder> {

    private List<Recensione> recensioni;

    public RecensioneAdapter(List<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    @NonNull
    @Override
    public RecensioneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recensione, parent, false);
        return new RecensioneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecensioneViewHolder holder, int position) {
        Recensione recensione = recensioni.get(position);


        DatabaseReference utentiRef = FirebaseDatabase.getInstance().getReference("Users");
        utentiRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();

                // Ciclo attraverso tutti gli elementi di "Prestito"
                for (DataSnapshot utentiSnapshot : dataSnapshot.getChildren()) {
                    // Ottieni i dati dai campi del prestito
                    if (Objects.equals(utentiSnapshot.child("userID").getValue(String.class), recensione.getIdUtente())) {
                        holder.autoreTextView.setText(utentiSnapshot.child("nome").getValue(String.class)+" "+utentiSnapshot.child("cognome").getValue(String.class));
                        holder.descrizioneTextView.setText(recensione.getRecensioneText());
                        break;  // Esci dal ciclo una volta trovato il prestito
                    }
                }
            }
        });




        //holder.autoreTextView.setText(recensione.getIdUtente());
        //holder.descrizioneTextView.setText(recensione.getRecensioneText());
    }

    @Override
    public int getItemCount() {
        return recensioni.size();
    }

    public static class RecensioneViewHolder extends RecyclerView.ViewHolder {
        TextView titoloTextView;
        TextView descrizioneTextView;
        TextView autoreTextView;

        public RecensioneViewHolder(@NonNull View itemView) {
            super(itemView);
            titoloTextView = itemView.findViewById(R.id.autoreTextView);
            descrizioneTextView = itemView.findViewById(R.id.descrizioneTextView);
            autoreTextView = itemView.findViewById(R.id.autoreTextView);
        }
    }
}

