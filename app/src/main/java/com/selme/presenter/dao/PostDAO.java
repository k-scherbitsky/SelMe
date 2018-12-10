package com.selme.presenter.dao;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.selme.R;
import com.selme.model.entity.PostEntity;
import com.selme.presenter.interfaces.PostDAOCallback;
import com.selme.presenter.service.PostService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PostDAO {

    private static final String TAG = "PostDAO";
    private static final String COLLECTION_PATH_POST = "post";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection(COLLECTION_PATH_POST).document();
    private CollectionReference collectionRef = db.collection(COLLECTION_PATH_POST);

    private PostDAOCallback callback;

    private Context context;

    public PostDAO() {
    }

    public PostDAO(Context context) {
        this.context = context;
    }

    public PostDAO(PostDAOCallback callback) {
        this.callback = callback;
    }

    public void updateLikes(String docId, Map<String, Boolean> likesMap){
        Log.d(TAG, "updateLikesQuantity: Update likes map in database");
        DocumentReference ref = db.collection(COLLECTION_PATH_POST).document(docId);

        ref.update("likes", likesMap);
    }

    public void updateLikesQuantity(String docId, TextView qntyView){
        final DocumentReference docRef = db.collection(COLLECTION_PATH_POST).document(docId);

        docRef.addSnapshotListener((documentSnapshot, e) -> {
           if (e != null){
               Log.e(TAG, "updateLikesQuantity: ", e);
           }

           if (documentSnapshot != null && documentSnapshot.exists()){
               PostEntity entity = documentSnapshot.toObject(PostEntity.class);
               Map<String, Boolean> likesMap = entity.getLikes();

               int countLikes = 0;
               for (Map.Entry entry : likesMap.entrySet()) {
                   if(entry.getValue().equals(true)){
                       countLikes++;
                   }
               }
               String qnty = countLikes != 0 ? String.valueOf(countLikes) : "";
               qntyView.setText(qnty);
           }
        });
    }

    public void updateQntyPick(String docId, int numButton, List<String> votesUserIds) {
        Log.d(TAG, "updateQntyPick: Update values in database");
        DocumentReference ref = db.collection(COLLECTION_PATH_POST).document(docId);

        ref.get().addOnSuccessListener(documentSnapshot -> {
            PostEntity postEntity = documentSnapshot.toObject(PostEntity.class);
            Map<String, Object> map = new HashMap<>();
            map.put("votedUserIds", votesUserIds);

            int qntyButton;

            switch (numButton) {
                case 1:
                    qntyButton = postEntity != null ? postEntity.getPickPic1() : 0;
                    map.put("pickPic1", qntyButton + 1);
                    break;
                case 2:
                    qntyButton = postEntity != null ? postEntity.getPickPic2() : 0;
                    map.put("pickPic2", qntyButton + 1);
            }
            ref.update(map);
        });
    }

    public void updateProgressBar(String docId, ProgressBar progressBar1, ProgressBar progressBar2){
        final DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("post")
                .document(docId);

        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null){
                Log.e(TAG, "addProgressBarValue: ", e);
            }

            if (documentSnapshot != null && documentSnapshot.exists()){
                PostEntity postEntity = documentSnapshot.toObject(PostEntity.class);
                int pickPic1 = postEntity != null ? postEntity.getPickPic1() : 0;
                int pickPic2 = postEntity != null ? postEntity.getPickPic2() : 0;

                PostService postService = new PostService();
                int amount = pickPic1 + pickPic2;
                int valueProgressBar1 = postService.calcValue(pickPic1, amount);
                int valueProgressBar2 = postService.calcValue(pickPic2, amount);

                progressBar1.setProgress(valueProgressBar1);
                progressBar2.setProgress(valueProgressBar2);

            } else {
                Toast.makeText(context, "Nothing to update", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "addProgressBarValue: ");
            }
        });
    }

    public void addNewComment(String docId, Map<String, String> commentsMap){
        Log.d(TAG, "addNewComment: Add new comment");
        DocumentReference ref = db.collection(COLLECTION_PATH_POST).document(docId);

        ref.update("comments", commentsMap);
    }

    public void addNewPost(ProgressDialog progressDialog, String description, String userId, String nameOfPic1, String nameOfPic2) {
        Log.d(TAG, "addNewPost");

        PostEntity postEntity = new PostEntity();

        postEntity.setDescription(description);
        postEntity.setPhoto1(nameOfPic1);
        postEntity.setPhoto2(nameOfPic2);
        postEntity.setUserId(userId);
        postEntity.setDocId(docRef.getId());
        postEntity.setPickPic1(0);
        postEntity.setPickPic2(0);
        postEntity.setComments(new HashMap<>());
        postEntity.setVotedUserIds(new ArrayList<>());
        postEntity.setLikes(new HashMap<>());

        docRef.set(postEntity).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "addNewPost: new post is created");
                progressDialog.dismiss();
            } else {
                Log.e(TAG, "addNewPost: new post isn't created. Check log", task.getException());
                Toast.makeText(context, R.string.error_create_post, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void getPost(boolean isProfile) {
        Query query;
        if (isProfile) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            query = collectionRef.whereEqualTo("userId", currentUserId);
        } else {
            query = collectionRef;
        }
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                PostEntity entity;
                List<PostEntity> entities = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
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
