package com.geocrat.geeksapp.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.geocrat.geeksapp.note_activity.NotesDetailActivity;
import com.geocrat.geeksapp.R;

import java.util.ArrayList;
import java.util.Random;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    ArrayList<NotesModel> notes;
    Context context;

    public NotesAdapter(ArrayList<NotesModel> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
        NotesViewHolder holder = new NotesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NotesViewHolder holder, final int position) {
        NotesModel notesModel = notes.get(position);
        final int code = getRandomColor();
        holder.title.setText(notesModel.getTitle());
        holder.content.setText(notesModel.getContent());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotesDetailActivity.class);
                intent.putExtra("title",holder.title.getText());
                intent.putExtra("content",holder.content.getText());
                intent.putExtra("code",code);
                context.startActivity(intent);
            }
        });

        holder.notesCard.setBackgroundColor(holder.title.getResources().getColor(code, null));
    }

    @Override
    public int getItemCount() {
        return notes.size();
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

    class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView title;
        CardView notesCard;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titles);
            content = itemView.findViewById(R.id.content);
            notesCard = itemView.findViewById(R.id.noteCard);
        }
    }
}
