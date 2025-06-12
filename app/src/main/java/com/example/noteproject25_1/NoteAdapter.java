package com.example.noteproject25_1;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

/**
 * 노트 어댑터
 * 노트 목록을 표시하는 데 사용
 * 노트의 제목, 내용, 이미지를 표시
 * 노트를 클릭하면 편집할 수 있음
 * 노트를 삭제할 수 있음
 *
 */
public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> { // ListAdapter 사용
    private OnItemClickListener listener;
    private OnItemDeleteListener deleteListener;


    public NoteAdapter() {
        super(DIFF_CALLBACK);// DiffUtil 사용

    }


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

            boolean titleSame = oldItem.getTitle() != null ? oldItem.getTitle().equals(newItem.getTitle()) : newItem.getTitle() == null;
            boolean contentSame = oldItem.getContent() != null ? oldItem.getContent().equals(newItem.getContent()) : newItem.getContent() == null;
            boolean imageUriSame = oldItem.getImageUri() != null ? oldItem.getImageUri().equals(newItem.getImageUri()) : newItem.getImageUri() == null;
            return titleSame && contentSame && imageUriSame;
        }
    };


    public interface OnItemClickListener {
        void onItemClick(Note note);
    }
    public interface OnItemDeleteListener { // 삭제 리스너 인터페이스
        void onItemDelete(Note note, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public void setOnItemDeleteListener(OnItemDeleteListener deleteListener) { // 삭제 리스너 설정 메소드
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_card, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        Note note = getItem(position);

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

        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(note); // 클릭 시 해당 Note 객체 전달
            }
        });

        // 삭제 버튼 클릭 리스너 설정
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onItemDelete(note, holder.getAdapterPosition());
            }
        });
    }



    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        ImageView noteImageView;
        ImageButton deleteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            noteImageView = itemView.findViewById(R.id.noteCardImageView);
            deleteButton = itemView.findViewById(R.id.deleteNoteButton);

        }
    }

   
}