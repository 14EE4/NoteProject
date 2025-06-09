package com.example.noteproject25_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;//노트 목록을 보기 위한 리사이클러뷰
    private NoteAdapter adapter;
    private FloatingActionButton addButton;//새 노트
    private static final String NOTE_ID = "note_id";
    private static final String NOTE_TITLE = "note_title";
    private static final String NOTE_CONTENT = "note_content";
    private ActivityResultLauncher<Intent> editNoteLauncher;
    private ActivityResultLauncher<Intent> addNoteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.noteRecyclerView);
        addButton = findViewById(R.id.addNoteButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Note> notes = NoteManager.getNotes(this);//노트 리스트
        adapter = new NoteAdapter(notes);//리사이클러뷰 사용을 위한 어댑터
        recyclerView.setAdapter(adapter);

        // EditNoteActivity 결과 처리를 위한 Launcher
        editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // EditNoteActivity에서 노트가 저장/수정되었을 때의 처리
                        androidx.media3.common.util.Log.d("MainActivity", "EditNoteLauncher: 노트 수정/저장 결과 받음");

                        // NoteManager를 통해 실제 저장 또는 업데이트 수행
                        Intent data = result.getData();
                        String noteId = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_ID);
                        String title = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_TITLE);
                        String content = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_CONTENT);
                        String imageUri = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_IMAGE_URI);

                        if (noteId != null) {
                            Note updatedNote = new Note(noteId, title, content, imageUri);
                            NoteManager.saveNote(this, updatedNote); // 또는 NoteManager.updateNote(this, updatedNote);
                            androidx.media3.common.util.Log.d("MainActivity", "EditNoteLauncher: Note updated/saved with ID: " + noteId);
                        }
                        refreshNoteList(); // 목록 새로고침
                    } else {
                        androidx.media3.common.util.Log.d("MainActivity", "EditNoteLauncher: 결과가 OK가 아니거나 데이터가 null임. ResultCode: " + result.getResultCode());
                    }
                });

        // AddNoteActivity 결과 처리를 위한 Launcher
        addNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        androidx.media3.common.util.Log.d("MainActivity", "AddNoteLauncher: 새 노트 저장 결과 받음");
                        Intent data = result.getData();
                        String noteId = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_ID);
                        String title = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_TITLE);
                        String content = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_CONTENT);
                        String imageUri = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_IMAGE_URI);

                        if (noteId != null) { // EditNoteActivity에서 ID를 항상 생성하므로 null이 아니어야 함
                            Note newNote = new Note(noteId, title, content, imageUri);
                            NoteManager.saveNote(this, newNote);
                            androidx.media3.common.util.Log.d("MainActivity", "AddNoteLauncher: New note saved with ID: " + noteId);
                        }
                        refreshNoteList();
                    } else {
                        androidx.media3.common.util.Log.d("MainActivity", "AddNoteLauncher: 결과가 OK가 아니거나 데이터가 null임. ResultCode: " + result.getResultCode());
                    }
                });


        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra(NOTE_ID, note.getId());
                intent.putExtra(NOTE_TITLE, note.getTitle());
                intent.putExtra(NOTE_CONTENT, note.getContent());
                startActivity(intent);//액티비티 이동
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                startActivity(intent);
            }
        });
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra(EditNoteActivity.EXTRA_NOTE_ID, note.getId());
                intent.putExtra(EditNoteActivity.EXTRA_NOTE_TITLE, note.getTitle());
                intent.putExtra(EditNoteActivity.EXTRA_NOTE_CONTENT, note.getContent());
                if (note.getImageUri() != null && !note.getImageUri().isEmpty()) {
                    intent.putExtra(EditNoteActivity.EXTRA_NOTE_IMAGE_URI, note.getImageUri());
                }
                editNoteLauncher.launch(intent); // Launcher로 실행
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                addNoteLauncher.launch(intent); // Launcher로 실행
            }
        });

        refreshNoteList();
    }

    @Override
    protected void onResume() {//노트 업데이트시
        super.onResume();
        List<Note> notes = NoteManager.getNotes(this);
        adapter.setNotes(notes);
        adapter.notifyDataSetChanged();
    }
    private void refreshNoteList() {
        List<Note> notes = NoteManager.getNotes(this);
        // ListAdapter를 사용한다면 adapter.submitList(notes);
        // 현재 Adapter를 사용한다면 adapter.setNotes(notes);
        // setNotes 내부 또는 여기서 notifyDataSetChanged() 호출
        if (adapter != null) { // 어댑터가 null이 아닌지 확인
            adapter.setNotes(notes); // 이 메소드 내부에서 notifyDataSetChanged()가 호출될 것으로 예상
        }
        androidx.media3.common.util.Log.d("MainActivity", "Note list refreshed. Item count: " + (notes != null ? notes.size() : 0));

    }
}