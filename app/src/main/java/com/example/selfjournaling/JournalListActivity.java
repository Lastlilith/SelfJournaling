package com.example.selfjournaling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.selfjournaling.model.Journal;
import com.example.selfjournaling.ui.JournalRecyclerAdapter;
import com.example.selfjournaling.util.JournalApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JournalListActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    //private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private StorageReference storageReference;
    private List<Journal> journalList;
    private RecyclerView recyclerView;
    private JournalRecyclerAdapter journalRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Journal");
    private TextView noJournalEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noJournalEntry = findViewById(R.id.listNoThoughts);

        journalList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionAdd:
                //Action add journal
                if(user != null && firebaseAuth != null) {
                    startActivity(new Intent(JournalListActivity.this,
                            PostJournalActivity.class));
                    //finish();
                }
                break;
            case R.id.actionSignOut:
                //Action sign out
                if(user != null && firebaseAuth != null) {
                    firebaseAuth.signOut();

                    startActivity(new Intent(JournalListActivity.this,
                            MainActivity.class));
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", JournalApi.getInstance()
                .getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        for(QueryDocumentSnapshot journals: queryDocumentSnapshots) {
                            Journal journal = journals.toObject(Journal.class);
                            journalList.add(journal);
                        }

                        //Invoke recycler view
                        journalRecyclerAdapter = new JournalRecyclerAdapter(JournalListActivity.this,
                                journalList);
                        recyclerView.setAdapter(journalRecyclerAdapter);
                        journalRecyclerAdapter.notifyDataSetChanged();

                    } else {
                        noJournalEntry.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {

                });
    }
}