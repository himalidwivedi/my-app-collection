package com.geocrat.geeksapp.note_fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.geocrat.geeksapp.R;
import com.geocrat.geeksapp.model.NotesModel;
import com.geocrat.geeksapp.note_activity.EditActivity;
import com.geocrat.geeksapp.note_activity.MainActivity;
import com.geocrat.geeksapp.note_activity.NotesDetailActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {

    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<NotesModel, NoteViewHolder> noteAdapter;

    RecyclerView noteList;
    ArrayList<NotesModel> notes = new ArrayList<>();
    FloatingActionButton addNotes;

    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("notes").document(FirebaseAuth.getInstance().getUid())
                .collection("myNotes").orderBy("title", Query.Direction.DESCENDING);
        /*
            flow of query in firestore be like:
             query notes > uuid > mynotes
        */
        FirestoreRecyclerOptions<NotesModel> allNotes = new FirestoreRecyclerOptions.Builder<NotesModel>()
                .setQuery(query, NotesModel.class).build();
            noteAdapter = new FirestoreRecyclerAdapter<NotesModel, NoteViewHolder>(allNotes) {
                @Override
                protected void onBindViewHolder(@NonNull final NoteViewHolder noteViewHolder, int i, @NonNull NotesModel notesModel) {
                    final int code = getRandomColor();
                    final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                    noteViewHolder.title.setText(notesModel.getTitle());
                    noteViewHolder.content.setText(notesModel.getContent());
                    noteViewHolder.title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), NotesDetailActivity.class);
                            intent.putExtra("title",noteViewHolder.title.getText());
                            intent.putExtra("content",noteViewHolder.content.getText());
                            intent.putExtra("code",code);
                            intent.putExtra("noteId", docId);
                            getActivity().startActivity(intent);
                        }
                    });

                    noteViewHolder.notesCard.setBackgroundColor(noteViewHolder.title.getResources().getColor(code, null));

                    noteViewHolder.menuIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu menu = new PopupMenu(v.getContext(), v);
                            menu.setGravity(Gravity.END);
                            menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    Intent intent = new Intent(getActivity(), EditActivity.class);
                                    intent.putExtra("title",noteViewHolder.title.getText());
                                    intent.putExtra("content",noteViewHolder.content.getText());
                                    intent.putExtra("code",code);
                                    intent.putExtra("noteId", docId);
                                    getActivity().startActivity(intent);
                                    return false;
                                }
                            });

                            menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    DocumentReference docRef = fStore.collection("notes").
                                            document(FirebaseAuth.getInstance().getUid())
                                            .collection("myNotes").document(docId);
                                    docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getActivity(), "Note deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Note cannot be deleted. \n Some error occured", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return false;
                                }
                            });

                            menu.show();
                        }
                    });
                }

                @NonNull
                @Override
                public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
                    NoteViewHolder holder = new NoteViewHolder(view);
                    return holder;
                }
            };


        noteList = view.findViewById(R.id.recyclerView);
        addNotes = view.findViewById(R.id.addNew);
        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.main_activity));
                startActivity(intent);
            }
        });
        setUpData();
        return view;
    }

    private int getRandomColor(){
        ArrayList<Integer> colorCodes = new ArrayList<>();
        colorCodes.add(R.color.yellow);
        colorCodes.add(R.color.lightGreen);
        colorCodes.add(R.color.pink);
        colorCodes.add(R.color.lightPurple);
        colorCodes.add(R.color.skyblue);
        colorCodes.add(R.color.gray);
        colorCodes.add(R.color.red);
        colorCodes.add(R.color.blue);
        colorCodes.add(R.color.lightGreen);
        colorCodes.add(R.color.notGreen);
        colorCodes.add(R.color.blackOverlay);

        Random random = new Random();
        int code = random.nextInt(colorCodes.size());
        return colorCodes.get(code);
    }


    private void setUpData(){
        noteList.setAdapter(noteAdapter);
        StaggeredGridLayoutManager layout = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        noteList.setLayoutManager(layout);
    }

    private class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView title;
        CardView notesCard;
        ImageView menuIcon;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titles);
            content = itemView.findViewById(R.id.content);
            notesCard = itemView.findViewById(R.id.noteCard);
            menuIcon = itemView.findViewById(R.id.menuIcon);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }
}
