package com.selme.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.selme.R;
import com.selme.dto.CommentsDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private List<CommentsDTO> commentsDTOList = new ArrayList<>();

    public void setItems(Collection<CommentsDTO> comments){
        commentsDTOList.addAll(comments);
        notifyDataSetChanged();
    }

    public void clearItems(){
        commentsDTOList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comment_item_view, viewGroup, false);

        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder commentsViewHolder, int i) {
        commentsViewHolder.bind(commentsDTOList.get(i));
    }

    @Override
    public int getItemCount() {
        return commentsDTOList.size();
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder{

        private ImageView avatar;
        private TextView userName;
        private TextView comment;

        CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.comment_avatar);
            userName = itemView.findViewById(R.id.comment_username);
            comment = itemView.findViewById(R.id.comment_text_view);
        }

        void bind(CommentsDTO commentsDTO) {
            if(commentsDTO != null) {

                Glide.with(itemView.getContext())
                        .load(commentsDTO.getAvatar())
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatar);

                userName.setText(commentsDTO.getUserName());
                comment.setText(commentsDTO.getComment());
            }
        }
    }
}
