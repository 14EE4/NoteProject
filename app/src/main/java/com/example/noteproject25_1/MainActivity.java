package com.example.noteproject25_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // <<< 이 import 문으로 변경 또는 추가
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
// import androidx.media3.common.util.UnstableApi; // android.util.Log를 사용하면 이 어노테이션은 필요 없을 수 있습니다 (Media3의 다른 UnstableApi를 사용하지 않는다면)
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

// @UnstableApi // 만약 androidx.media3.common.util.Log 외에 다른 UnstableApi를 사용하지 않는다면 이 어노테이션 제거 가능
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private FloatingActionButton addButton;
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

        List<Note> notes = NoteManager.getNotes(this);
        // adapter = new NoteAdapter(notes); // 이전 어댑터 생성 방식
        adapter = new NoteAdapter(); // ListAdapter 사용 시
        recyclerView.setAdapter(adapter);
        adapter.submitList(notes);

        editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Log.d("MainActivity", "EditNoteLauncher: 노트 수정/저장 결과 받음"); // 변경됨

                        Intent data = result.getData();
                        String noteId = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_ID);
                        String title = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_TITLE);
                        String content = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_CONTENT);
                        String imageUri = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_IMAGE_URI);

                        if (noteId != null) {
                            Note updatedNote = new Note(noteId, title, content, imageUri);
                            NoteManager.saveNote(this, updatedNote);
                            Log.d("MainActivity", "EditNoteLauncher: Note updated/saved with ID: " + noteId); // 변경됨
                        }
                        refreshNoteList();
                    } else {
                        Log.d("MainActivity", "EditNoteLauncher: 결과가 OK가 아니거나 데이터가 null임. ResultCode: " + result.getResultCode()); // 변경됨
                    }
                });

        addNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Log.d("MainActivity", "AddNoteLauncher: 새 노트 저장 결과 받음"); // 변경됨
                        Intent data = result.getData();
                        String noteId = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_ID);
                        String title = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_TITLE);
                        String content = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_CONTENT);
                        String imageUri = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_IMAGE_URI);

                        if (noteId != null) {
                            Note newNote = new Note(noteId, title, content, imageUri);
                            NoteManager.saveNote(this, newNote);
                            Log.d("MainActivity", "AddNoteLauncher: New note saved with ID: " + noteId); // 변경됨
                        }
                        refreshNoteList();
                    } else {
                        Log.d("MainActivity", "AddNoteLauncher: 결과가 OK가 아니거나 데이터가 null임. ResultCode: " + result.getResultCode()); // 변경됨
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
                editNoteLauncher.launch(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                addNoteLauncher.launch(intent);
            }
        });

        refreshNoteList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // List<Note> notes = NoteManager.getNotes(this); // refreshNoteList에서 호출되므로 중복될 수 있음
        // adapter.submitList(notes);
        refreshNoteList(); // onResume 시 목록을 다시 로드하여 변경사항 반영
    }

    private void refreshNoteList() {
        List<Note> notes = NoteManager.getNotes(this);
        if (adapter != null) {
            adapter.submitList(notes);
        }
        Log.d("MainActivity", "Note list refreshed. Item count: " + (notes != null ? notes.size() : 0)); // 변경됨
    }
}