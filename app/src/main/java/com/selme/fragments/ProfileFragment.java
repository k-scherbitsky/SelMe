package com.selme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.selme.R;
import com.selme.dao.UserDAO;
import com.selme.entity.UserEntity;
import com.selme.interfaces.UserDAOCallback;

public class ProfileFragment extends Fragment implements UserDAOCallback {
    private static final String TAG = "ProfileFragment";

    private TextView userName;
    private TextView description;

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

        UserDAO userDAO = new UserDAO(this);
        userDAO.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());

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
    }

    @Override
    public void onFailed(String error) {

    }

    private  String getUserName(String firstName, String lastName){
        return firstName + " " + lastName;
    }
}
