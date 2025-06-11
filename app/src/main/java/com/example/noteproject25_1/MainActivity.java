package com.example.noteproject25_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


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

        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);
        adapter.submitList(notes);

        editNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Log.d("MainActivity", "EditNoteLauncher: 노트 수정/저장 결과 받음");

                        Intent data = result.getData();
                        String noteId = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_ID);
                        String title = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_TITLE);
                        String content = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_CONTENT);
                        String imageUri = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_IMAGE_URI);

                        if (noteId != null) {
                            Note updatedNote = new Note(noteId, title, content, imageUri);
                            NoteManager.saveNote(this, updatedNote);
                            Log.d("MainActivity", "EditNoteLauncher: Note updated/saved with ID: " + noteId);
                        }
                        refreshNoteList();
                    } else {
                        Log.d("MainActivity", "EditNoteLauncher: 결과가 OK가 아니거나 데이터가 null임. ResultCode: " + result.getResultCode());
                    }
                });

        addNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Log.d("MainActivity", "AddNoteLauncher: 새 노트 저장 결과 받음");
                        Intent data = result.getData();
                        String noteId = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_ID);
                        String title = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_TITLE);
                        String content = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_CONTENT);
                        String imageUri = data.getStringExtra(EditNoteActivity.EXTRA_NOTE_IMAGE_URI);

                        if (noteId != null) {
                            Note newNote = new Note(noteId, title, content, imageUri);
                            NoteManager.saveNote(this, newNote);
                            Log.d("MainActivity", "AddNoteLauncher: New note saved with ID: " + noteId);
                        }
                        refreshNoteList();
                    } else {
                        Log.d("MainActivity", "AddNoteLauncher: 결과가 OK가 아니거나 데이터가 null임. ResultCode: " + result.getResultCode());
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
        adapter.setOnItemDeleteListener(new NoteAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDelete(Note note, int position) {
                // 확인 다이얼로그 표시 (선택 사항)
                new AlertDialog.Builder(MainActivity.this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                        .setTitle("노트 삭제")
                        .setMessage("'" + note.getTitle() + "' 노트를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", (dialog, which) -> {
                            deleteNoteWithUndo(note, position); // 위에서 구현한 메소드 재활용 가능
                        })
                        .setNegativeButton("취소", null)
                        .show();
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

        refreshNoteList(); // onResume 시 목록을 다시 로드하여 변경사항 반영
    }

    private void refreshNoteList() {
        List<Note> notes = NoteManager.getNotes(this);
        if (adapter != null) {
            adapter.submitList(notes);
        }
        Log.d("MainActivity", "Note list refreshed. Item count: " + (notes != null ? notes.size() : 0));
    }
    private void deleteNoteWithUndo(Note noteToDelete, int position) {

        NoteManager.deleteNote(this, noteToDelete);


        refreshNoteList(); // 어댑터에 새 리스트를 전달하여 UI 업데이트


        Snackbar snackbar = Snackbar.make(recyclerView, "노트가 삭제되었습니다.", Snackbar.LENGTH_LONG);
        snackbar.setAction("실행 취소", v -> {
            //실행 취소 터치시
            NoteManager.saveNote(MainActivity.this, noteToDelete);
            refreshNoteList();

            Snackbar.make(recyclerView, "삭제가 취소되었습니다.", Snackbar.LENGTH_SHORT).show();
        });
        snackbar.show();
    }
}