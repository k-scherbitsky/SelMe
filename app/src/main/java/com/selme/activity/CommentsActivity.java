package com.selme.activity;

import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.selme.R;
import com.selme.adapter.CommentsAdapter;
import com.selme.dao.PostDAO;
import com.selme.dto.CommentsDTO;
import com.selme.dto.PostDTO;
import com.selme.interfaces.CommentsDTOCallback;
import com.selme.service.DataMapper;

import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity implements CommentsDTOCallback {

    private PostDTO postDTO;
    private CommentsAdapter commentsAdapter;
    private DataMapper dataMapper;

    private ProgressBar progressBarPostCard;
    private RecyclerView recyclerView;
    private EditText commentText;
    private ImageView sendCommentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        dataMapper = new DataMapper(storageRef);

        progressBarPostCard = findViewById(R.id.comments_progress_bar);
        recyclerView = findViewById(R.id.comments_recycler_view);
        commentText = findViewById(R.id.comments_input_comment);
        sendCommentView = findViewById(R.id.comments_send_comment_view);

        postDTO = getIntent().getParcelableExtra("PostCard");
        if(postDTO != null) {
            dataMapper.toCommentsDto(postDTO.getComments(), this);
            initRecyclerView();
        }

        sendCommentView.setOnClickListener(view -> {
            sendComment();
        });
    }

    private void sendComment() {
        String comment = commentText.getText().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(comment.isEmpty()){
            commentText.setError(getString(R.string.write_comment));
            return;
        } else {
            commentText.setError(null);
        }

        Map<String, String> commentsMap = postDTO.getComments();
        if(commentsMap.containsKey(userId)){
            Snackbar.make(getCurrentFocus(), "You have already write a comment!", Snackbar.LENGTH_SHORT).show();
            return;
        }
        commentsMap.put(userId, comment);

        PostDAO postDAO = new PostDAO(getBaseContext());
        postDAO.addNewComment(postDTO.getDocId(), commentsMap);
        commentText.setText(null);
        dataMapper.toCommentsDto(commentsMap, this);
        progressBarPostCard.setVisibility(View.VISIBLE);
    }

    private void initRecyclerView() {
        if(postDTO.getComments().size() == 0){
            progressBarPostCard.setVisibility(View.GONE);
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
