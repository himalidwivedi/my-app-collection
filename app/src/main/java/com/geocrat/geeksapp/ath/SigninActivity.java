package com.geocrat.geeksapp.ath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geocrat.geeksapp.R;
import com.geocrat.geeksapp.note_activity.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SigninActivity extends AppCompatActivity {

    EditText fullName, email, password, confirmPassword;
    String nFullName, nEmail, nPassword, nConfirmPassword;
    AppCompatButton syncNow;
    TextView login, loadingPleaseWait;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        firebaseAuth = FirebaseAuth.getInstance();
        fullName = findViewById(R.id.input_username);
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        confirmPassword = findViewById(R.id.input_confirm_password);
        syncNow = findViewById(R.id.btn_register);
        login = findViewById(R.id.link_login);
        loadingPleaseWait = findViewById(R.id.loadingPleaseWait);
        loadingPleaseWait.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, LoginActivity.class));
                finish();
            }
        });

        syncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nFullName = fullName.getText().toString();
                nEmail = email.getText().toString();
                nPassword = password.getText().toString();
                nConfirmPassword = confirmPassword.getText().toString();

                if (nFullName.isEmpty() || nEmail.isEmpty() || nPassword.isEmpty() || nConfirmPassword.isEmpty()){
                    Toast.makeText(SigninActivity.this, "All these credentials are mandatory.\n Plaese fill all the above fields", Toast.LENGTH_SHORT).show();
                }
                if (!nPassword.equals(nConfirmPassword)){

                    confirmPassword.setError("\"Password\" must match the \"Confirm Password\"");
                }

                AuthCredential credential = EmailAuthProvider.getCredential(nEmail, nPassword);
                firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(SigninActivity.this, "Notes are synced", Toast.LENGTH_SHORT).show();

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nFullName).build();
                        user.updateProfile(request);

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SigninActivity.this, "Failed to connect. \n Try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
