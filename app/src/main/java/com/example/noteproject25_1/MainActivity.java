package com.example.noteproject25_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private FloatingActionButton addButton;
    private static final String NOTE_ID = "note_id";
    private static final String NOTE_TITLE = "note_title";
    private static final String NOTE_CONTENT = "note_content";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.noteRecyclerView);
        addButton = findViewById(R.id.addNoteButton); // 수정된 부분

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Note> notes = NoteManager.getNotes(this);
        adapter = new NoteAdapter(notes);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra(NOTE_ID, note.getId());
                intent.putExtra(NOTE_TITLE, note.getTitle());
                intent.putExtra(NOTE_CONTENT, note.getContent());
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() { // 수정된 부분
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Note> notes = NoteManager.getNotes(this);
        adapter.setNotes(notes);
        adapter.notifyDataSetChanged();
    }
}