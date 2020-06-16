package com.geocrat.geeksapp.note_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.geocrat.geeksapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    Toolbar toolbar;
    FrameLayout frame;
    EditText editNote;
    FloatingActionButton btnEditSave;
    FirebaseFirestore fStore;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        fStore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.editToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        frame = findViewById(R.id.frame);
        frame.setBackgroundColor(getResources().getColor(getIntent().getIntExtra("code", 0), null));

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        editNote = findViewById(R.id.editNote);
        editNote.setText(getIntent().getStringExtra("content"));

        btnEditSave = findViewById(R.id.btnsaveEdit);
        btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("content").equals(editNote.getText().toString())){
                    Toast.makeText(EditActivity.this, "Note is already saved", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (editNote.getText().equals(getIntent().getStringExtra("content"))){
                        Toast.makeText(EditActivity.this, "Note is already saved", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    DocumentReference docRef = fStore.collection("notes").
                            document(FirebaseAuth.getInstance().getUid())
                            .collection("myNotes").document(getIntent().getStringExtra("noteId"));
                    Map<String , Object> note  = new HashMap<>();
                    note.put("title", getIntent().getStringExtra("title"));
                    note.put("content", editNote.getText().toString());
                    docRef.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditActivity.this, "Note cannot be saved. \n Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });
    }
}
