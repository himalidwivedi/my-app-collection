package com.geocrat.geeksapp.note_fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNoteFragment extends Fragment {

    FirebaseFirestore fStore;
    FloatingActionButton save;
    EditText note;
    String nTitle, nContent;
    Dialog dialog;

    public AddNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_add_note, container, false);

        fStore = FirebaseFirestore.getInstance();
        note = view.findViewById(R.id.note);

        dialog = new Dialog(getActivity());
        save = view.findViewById(R.id.saveNote);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nContent = note.getText().toString();
                if (nContent.isEmpty()){
                    Toast.makeText(getActivity(), "Note is empty \n Please write something", Toast.LENGTH_SHORT).show();
                }else{
                    showSavePopup();
                }
            }
        });
        return view;
    }

    public void showSavePopup(){
        final Button saveNote;
        final EditText title;
        dialog.setContentView(R.layout.save_dialog);
        saveNote = dialog.findViewById(R.id.saveInFirebase);
        title = dialog.findViewById(R.id.title);
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nTitle = title.getText().toString();
                if (nTitle.isEmpty()){
                    Toast.makeText(getActivity(), "Title is empty \n Please write something", Toast.LENGTH_SHORT).show();
                }else{
                    //save note
                    final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    DocumentReference docRef = fStore.collection("notes").
                    document(FirebaseAuth.getInstance().getUid())
                            .collection("myNotes").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", nTitle);
                    note.put("content", nContent);

                    docRef.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Note added successfully", Toast.LENGTH_SHORT).show();;
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Note cannot be added.\n Some error occured", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        dialog.show();
    }
}
