package com.example.selfjournaling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.selfjournaling.util.JournalApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private Button createAcctButton;
    private AutoCompleteTextView emailAddress;
    private EditText password;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
//    private FirebaseAuth.AuthStateListener authStateListener;
//    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        progressBar = findViewById(R.id.loginProgress);

        firebaseAuth = FirebaseAuth.getInstance();

        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginButton = findViewById(R.id.emailSignInButton);
        createAcctButton = findViewById(R.id.createAccountButtonLogin);

        createAcctButton.setOnClickListener(v -> startActivity(
                new Intent(LoginActivity.this, CreateAccountActivity.class)));

        loginButton.setOnClickListener(v -> loginEmailPasswordUser(emailAddress.getText().toString().trim(),
                password.getText().toString().trim()));
    }

    private void loginEmailPasswordUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(email)
        && (!TextUtils.isEmpty(password))) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        String currentUserId = user.getUid();

                        collectionReference
                                .whereEqualTo("userId", currentUserId)
                                .addSnapshotListener((querySnapshot, e) -> {

                                    assert querySnapshot != null;
                                    if(!querySnapshot.isEmpty()) {

                                        progressBar.setVisibility(View.INVISIBLE);
                                        for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                                            JournalApi journalApi = JournalApi.getInstance();
                                            journalApi.setUsername(queryDocumentSnapshot.getString("username"));
                                            journalApi.setUserId(currentUserId);

                                            //Go to ListActivity
                                            startActivity(new Intent(LoginActivity.this,
                                                    PostJournalActivity.class));

                                        }


                                    }
                                });
                    })
                    .addOnFailureListener(e -> progressBar.setVisibility(View.INVISIBLE));
        } else {

            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this,
                    "Please enter email and password",
                    Toast.LENGTH_LONG).show();
        }
    }
}