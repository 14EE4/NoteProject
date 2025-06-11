package com.example.noteproject25_1;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 노트 관리 클래스
 * 노트를 저장하고 로드하는 데 사용
 * 기본값으로 테스트 노트 3개 생성
 */
public class NoteManager {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String NOTES_KEY = "notes";

    private static SharedPreferences getSharedPreferences(Context context) {
        if (context == null) {
            // Context가 null인 경우에 대한 처리 (예: 예외 발생 또는 기본값 반환)
            // Log.e("NoteManager", "Context is null in getSharedPreferences");
            throw new IllegalArgumentException("Context cannot be null");
        }
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void deleteNote(Context context, Note noteToDelete) {
        List<Note> notes = getNotes(context);

        boolean removed = notes.removeIf(note -> note.getId().equals(noteToDelete.getId()));
        if (removed) {
            saveNotes(context, notes);
        }

    }


    public static synchronized void saveNote(Context context, Note noteToSave) {
        List<Note> notes = getNotes(context);
        boolean isUpdated = false;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(noteToSave.getId())) {
                notes.set(i, noteToSave); // 기존 노트를 업데이트
                isUpdated = true;
                break;
            }
        }
        if (!isUpdated) {
            notes.add(noteToSave); // 새로운 노트를 추가
        }
        saveNotes(context, notes);
    }

    public static List<Note> getNotes(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        String json = prefs.getString(NOTES_KEY, null);
        if (json == null) {
            List<Note> initialNotes = createInitialNotes(); // Context 전달 제거 (또는 필요시 유지)
            saveNotes(context, initialNotes);
            return initialNotes;
        }
        try {
            Type type = new TypeToken<ArrayList<Note>>() {}.getType();
            List<Note> loadedNotes = new Gson().fromJson(json, type);
            return loadedNotes != null ? loadedNotes : new ArrayList<>(); // null 반환 방지
        } catch (Exception e) { // Gson 파싱 오류 등 예외 처리
            // Log.e("NoteManager", "Error parsing notes from JSON", e);
            return new ArrayList<>(); // 오류 발생 시 빈 리스트 반환
        }
    }

    private static synchronized void saveNotes(Context context, List<Note> notes) {
        SharedPreferences prefs = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(notes);
        editor.putString(NOTES_KEY, json);
        editor.apply();
    }

    // 테스트 노트를 생성하는 함수
    private static List<Note> createInitialNotes() {
        List<Note> initialNotes = new ArrayList<>();
        // 4개의 인자를 받는 Note 생성자 사용 (id, title, content, imageUri)
        // imageUri는 초기값이 없으므로 null로 설정
        initialNotes.add(new Note(UUID.randomUUID().toString(), "테스트 제목 1", "테스트 내용 1", null));
        initialNotes.add(new Note(UUID.randomUUID().toString(), "테스트 제목 2", "테스트 내용 2", null));
        initialNotes.add(new Note(UUID.randomUUID().toString(), "테스트 제목 3", "테스트 내용 3", null));
        return initialNotes;
    }
}