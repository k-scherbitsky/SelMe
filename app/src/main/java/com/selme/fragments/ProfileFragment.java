package com.selme.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.selme.R;
import com.selme.dao.UserDAO;
import com.selme.entity.UserEntity;
import com.selme.interfaces.UserDAOCallback;

public class ProfileFragment extends Fragment implements UserDAOCallback {
    private static final String TAG = "ProfileFragment";

    private StorageReference mStorageRef;
    private String userId;

    private ProgressBar progressBar;
    private TextView userName;
    private TextView description;
    private ImageView profileImage;

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

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressBar = view.findViewById(R.id.progressBarProfile);
        profileImage = view.findViewById(R.id.profileImageView);

        UserDAO userDAO = new UserDAO(this);
        userDAO.getUser(userId);

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
    public void onLoaded(UserEntity user) {
        userName.setText(getUserName(user.getFirstName(), user.getLastName()));
        description.setText(user.getDescription());
        getProfilePhoto();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFailed(String error) {

    }

    private void getProfilePhoto(){
        String filePath = "profileImage/" + userId + ".jpg";
        StorageReference riversRef = mStorageRef.child(filePath);

        riversRef.getDownloadUrl()
                .addOnSuccessListener(uri -> Glide.with(this).load(uri).into(profileImage));


    }

    private  String getUserName(String firstName, String lastName){
        return firstName + " " + lastName;
    }
}
