package com.example.noteproject25_1;

/**
 * 노트 클래스
 * 노트의 ID, 제목, 내용, 이미지 URI를 저장
 *
 */
public class Note {
    private String id;
    private String title;
    private String content; // 노트의 텍스트 내용
    private String imageUri;  // 노트에 첨부된 이미지의 URI (문자열 형태)




    // 모든 필드를 초기화하는 생성자
    public Note(String id, String title, String content, String imageUri) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUri = imageUri;
    }

    // Getter 메소드
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImageUri() {
        return imageUri;
    }

    // Setter 메소드
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


}