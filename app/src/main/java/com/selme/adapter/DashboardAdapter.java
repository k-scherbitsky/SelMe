package com.selme.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.selme.R;
import com.selme.dao.PostDAO;
import com.selme.dto.PostDTO;
import com.selme.entity.PostEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private static final String TAG = "DashboardAdapter";
    private static final int BUTTON_1 = 1;
    private static final int BUTTON_2 = 2;

    private List<PostDTO> postEntityList = new ArrayList<>();

    public void setItems(Collection<PostDTO> posts) {
        postEntityList.addAll(posts);
        notifyDataSetChanged();
    }

    public void clearItems() {
        postEntityList.clear();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.selme_item_view, viewGroup, false);

        return new DashboardViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder dashboardViewHolder, int i) {

        Button thisOneButton1 = dashboardViewHolder.thisOneButton1;
        thisOneButton1.setOnClickListener(view1 -> addProgressBarValue(dashboardViewHolder, BUTTON_1));

        Button thisOneButton2 = dashboardViewHolder.thisOneButton2;
        thisOneButton2.setOnClickListener(view1 -> addProgressBarValue(dashboardViewHolder, BUTTON_2));

        dashboardViewHolder.bind(postEntityList.get(i));
    }

    private void addProgressBarValue(DashboardViewHolder dashboardViewHolder, int button) {

        Button buttonView1 = dashboardViewHolder.thisOneButton1;
        Button buttonView2 = dashboardViewHolder.thisOneButton2;
        buttonView1.setEnabled(false);
        buttonView2.setEnabled(false);

        int adapterPosition = dashboardViewHolder.getAdapterPosition();
        String docId = postEntityList.get(adapterPosition).getDocId();

        PostDAO postDAO = new PostDAO(dashboardViewHolder.itemView.getContext());
        switch (button) {
            case BUTTON_1:
                postDAO.updateQntyPick(docId, BUTTON_1);
                break;
            case BUTTON_2:
                postDAO.updateQntyPick(docId, BUTTON_2);
                break;
        }

        postDAO.updateProgressBar(docId, dashboardViewHolder.progressBar1, dashboardViewHolder.progressBar2);
    }

    @Override
    public int getItemCount() {
        return postEntityList.size();
    }

    class DashboardViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatarImageView;
        private TextView userName;
        private TextView title;
        private TextView description;
        private ImageView picture1;
        private ImageView picture2;
        private Button thisOneButton1;
        private Button thisOneButton2;
        private ProgressBar progressBar1;
        private ProgressBar progressBar2;

        DashboardViewHolder(@NonNull View itemView) {
            super(itemView);

            avatarImageView = itemView.findViewById(R.id.post_avatar);
            userName = itemView.findViewById(R.id.post_username);

            title = itemView.findViewById(R.id.titlePostTextView);
            description = itemView.findViewById(R.id.descriptionPostTextView);
            picture1 = itemView.findViewById(R.id.postImage1);
            picture2 = itemView.findViewById(R.id.postImage2);
            thisOneButton1 = itemView.findViewById(R.id.thisOneButton1);
            thisOneButton2 = itemView.findViewById(R.id.thisOneButton2);
            progressBar1 = itemView.findViewById(R.id.progressBar1);
            progressBar2 = itemView.findViewById(R.id.progressBar2);

        }

        void bind(PostDTO postDTO) {
            if (postDTO.getUserName() != null) {
                userName.setText(postDTO.getUserName());
            }
            if (postDTO.getTitle() != null) {
                title.setText(postDTO.getTitle());
            }
            if (postDTO.getDescription() != null) {
                description.setText(postDTO.getDescription());
            }

            Uri avatar = postDTO.getAvatar();
            Uri pic1 = postDTO.getPicture1();
            Uri pic2 = postDTO.getPicture2();


            Glide.with(itemView.getContext()).load(avatar).apply(RequestOptions.circleCropTransform()).into(avatarImageView);
            Glide.with(itemView.getContext()).load(pic1).into(picture1);
            Glide.with(itemView.getContext()).load(pic2).into(picture2);

            picture1.setVisibility(avatar != null ? View.VISIBLE : View.GONE);
            picture1.setVisibility(pic1 != null ? View.VISIBLE : View.GONE);
            picture2.setVisibility(pic2 != null ? View.VISIBLE : View.GONE);
        }
    }
}
