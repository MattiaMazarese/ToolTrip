package item;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tooltrip.R;

import java.util.List;

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
    public void onBindViewHolder(ToolViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.textViewNome.setText(item.getNome());
        holder.textViewDescrizione.setText(item.getDescrizione());

        // Verifica che la categoria non sia null o vuota
        String category = item.getCategoriaId(); // Usa il metodo corretto della classe Item
        if (category == null || category.isEmpty()) {
            category = "Altro"; // Imposta un valore predefinito
        }

        final String finalCategory = category;

        int categoryImage = getCategoryImage(category);
        holder.imageViewCategory.setImageResource(categoryImage);

        holder.btnVisualizza.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), VisualizzaProdottoSingoloActivity.class);
            intent.putExtra("itemNome", item.getNome());
            intent.putExtra("itemDescrizione", item.getDescrizione());
            intent.putExtra("itemCategoria", finalCategory); // Usa la variabile locale "category"
            intent.putExtra("itemID", item.getItemId());
            intent.putExtra("possessoreID", item.getPossesore().getUserID());
            holder.itemView.getContext().startActivity(intent);
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