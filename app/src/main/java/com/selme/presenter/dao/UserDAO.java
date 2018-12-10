package com.selme.presenter.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.selme.R;
import com.selme.model.entity.UserEntity;
import com.selme.presenter.interfaces.UserDAOCallback;


public class UserDAO {

    private static final String TAG = "UserDAO";
    private static final String COLLECTION_PATH_USERS = "users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference newUserRef = db.collection(COLLECTION_PATH_USERS).document();
    private CollectionReference userCollectionRef = db.collection(COLLECTION_PATH_USERS);
    private UserDAOCallback callback;

    private Context context;

    public UserDAO(Context context) {
        this.context = context;
    }

    public UserDAO(UserDAOCallback callback){
        this.callback = callback;
    }

    public void createNewUser(String firstName, String lastName, String description, String userId, String avatar) {
        Log.d(TAG, "createNewUser");
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setDescription(description);
        userEntity.setUserId(userId);
        userEntity.setProfilePhoto(avatar);

        newUserRef.set(userEntity).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "createNewUser: success");
                Toast.makeText(context, R.string.toast_sign_up_success, Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "createNewUser: failure", task.getException());
                Toast.makeText(context, R.string.toast_sign_up_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUser(String uid, int requestCode, int pos) {
        Query usersQuery = userCollectionRef.whereEqualTo("userId", uid);
        usersQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserEntity userEntity = new UserEntity();
                for (QueryDocumentSnapshot document: task.getResult()){
                    userEntity = document.toObject(UserEntity.class);
                }
                callback.onUserLoaded(userEntity, requestCode, pos);
            } else{
                callback.onUserLoadFailed(task.getException());
            }
        });

    }
}
