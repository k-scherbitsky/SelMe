package com.selme.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.selme.R;
import com.selme.entity.UserEntity;


public class UserDAO {
    private static final String TAG = "UserDAO";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference newUserRef = db.collection("users").document();
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Context context;

    public UserDAO(Context context){
        this.context = context;
    }

    public void createNewUser(String firstName, String lastName, String description){
        Log.d(TAG, "createNewUser");
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setDescription(description);

        newUserRef.set(userEntity).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d(TAG, "createNewUser: success");
                Toast.makeText(context, R.string.toast_sign_up_success, Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "createNewUser: failure");
                Toast.makeText(context, R.string.toast_sign_up_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
