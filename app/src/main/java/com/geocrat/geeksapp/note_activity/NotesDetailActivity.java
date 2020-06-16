package com.geocrat.geeksapp.note_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.geocrat.geeksapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NotesDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    FrameLayout frame;
    TextView note;
    FloatingActionButton buttonEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        toolbar = findViewById(R.id.toolbar);
        frame = findViewById(R.id.frame);
        note = findViewById(R.id.note);
        buttonEdit = findViewById(R.id.btnAdd);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesDetailActivity.this, EditActivity.class);
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("content", getIntent().getStringExtra("content"));
                intent.putExtra("code",getIntent().getIntExtra("code",0));
                intent.putExtra("noteId", getIntent().getStringExtra("noteId"));
                startActivity(intent);
            }
        });

        setToolbar();
        setNote();

        frame.setBackgroundColor(getResources().getColor(getIntent().getIntExtra("code", 0), null));
    }

    private void setToolbar(){
        setSupportActionBar(toolbar);
        String title = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setNote(){
        String content = getIntent().getStringExtra("content");
        note.setText(content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
