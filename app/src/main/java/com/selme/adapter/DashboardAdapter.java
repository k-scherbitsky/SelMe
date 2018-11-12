package com.selme.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.selme.R;
import com.selme.entity.PostEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private List<PostEntity> postEntityList = new ArrayList<>();

    public void setItems(Collection<PostEntity> posts){
        postEntityList.addAll(posts);
        notifyDataSetChanged();
    }

    public void clearItems(){
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

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder dashboardViewHolder, int i) {
        dashboardViewHolder.bind(postEntityList.get(i));
    }

    @Override
    public int getItemCount() {
        return postEntityList.size();
    }


    public class DashboardViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView description;
        private ImageView picture1;
        private ImageView picture2;
        private Button thisOneButton1;
        private Button thisOneButton2;

        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);

            title =  itemView.findViewById(R.id.titlePostTextView);
            description = itemView.findViewById(R.id.descriptionPostTextView);
            picture1 = itemView.findViewById(R.id.postImage1);
            picture2 = itemView.findViewById(R.id.postImage2);
            thisOneButton1 = itemView.findViewById(R.id.thisOneButton1);
            thisOneButton2 = itemView.findViewById(R.id.thisOneButton2);

        }
        public void bind(PostEntity entity){
            title.setText(entity.getTitle());
            description.setText(entity.getDescription());

            String pic1 = entity.getPhoto1();
            String pic2 = entity.getPhoto2();

            Glide.with(itemView.getContext()).load(pic1).into(picture1);
            Glide.with(itemView.getContext()).load(pic2).into(picture2);

            picture1.setVisibility(pic1 != null ? View.VISIBLE : View.GONE);
            picture2.setVisibility(pic2 != null ? View.VISIBLE : View.GONE);
        }
    }


}
