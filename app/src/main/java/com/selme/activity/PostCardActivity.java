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
    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private CommentsAdapter commentsAdapter;

    private ProgressBar progressBarPostCard;
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private Button thisOneButton1;
    private Button thisOneButton2;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_card);

        progressBarPostCard = findViewById(R.id.post_card_progress_bar);
        progressBar1 = findViewById(R.id.post_card_progressBar1);
        progressBar2 = findViewById(R.id.post_card_progressBar2);
        thisOneButton1 = findViewById(R.id.post_card_thisOneButton1);
        thisOneButton2 = findViewById(R.id.post_card_thisOneButton2);
        recyclerView = findViewById(R.id.post_card_recycler_view);

        postDTO = getIntent().getParcelableExtra("PostCard");
        if(postDTO != null) {
            initPostCard(postDTO);
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
        DividerItemDecoration divider = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
    }

    private void initPostCard(PostDTO postDTO) {
        ImageView avatar = findViewById(R.id.post_card_avatar);
        Glide.with(getBaseContext())
                .load(postDTO.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .into(avatar);

        TextView userName = findViewById(R.id.post_card_username);
        userName.setText(postDTO.getUserName());

        TextView title = findViewById(R.id.post_card_title_text_view);
        title.setText(postDTO.getTitle());

        TextView description = findViewById(R.id.post_card_description_text_view);
        description.setText(postDTO.getDescription());

        ImageView picture1 = findViewById(R.id.post_card_image1);
        Glide.with(getBaseContext())
                .load(postDTO.getPicture1())
                .into(picture1);

        ImageView picture2 = findViewById(R.id.post_card_image2);
        Glide.with(getBaseContext())
                .load(postDTO.getPicture2())
                .into(picture2);

        List<String> votedUserIds = postDTO.getVotedUserIds();
        PostService service = new PostService();
        int pickPic1 = postDTO.getPickPic1();
        int pickPic2 = postDTO.getPickPic2();
        int amountPick = pickPic1 + pickPic2;
        if(votedUserIds.contains(currentUserId)){
            progressBar1.setProgress(service.calcValue(pickPic1, amountPick));
            progressBar2.setProgress(service.calcValue(pickPic2, amountPick));
        }

//        progressBarPostCard.setVisibility(View.GONE);
    }

    @Override
    public void toCommentsDot(List<CommentsDTO> commentsDTOList) {
        commentsAdapter.clearItems();
        commentsAdapter.setItems(commentsDTOList);
        progressBarPostCard.setVisibility(View.GONE);
    }
}
