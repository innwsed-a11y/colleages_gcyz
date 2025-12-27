package com.example.colleagues_items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> comments;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onDeleteClick(int commentId);
    }

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvCommenter.setText(comment.getCommenter());
        holder.tvCommentContent.setText(comment.getContent());
        holder.tvCommentDate.setText(comment.getDate());
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommenter;
        TextView tvCommentContent;
        TextView tvCommentDate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommenter = itemView.findViewById(R.id.tv_commenter);
            tvCommentContent = itemView.findViewById(R.id.tv_comment_content);
            tvCommentDate = itemView.findViewById(R.id.tv_comment_date);
        }
    }
}