package group;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tooltrip.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messageList;
    private String currentUserID;

    public ChatAdapter(List<Message> messageList, String currentUserID) {
        this.messageList = messageList;
        this.currentUserID = currentUserID;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);

        // Formatta il timestamp
        String formattedTimestamp = new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date(message.getTimestamp()));

        if (message.getSender().equals(currentUserID)) {
            holder.layoutMessageSelf.setVisibility(View.VISIBLE);
            holder.layoutMessageOther.setVisibility(View.GONE);
            holder.textViewMessageSelf.setText(message.getText());
            holder.textViewTimestampSelf.setText(formattedTimestamp);
        } else {
            holder.layoutMessageSelf.setVisibility(View.GONE);
            holder.layoutMessageOther.setVisibility(View.VISIBLE);
            holder.textViewMessage.setText(message.getText());
            holder.textViewTimestamp.setText(formattedTimestamp);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutMessageOther;
        LinearLayout layoutMessageSelf;
        TextView textViewMessage;
        TextView textViewMessageSelf;
        TextView textViewTimestamp;
        TextView textViewTimestampSelf;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutMessageOther = itemView.findViewById(R.id.layoutMessageOther);
            layoutMessageSelf = itemView.findViewById(R.id.layoutMessageSelf);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewMessageSelf = itemView.findViewById(R.id.textViewMessageSelf);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            textViewTimestampSelf = itemView.findViewById(R.id.textViewTimestampSelf);
        }
    }


}
