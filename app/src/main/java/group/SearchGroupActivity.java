package group;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import java.util.Objects;

public class SearchGroupActivity extends AppCompatActivity {

    private RecyclerView cardAllGruppi;
    private List<Group> mieiGroupAllList;
    private GroupAdapter groupAdapterAll;
    private String currentUserID;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        cardAllGruppi = findViewById(R.id.recyclerView);
        cardAllGruppi.setLayoutManager(new LinearLayoutManager(this));


        mieiGroupAllList = new ArrayList<>();


        mDatabase = FirebaseDatabase.getInstance().getReference("Groups");

        groupAdapterAll= new GroupAdapter(mieiGroupAllList);

        cardAllGruppi.setAdapter(groupAdapterAll);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
        currentUserID = currentUser.getUid();

        loadItemsFromDatabase();

    }

    private void loadItemsFromDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mieiGroupAllList.clear(); // Clear previous data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group != null) {
                        mieiGroupAllList.add(group);
                    }
                }
                groupAdapterAll.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SearchGroupActivity.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
