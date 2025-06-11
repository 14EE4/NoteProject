package com.example.noteproject25_1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


public class EditNoteActivity extends AppCompatActivity {

    private static final String TAG = "EditNoteActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    // Intent로 데이터를 주고받기 위한 키 값들

    public static final String EXTRA_NOTE_ID = "com.example.noteproject25_1.NOTE_ID";
    public static final String EXTRA_NOTE_TITLE = "com.example.noteproject25_1.NOTE_TITLE";
    public static final String EXTRA_NOTE_CONTENT = "com.example.noteproject25_1.NOTE_CONTENT";
    public static final String EXTRA_NOTE_IMAGE_URI = "com.example.noteproject25_1.NOTE_IMAGE_URI";

    EditText titleEditText, contentEditText;
    ImageView noteImageView;
    FrameLayout imageDisplayContainer; // 이미지를 보여주는 FrameLayout
    ImageButton saveButton, addImageButton;

    private String currentNoteId; // 현재 편집 중인 노트의 ID (새 노트면 null)
    private Uri currentImageUri;  // 현재 선택된 이미지의 URI

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note); // activity_edit_note.xml 레이아웃 사용

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        noteImageView = findViewById(R.id.noteImageView);
        imageDisplayContainer = findViewById(R.id.imageDisplayContainer);
        saveButton = findViewById(R.id.saveButton);
        addImageButton = findViewById(R.id.addImageButton);

        // Intent로부터 노트 데이터 로드 (기존 노트 수정 시)
        loadNoteData();

        // 버튼 클릭 리스너 설정
        addImageButton.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> saveNote());

        // 초기 이미지 컨테이너 가시성 업데이트
        updateImageContainerVisibility();
    }

    /**
     * Intent에 담겨 넘어온 기존 노트 데이터를 UI에 로드
     */
    private void loadNoteData() {
        Intent intent = getIntent();
        if (intent != null) {
            // 노트 ID가 있으면 기존 노트로 간주
            if (intent.hasExtra(EXTRA_NOTE_ID)) {
                currentNoteId = intent.getStringExtra(EXTRA_NOTE_ID);
            }
            titleEditText.setText(intent.getStringExtra(EXTRA_NOTE_TITLE));
            contentEditText.setText(intent.getStringExtra(EXTRA_NOTE_CONTENT));

            String imageUriString = intent.getStringExtra(EXTRA_NOTE_IMAGE_URI);
            if (imageUriString != null && !imageUriString.isEmpty()) {
                currentImageUri = Uri.parse(imageUriString);
                Log.d(TAG, "로드된 이미지 URI: " + currentImageUri.toString());
            }
        }
    }

    /**
     * 갤러리를 열어 이미지 선택
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


        // resolveActivity는 이 Intent를 처리할 수 있는 액티비티가 있는지 확인합니다.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            Toast.makeText(this, "갤러리 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 갤러리에서 이미지 선택 결과를 처리합니다.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // 이미지가 성공적으로 선택됨
            currentImageUri = data.getData(); // 선택된 이미지의 URI
            Log.d(TAG, "선택된 이미지 URI: " + currentImageUri.toString());

            // 이미지 뷰 업데이트
            updateImageContainerVisibility();


        }
    }

    /**
     * 이미지가 있으면 이미지 컨테이너의 가시성을 업데이트하고 이미지를 로드
     */
    private void updateImageContainerVisibility() {
        if (currentImageUri != null) {
            imageDisplayContainer.setVisibility(View.VISIBLE); // 이미지가 있으면 컨테이너 보이기
            Glide.with(this) // Glide 라이브러리를 사용하여 이미지 로드
                    .load(currentImageUri)
                    .placeholder(R.drawable.ic_placeholder_image) // 로딩 중 표시될 이미지
                    .error(R.drawable.ic_image_load_error)       // 로드 실패 시 표시될 이미지
                    .into(noteImageView);
        } else {
            imageDisplayContainer.setVisibility(View.GONE); // 이미지가 없으면 컨테이너 숨기기

        }
    }

    /**
     * 현재 노트의 제목, 내용, 이미지 URI를 저장(결과로 반환)하고 액티비티를 종료
     */
    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        // 제목, 내용, 이미지가 모두 비어있으면 저장하지 않음
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content) && currentImageUri == null) {
            Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent resultIntent = new Intent();

        // 새 노트인 경우 (currentNoteId가 null), 새로운 ID를 생성

        if (currentNoteId == null) {
            currentNoteId = String.valueOf(System.currentTimeMillis()); // 임시 ID 생성 방식
        }
        resultIntent.putExtra(EXTRA_NOTE_ID, currentNoteId);
        resultIntent.putExtra(EXTRA_NOTE_TITLE, title);
        resultIntent.putExtra(EXTRA_NOTE_CONTENT, content);

        if (currentImageUri != null) {
            resultIntent.putExtra(EXTRA_NOTE_IMAGE_URI, currentImageUri.toString());
        }
        // 이미지가 없는 경우 EXTRA_NOTE_IMAGE_URI 키 자체가 포함되지 않거나 null로 전달

        setResult(RESULT_OK, resultIntent); // 결과를 성공으로 설정하고 Intent 전달
        Log.d(TAG, "노트 저장 완료. ID: " + currentNoteId);
        finish(); // 현재 액티비티 종료
    }
}