package com.selme.view.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.selme.R;
import com.selme.view.adapter.DashboardAdapter;
import com.selme.dao.UserDAO;
import com.selme.dto.PostDTO;
import com.selme.entity.UserEntity;
import com.selme.interfaces.PictureLoaderCallback;
import com.selme.interfaces.PostDTOCallback;
import com.selme.interfaces.UserDAOCallback;
import com.selme.service.DataMapper;
import com.selme.service.PictureLoader;

import java.util.List;

public class ProfileFragment extends Fragment implements UserDAOCallback, PictureLoaderCallback, PostDTOCallback {
    private static final String TAG = "ProfileFragment";

    private StorageReference mStorageRef;
    private PictureLoader pictureLoader;

    private ProgressBar progressBar;
    private TextView userName;
    private TextView description;
    private TextView nothingToShowView;
    private ImageView profileImage;
    private RecyclerView recyclerView;
    private DashboardAdapter dashboardAdapter;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressBar = view.findViewById(R.id.progressBarProfile);
        profileImage = view.findViewById(R.id.profileImageView);
        nothingToShowView = view.findViewById(R.id.nothing_to_show_view);

        UserDAO userDAO = new UserDAO(this);
        userDAO.getUser(userId, 0, 0);

        pictureLoader = new PictureLoader(mStorageRef, this);

        userName = view.findViewById(R.id.userNameTextView);
        description = view.findViewById(R.id.profileDescriptionTextView);

        recyclerView = view.findViewById(R.id.profile_recycler_view);
        DataMapper dataMapper = new DataMapper(mStorageRef);
        dataMapper.toPostDto(this, true);
        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dashboardAdapter = new DashboardAdapter();
        recyclerView.setAdapter(dashboardAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
    }

    @Override
    public void onUserLoaded(UserEntity user, int requestCode, int pos) {
        userName.setText(getUserName(user.getFirstName(), user.getLastName()));
        description.setText(user.getDescription());
        String avatarName = user.getProfilePhoto();

        mStorageRef = mStorageRef.child("profileImage/" + avatarName);
        pictureLoader.getPhotoUri(mStorageRef,0,  0);
    }

    @Override
    public void onUserLoadFailed(Exception error) {
        Log.w(TAG, "onUserLoadFailed: Data from db wasn't upload. Check log", error);
    }

    @Override
    public void onPictureDownloaded(Uri uri, int requestCode, int pos) {
        Glide.with(this)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions.placeholderOf(R.mipmap.ic_launcher_round))
                .into(profileImage);
    }

    private  String getUserName(String firstName, String lastName){
        return firstName + " " + lastName;
    }

    @Override
    public void toDto(List<PostDTO> dto) {
        if(dto == null){
            progressBar.setVisibility(View.GONE);
            nothingToShowView.setVisibility(View.VISIBLE);
            return;
        }

        dashboardAdapter.clearItems();
        dashboardAdapter.setItems(Lists.reverse(dto));
        progressBar.setVisibility(View.GONE);
    }
}
