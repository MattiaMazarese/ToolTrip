package item;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ToolViewHolder> {
    private List<Item> itemList;

    public ToolAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public ToolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tool, parent, false);
        return new ToolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Imposta i dati della card
        holder.textViewNome.setText(item.getNome());
        holder.textViewDescrizione.setText(item.getDescrizione());

        // Listener per il pulsante "Approfondisci"
        holder.btnVisualizza.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), VisualizzaProdottoSingoloActivity.class);
            intent.putExtra("itemNome", item.getNome());
            intent.putExtra("itemDescrizione", item.getDescrizione());
            intent.putExtra("itemCategoria", item.getCategoriaId());
            intent.putExtra("itemID", item.getItemId());
            intent.putExtra("possessoreID", item.getPossesore().getUserID());
            holder.itemView.getContext().startActivity(intent);
        });

        DatabaseReference categoriaRef = FirebaseDatabase.getInstance().getReference("categories");
        categoriaRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot prestitoSnapshot : dataSnapshot.getChildren()) {
                    if(Objects.equals(prestitoSnapshot.child("id").getValue(String.class), item.getCategoriaId())) {
                        switch (prestitoSnapshot.child("nome").getValue(String.class)) {
                            case "Informatica":
                                holder.imageViewCategory.setImageResource(R.drawable.ic_elettronica); // Verifica che il nome sia corretto
                                break;
                            case "Giardinaggio":
                                holder.imageViewCategory.setImageResource(R.drawable.ic_gear); // Verifica che il nome sia corretto
                                break;
                            case "Meccanica":
                                holder.imageViewCategory.setImageResource(R.drawable.ic_devices); // Verifica che il nome sia corretto
                                break;
                            case "Altro":
                                holder.imageViewCategory.setImageResource(R.drawable.ic_altro); // Verifica che il nome sia corretto
                                break;
                        }
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ToolViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNome, textViewDescrizione;
        Button btnVisualizza;
        ImageView imageViewCategory;

        public ToolViewHolder(View itemView) {
            super(itemView);
            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewDescrizione = itemView.findViewById(R.id.textViewDescrizione);
            btnVisualizza = itemView.findViewById(R.id.btnVisualizza);
            imageViewCategory = itemView.findViewById(R.id.imageViewCategory);
        }
    }

    // Map category to image resource
    private int getCategoryImage(String category) {
        if (category == null || category.isEmpty()) {
            // Categoria sconosciuta o non specificata, restituisci un'icona predefinita
            return R.drawable.ic_altro; // Immagine predefinita
        }

        Log.d("ToolAdapter", "Categoria trovata: " + category);

        switch (category) {
            case "Elettronica":
                return R.drawable.ic_elettronica; // Verifica che il nome sia corretto
            case "Meccanica":
                return R.drawable.ic_gear; // Verifica che il nome sia corretto
            case "Informatica":
                return R.drawable.ic_devices; // Verifica che il nome sia corretto
            case "Altro":
                return R.drawable.ic_altro; // Verifica che il nome sia corretto
            default:
                Log.w("ToolAdapter", "Categoria sconosciuta: " + category);
                return R.drawable.ic_altro; // Default se la categoria non è riconosciuta
        }
    }


}