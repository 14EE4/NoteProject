package com.example.noteproject25_1;

import android.net.Uri; // Uri import 추가
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // ImageView import 추가
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter; // ListAdapter 사용 권장
import androidx.recyclerview.widget.DiffUtil;    // ListAdapter 사용 시 필요
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Glide import 추가

import java.util.List;

// ListAdapter를 사용하는 것을 강력히 권장합니다.
// public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> { // 이전 코드
public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> { // ListAdapter 사용
    private OnItemClickListener listener;

    // ListAdapter 사용 시 생성자 변경
    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    // DiffUtil.ItemCallback 구현 (ListAdapter에 필요)
    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            if (oldItem.getId() == null || newItem.getId() == null) { // ID가 null일 수 있는 경우 대비
                return oldItem == newItem; // 객체 자체 비교 또는 다른 로직
            }
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            // 모든 필드를 비교하여 내용 변경 여부 확인
            // Note 클래스에 equals()를 제대로 구현했다면 oldItem.equals(newItem) 사용 가능
            boolean titleSame = oldItem.getTitle() != null ? oldItem.getTitle().equals(newItem.getTitle()) : newItem.getTitle() == null;
            boolean contentSame = oldItem.getContent() != null ? oldItem.getContent().equals(newItem.getContent()) : newItem.getContent() == null;
            boolean imageUriSame = oldItem.getImageUri() != null ? oldItem.getImageUri().equals(newItem.getImageUri()) : newItem.getImageUri() == null;
            return titleSame && contentSame && imageUriSame;
        }
    };


    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_card, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        // Note note = notes.get(position); // RecyclerView.Adapter 사용 시
        Note note = getItem(position); // ListAdapter 사용 시

        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());

        // 이미지 URI가 있는지 확인하고 Glide로 로드
        if (note.getImageUri() != null && !note.getImageUri().isEmpty()) {
            holder.noteImageView.setVisibility(View.VISIBLE); // 이미지가 있으면 보이도록 설정
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(note.getImageUri())) // 문자열 URI를 Uri 객체로 파싱
                    .placeholder(R.drawable.ic_placeholder_image) // 로딩 중 표시할 이미지 (선택 사항)
                    .error(R.drawable.ic_image_load_error)       // 에러 시 표시할 이미지 (선택 사항)
                    .into(holder.noteImageView);
        } else {
            holder.noteImageView.setVisibility(View.GONE); // 이미지가 없으면 숨김
            // Glide.with(holder.itemView.getContext()).clear(holder.noteImageView); // 이전 이미지 메모리 해제 (선택 사항)
            // holder.noteImageView.setImageDrawable(null); // ImageView 내용 비우기
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(note); // 클릭 시 해당 Note 객체 전달
            }
        });
    }

    // getItemCount()는 ListAdapter가 내부적으로 처리하므로 오버라이드할 필요 없음 (필요시 super.getItemCount() 또는 현재 리스트 크기 반환)

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        ImageView noteImageView; // ImageView 참조 추가

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            noteImageView = itemView.findViewById(R.id.noteCardImageView); // 레이아웃의 ImageView ID와 일치
        }
    }

   
}