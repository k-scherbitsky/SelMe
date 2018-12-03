package com.selme.activity;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.selme.R;
import com.selme.adapter.CommentsAdapter;
import com.selme.dto.CommentsDTO;
import com.selme.dto.PostDTO;
import com.selme.interfaces.CommentsDTOCallback;
import com.selme.service.DataMapper;
import com.selme.service.PostService;

import java.util.List;

public class PostCardActivity extends AppCompatActivity implements CommentsDTOCallback {

    private PostDTO postDTO;
    private CommentsAdapter commentsAdapter;

    private ProgressBar progressBarPostCard;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_card);

        progressBarPostCard = findViewById(R.id.post_card_progress_bar);
        recyclerView = findViewById(R.id.post_card_recycler_view);

        postDTO = getIntent().getParcelableExtra("PostCard");
        if(postDTO != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            DataMapper dataMapper = new DataMapper(storageRef);
            dataMapper.toCommentsDto(postDTO.getComments(), this);
            initRecyclerView();
        }
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        commentsAdapter = new CommentsAdapter();
        recyclerView.setAdapter(commentsAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
    }

    @Override
    public void toCommentsDot(List<CommentsDTO> commentsDTOList) {
        commentsAdapter.clearItems();
        commentsAdapter.setItems(commentsDTOList);
        progressBarPostCard.setVisibility(View.GONE);
    }
}
