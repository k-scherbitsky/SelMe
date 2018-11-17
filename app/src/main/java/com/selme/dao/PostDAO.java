package com.selme.dao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.selme.R;
import com.selme.entity.PostEntity;
import com.selme.interfaces.PostDAOCallback;

import java.util.ArrayList;
import java.util.List;


public class PostDAO {

    private static final String TAG = "PostDAO";
    private static final String COLLECTION_PATH_POST = "post";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection(COLLECTION_PATH_POST).document();
    private CollectionReference collectionRef = db.collection(COLLECTION_PATH_POST);

    private PostDAOCallback callback;

    private Context context;
    private Activity activity;

    public PostDAO(Context context, FragmentActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    public PostDAO(PostDAOCallback callback){
        this.callback = callback;
    }

    public void createNewPost(ProgressDialog progressDialog, String title, String description, String userId, String nameOfPic1, String nameOfPic2){
        Log.d(TAG, "createNewPost");

        PostEntity postEntity = new PostEntity();

        postEntity.setTitle(title);
        postEntity.setDescription(description);
        postEntity.setPhoto1(nameOfPic1);
        postEntity.setPhoto2(nameOfPic2);
        postEntity.setUserId(userId);

        docRef.set(postEntity).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d(TAG, "createNewPost: new post is created");
                progressDialog.dismiss();
            } else {
                Log.w(TAG, "createNewPost: new post isn't ctreated. Check log", task.getException());
                Toast.makeText(context, R.string.error_create_post, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void getPost(){
        Query query = collectionRef;

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                PostEntity entity;
                List<PostEntity> entities = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                    entity = documentSnapshot.toObject(PostEntity.class);
                    entities.add(entity);
                }
                callback.onPostLoaded(entities);
            } else {
                callback.onPostFailed(task.getException());
            }
        });
    }


}
