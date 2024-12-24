package group;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private ImageButton btnSendMessage;

    private DatabaseReference mDatabase;
    private String groupID;
    private List<Message> messageList;

    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        mDatabase = FirebaseDatabase.getInstance().getReference("Groups");

        // Recupera l'ID del gruppo dall'Intent
        groupID = getIntent().getStringExtra("groupID");

        // Configura la chat
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, currentUserId());
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);

        // Carica i messaggi della chat
        loadMessages();

        // Invia un messaggio
        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        DatabaseReference chatReference = mDatabase.child(groupID).child("chat");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                recyclerViewChat.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupChatActivity.this, "Errore nel caricamento dei messaggi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String text = editTextMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        DatabaseReference chatReference = mDatabase.child(groupID).child("chat");

        String userId = currentUserId();
        if (userId == null) {
            Toast.makeText(this, "Utente non autenticato. Effettua il login.", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();
        Message message = new Message(userId, text, timestamp);

        String messageID = chatReference.push().getKey();
        chatReference.child(messageID).setValue(message);

        editTextMessage.setText(""); // Resetta il campo di testo
    }

    private String currentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return currentUser.getUid();
    }
}
