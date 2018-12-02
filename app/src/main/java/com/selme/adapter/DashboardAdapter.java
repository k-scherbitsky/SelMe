package com.selme.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.selme.R;
import com.selme.dao.PostDAO;
import com.selme.dto.PostDTO;
import com.selme.service.PostService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private static final String TAG = "DashboardAdapter";
    private static final int BUTTON_1 = 1;
    private static final int BUTTON_2 = 2;

    private List<PostDTO> postDtoList = new ArrayList<>();
    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public void setItems(Collection<PostDTO> posts) {
        postDtoList.addAll(posts);
        notifyDataSetChanged();
    }

    public void clearItems() {
        postDtoList.clear();
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

        dashboardViewHolder.bind(postDtoList.get(i));
    }

    private void addProgressBarValue(DashboardViewHolder dashboardViewHolder, int button) {

        Button buttonView1 = dashboardViewHolder.thisOneButton1;
        Button buttonView2 = dashboardViewHolder.thisOneButton2;
        buttonView1.setEnabled(false);
        buttonView2.setEnabled(false);

        int adapterPosition = dashboardViewHolder.getAdapterPosition();
        String docId = postDtoList.get(adapterPosition).getDocId();
        List<String> votedUserIds = postDtoList.get(adapterPosition).getVotedUserIds();

        if (!votedUserIds.contains(currentUserId)) {
            votedUserIds.add(currentUserId);

            PostDAO postDAO = new PostDAO(dashboardViewHolder.itemView.getContext());
            switch (button) {
                case BUTTON_1:
                    postDAO.updateQntyPick(docId, BUTTON_1, votedUserIds);
                    break;
                case BUTTON_2:
                    postDAO.updateQntyPick(docId, BUTTON_2, votedUserIds);
                    break;
            }

            postDAO.updateProgressBar(docId, dashboardViewHolder.progressBar1, dashboardViewHolder.progressBar2);
        } else {
            Toast.makeText(dashboardViewHolder.itemView.getContext(), R.string.you_voted, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return postDtoList.size();
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
        private ProgressBar progressBarPic1;
        private ProgressBar progressBarPic2;

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
            progressBarPic1 = itemView.findViewById(R.id.progressBar_pic1);
            progressBarPic2 = itemView.findViewById(R.id.progressBar_pic2);

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


            Glide.with(itemView.getContext())
                    .load(avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatarImageView);

            Glide.with(itemView.getContext())
                    .load(pic1)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBarPic1.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(picture1);

            Glide.with(itemView.getContext())
                    .load(pic2)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBarPic2.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(picture2);

            avatarImageView.setVisibility(avatar != null ? View.VISIBLE : View.GONE);
            picture1.setVisibility(pic1 != null ? View.VISIBLE : View.GONE);
            picture2.setVisibility(pic2 != null ? View.VISIBLE : View.GONE);

            List<String> votedUserIds = postDTO.getVotedUserIds();
            PostService service = new PostService();
            int pickPic1 = postDTO.getPickPic1();
            int pickPic2 = postDTO.getPickPic2();
            int amountPick = pickPic1 + pickPic2;
            if(votedUserIds.contains(currentUserId)){
                progressBar1.setProgress(service.calcValue(pickPic1, amountPick));
                progressBar2.setProgress(service.calcValue(pickPic2, amountPick));
            }
        }
    }
}
