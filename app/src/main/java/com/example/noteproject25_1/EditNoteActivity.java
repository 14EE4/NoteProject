package com.example.noteproject25_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditNoteActivity extends AppCompatActivity {

    Button saveButton;
    EditText titleEditText, contentEditText;
    private static final String NOTE_ID = "note_id";
    private static final String NOTE_TITLE = "note_title";
    private static final String NOTE_CONTENT = "note_content";
    private String noteId = null;
    private static final String TAG = "EditNoteActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() 시작");
        setContentView(R.layout.activity_edit_note);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);

        // Intent로부터 데이터 받기
        Intent intent = getIntent();
        if (intent != null) {
            noteId = intent.getStringExtra(NOTE_ID);
            String title = intent.getStringExtra(NOTE_TITLE);
            String content = intent.getStringExtra(NOTE_CONTENT);
            if (title != null && content != null) {
                titleEditText.setText(title);
                contentEditText.setText(content);
            }
        }

        // 저장 버튼
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "saveButton 클릭");
                saveNote();
            }
        });

        Log.d(TAG, "onCreate() 종료");
    }

    private void saveNote() {
        Log.d(TAG, "saveNote() 시작");
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (noteId != null) {
            // 기존 노트 업데이트
            Note note = new Note(noteId, title, content);
            NoteManager.updateNote(this, note);
            Log.d(TAG, "노트 업데이트: " + noteId);
        } else {
            // 새로운 노트 추가
            noteId = String.valueOf(System.currentTimeMillis());
            Note note = new Note(noteId, title, content);
            NoteManager.saveNote(this, note);
            Log.d(TAG, "노트 추가: " + noteId);
        }

        Log.d(TAG, "saveNote() 종료");
        finish();
    }
}