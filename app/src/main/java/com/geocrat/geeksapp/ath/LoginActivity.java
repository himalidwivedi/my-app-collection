package com.geocrat.geeksapp.ath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geocrat.geeksapp.R;
import com.geocrat.geeksapp.note_activity.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText email, password;
    String nEmail, nPassword;
    AppCompatButton login;
    TextView createAccount, loadingPleaseWait;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_login);
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        login = findViewById(R.id.btn_login);
        createAccount = findViewById(R.id.link_signup);
        progressBar = findViewById(R.id.progressBar);
        loadingPleaseWait = findViewById(R.id.pleaseWait);
        loadingPleaseWait.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SigninActivity.class));
                finish();
            }
        });

        showWarning();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nEmail = email.getText().toString();
                nPassword = password.getText().toString();
                if (nEmail.isEmpty() || nPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "All the credentials are mandatory.\n fill all of the above fields", Toast.LENGTH_SHORT).show();
                }

                //delete notes first
                progressBar.setVisibility(View.VISIBLE);
                loadingPleaseWait.setVisibility(View.VISIBLE);
                try {
                    if (firebaseAuth.getCurrentUser().isAnonymous()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        firestore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "All the temporary data deleted", Toast.LENGTH_SHORT).show();
                            }
                        });

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Temporary user also deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    firebaseAuth.signInWithEmailAndPassword(nEmail, nPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            loadingPleaseWait.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login Failed.\n Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (NullPointerException e){
                    Log.e(TAG, "onClick: NullPointerException " + e.getMessage());
                }
            }
        });
    }

    private void showWarning() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?").setMessage("Linking existing account will delete all the temporary notes. Create new account to save them")
                .setPositiveButton("Save Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                        finish();
                    }
                }).setNegativeButton("Its ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: need to delete the annonymous user

                    }
                });
        warning.create();
        warning.show();
    }
}












