package com.selme.modal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.selme.interfaces.PictureLoaderCallback;

import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class PictureLoader {

    private StorageReference storageRef;
    private Activity activity;
    private PictureLoaderCallback callback;

    public PictureLoader(StorageReference storageRef, Activity activity) {
        this.storageRef = storageRef;
        this.activity = activity;
    }

    public PictureLoader(StorageReference storageRef, PictureLoaderCallback callback) {
        this.callback = callback;
        this.storageRef = storageRef;
    }

    public void uploadPhoto(Uri uri, String TAG, String folderName, String fileName) {
        String filePath = folderName + "/" + fileName;
        StorageReference riversRef = storageRef.child(filePath);

        riversRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    Log.d(TAG, "uploadPhoto: photo is uploaded");
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "uploadPhoto: photo isn't uploaded. Check log");
                });
    }

    public void getPhotoUri(StorageReference riversRef, int requestCode, int pos) {
        riversRef.getDownloadUrl().addOnSuccessListener(uri -> callback.onPictureDownloaded(uri, requestCode, pos));
    }

    public Bitmap getBitmapImage(Bitmap bitmap, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return bitmap;
    }

}
