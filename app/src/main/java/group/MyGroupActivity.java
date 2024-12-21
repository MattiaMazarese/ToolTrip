package group;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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

public class MyGroupActivity extends AppCompatActivity {

    private Button creaGruppo;
    private Button cercaGruppo;
    private RecyclerView cardMieiGruppi;
    private RecyclerView cardGruppiAggiunti;
    private List<Group> mieiGroupCreatiList,mieiGroupAggiuntiList;
    private GroupAdapter groupAdapterCreati,groupAdapterAggiunti;
    private String currentUserID;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        creaGruppo=findViewById(R.id.btn_crea_gruppo);
        creaGruppo.setOnClickListener(v -> {
            Intent intent = new Intent(MyGroupActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        });
        cercaGruppo=findViewById(R.id.btn_cerca_gruppo);
        cercaGruppo.setOnClickListener(v -> {
            Intent intent = new Intent(MyGroupActivity.this, SearchGroupActivity.class);
            startActivity(intent);
        });

        cardMieiGruppi = findViewById(R.id.recycler_miei_gruppi);
        cardMieiGruppi.setLayoutManager(new LinearLayoutManager(this));

        cardGruppiAggiunti = findViewById(R.id.recycler_gruppi_aggiunti);
        cardGruppiAggiunti.setLayoutManager(new LinearLayoutManager(this));

        mieiGroupCreatiList = new ArrayList<>();
        mieiGroupAggiuntiList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("Groups");

        groupAdapterCreati = new GroupAdapter(mieiGroupCreatiList);
        groupAdapterAggiunti = new GroupAdapter(mieiGroupAggiuntiList);

        cardMieiGruppi.setAdapter(groupAdapterCreati);
        cardGruppiAggiunti.setAdapter(groupAdapterAggiunti);

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
                mieiGroupCreatiList.clear(); // Clear previous data
                mieiGroupAggiuntiList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group != null && Objects.equals(group.getCreatoreID(), currentUserID)) {
                        mieiGroupCreatiList.add(group);
                    }
                    if (group != null && group.getMembri().contains(currentUserID)) {
                        mieiGroupAggiuntiList.add(group);
                    }
                }
                groupAdapterCreati.notifyDataSetChanged();
                groupAdapterAggiunti.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyGroupActivity.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
