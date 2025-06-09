package com.example.noteproject25_1;

// Note.java
// 수정: 간단한 텍스트 내용과 이미지 URI 하나를 저장하는 형태로 변경
public class Note {
    private String id;
    private String title;
    private String content; // 노트의 텍스트 내용
    private String imageUri;  // 노트에 첨부된 이미지의 URI (문자열 형태)

    // 기본 생성자 (예: Firebase와 같은 일부 라이브러리에서 필요할 수 있음)
    public Note() {
    }

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

    // 필요하다면 equals()와 hashCode() 메소드를 오버라이드하여 객체 비교를 용이하게 할 수 있습니다.
    // 또한, toString() 메소드를 오버라이드하여 디버깅 시 유용하게 사용할 수 있습니다.
}