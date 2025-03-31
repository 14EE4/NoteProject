package com.example.noteproject25_1;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NoteManager {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String NOTES_KEY = "notes";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void deleteNote(Context context, Note note) {
        List<Note> notes = getNotes(context);
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(note.getId())) {
                notes.remove(i);
            }
        }
        saveNotes(context, notes);
    }

    public static void updateNote(Context context, Note updatedNote) {
        List<Note> notes = getNotes(context);
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(updatedNote.getId())) {
                notes.set(i, updatedNote); // 노트를 업데이트
                break;
            }
        }
        saveNotes(context, notes); // 수정된 노트를 저장
    }

    public static void saveNote(Context context, Note note) {
        List<Note> notes = getNotes(context);
        boolean isUpdated = false;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(note.getId())) {
                notes.set(i, note); // 기존 노트를 업데이트
                isUpdated = true;
                break;
            }
        }
        if (!isUpdated) {
            notes.add(note); // 새로운 노트를 추가
        }
        saveNotes(context, notes);
    }

    public static List<Note> getNotes(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        String json = prefs.getString(NOTES_KEY, null);
        if (json == null) {
            List<Note> initialNotes = createInitialNotes(); // 테스트 노트를 생성하는 함수 호출
            saveNotes(context, initialNotes); // 생성된 노트를 저장
            return initialNotes; // 생성된 노트를 반환
        }
        Type type = new TypeToken<List<Note>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static Note getNoteById(Context context, String noteId) {
        List<Note> notes = getNotes(context);
        for (Note note : notes) {
            if (note.getId().equals(noteId)) {
                return note;
            }
        }
        return null; // 해당 ID를 가진 노트를 찾지 못한 경우
    }

    private static void saveNotes(Context context, List<Note> notes) {
        SharedPreferences prefs = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(notes);
        editor.putString(NOTES_KEY, json);
        editor.apply();
    }

    // 테스트 노트를 생성하는 함수
    private static List<Note> createInitialNotes() {
        List<Note> initialNotes = new ArrayList<>();
        initialNotes.add(new Note(String.valueOf(System.currentTimeMillis()),"테스트 제목 1", "테스트 내용 1"));
        initialNotes.add(new Note(String.valueOf(System.currentTimeMillis()),"테스트 제목 2", "테스트 내용 2"));
        initialNotes.add(new Note(String.valueOf(System.currentTimeMillis()),"테스트 제목 3", "테스트 내용 3"));
        return initialNotes;
    }
}