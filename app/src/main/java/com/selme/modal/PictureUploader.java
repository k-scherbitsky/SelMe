package com.selme.modal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class PictureUploader {

    private FirebaseAuth auth;
    private StorageReference storageRef;
    private Activity activity;

    public PictureUploader(FirebaseAuth auth, StorageReference storageRef, Activity activity) {
        this.auth = auth;
        this.storageRef = storageRef;
        this.activity = activity;
    }

    public boolean uploadPhoto(Uri uri, String TAG, String folderName, String fileName) {
        final boolean[] upload = new boolean[1];
        String filePath = folderName + "/" + fileName + ".jpg";
        StorageReference riversRef = storageRef.child(filePath);

        riversRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    upload[0] = true;
                    Log.d(TAG, "uploadPhoto: photo is uploaded");
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    upload[0] = true;
                    Log.d(TAG, "uploadPhoto: photo isn't uploaded. Check log");
                });
        return upload[0];
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
