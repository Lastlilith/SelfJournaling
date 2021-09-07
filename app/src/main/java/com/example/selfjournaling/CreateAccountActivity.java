package com.example.selfjournaling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.selfjournaling.util.JournalApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {
    //private Button loginButton;
    private Button createAcctButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");


    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();

        createAcctButton = findViewById(R.id.createAccountButton);
        progressBar = findViewById(R.id.createAcctButton);
        emailEditText = findViewById(R.id.emailAccount);
        passwordEditText = findViewById(R.id.passwordAccount);
        userNameEditText = findViewById(R.id.usernameAccount);

        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                //user is already logged in..
            }else {
                //no user yet...
            }

        };

        createAcctButton.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(emailEditText.getText().toString())
                    && !TextUtils.isEmpty(passwordEditText.getText().toString())
                    && !TextUtils.isEmpty(userNameEditText.getText().toString())) {

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String username = userNameEditText.getText().toString().trim();

                createUserEmailAccount(email, password, username);

            }else {
                Toast.makeText(CreateAccountActivity.this,
                        "Empty Fields Not Allowed",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    private void createUserEmailAccount(String email, String password, final String username) {
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username)) {

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //we take user to AddJournalActivity
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;
                            final String currentUserId = currentUser.getUid();

                            //Create a user Map so we can create a user in the User collection
                            Map<String, String> userObj = new HashMap<>();
                            userObj.put("userId", currentUserId);
                            userObj.put("username", username);

                            //save to our fireStore database
                            collectionReference.add(userObj)
                                    .addOnSuccessListener(documentReference -> documentReference.get()
                                            .addOnCompleteListener(task1 -> {
                                                if (Objects.requireNonNull(task1.getResult()).exists()) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    String name = task1.getResult()
                                                            .getString("username");

                                                    JournalApi journalApi = JournalApi.getInstance(); //Global API
                                                    journalApi.setUserId(currentUserId);
                                                    journalApi.setUsername(name);

                                                    Intent intent = new Intent(CreateAccountActivity.this,
                                                            PostJournalActivity.class);
                                                    intent.putExtra("username", name);
                                                    intent.putExtra("userId", currentUserId);
                                                    startActivity(intent);


                                                }else {

                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }

                                            }))
                                    .addOnFailureListener(e -> {

                                    });


                        }else {
                            //something went wrong
                        }


                    })
                    .addOnFailureListener(e -> {

                    });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }
}