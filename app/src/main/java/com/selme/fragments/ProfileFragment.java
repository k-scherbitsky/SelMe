package com.selme.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.selme.R;
import com.selme.dao.UserDAO;
import com.selme.entity.UserEntity;
import com.selme.interfaces.PictureLoaderCallback;
import com.selme.interfaces.UserDAOCallback;
import com.selme.modal.PictureLoader;

public class ProfileFragment extends Fragment implements UserDAOCallback, PictureLoaderCallback {
    private static final String TAG = "ProfileFragment";

    private StorageReference mStorageRef;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userId;
    private PictureLoader pictureLoader;

    private ProgressBar progressBar;
    private TextView userName;
    private TextView description;
    private ImageView profileImage;
    private String avatarName;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        userId = auth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressBar = view.findViewById(R.id.progressBarProfile);
        profileImage = view.findViewById(R.id.profileImageView);

        UserDAO userDAO = new UserDAO(this);
        userDAO.getUser(userId, 0, 0);

        pictureLoader = new PictureLoader(mStorageRef, this);

        userName = view.findViewById(R.id.userNameTextView);
        description = view.findViewById(R.id.profileDescriptionTextView);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onUserLoaded(UserEntity user, int requestCode, int pos) {
        userName.setText(getUserName(user.getFirstName(), user.getLastName()));
        description.setText(user.getDescription());
        avatarName = user.getProfilePhoto();

        mStorageRef = mStorageRef.child("profileImage/" + avatarName );
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
        progressBar.setVisibility(View.INVISIBLE);
    }

    private  String getUserName(String firstName, String lastName){
        return firstName + " " + lastName;
    }

}
