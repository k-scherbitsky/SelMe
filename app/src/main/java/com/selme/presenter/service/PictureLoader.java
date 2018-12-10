package com.selme.presenter.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.firebase.storage.StorageReference;
import com.selme.presenter.interfaces.PictureLoaderCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class PictureLoader {

    private static final String TAG = "PictureLoader";
    private StorageReference storageRef;
    private Activity activity;
    private Context context;
    private PictureLoaderCallback callback;

    public PictureLoader(StorageReference storageRef, Context context) {
        this.storageRef = storageRef;
        this.context = context;
    }

    public PictureLoader(StorageReference storageRef, PictureLoaderCallback callback) {
        this.callback = callback;
        this.storageRef = storageRef;
    }

    public void uploadPhotoFromDataInMemory(Bitmap bitmap, String folderName, String fileName){
        Log.d(TAG, "uploadPhotoFromDataInMemory() called with: bitmap = [" + bitmap + "], folderName = [" + folderName + "], fileName = [" + fileName + "]");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String filePath = folderName + "/" + fileName;
        StorageReference riversRef = storageRef.child(filePath);

        riversRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            Log.d(TAG, "uploadPhotoFromDataInMemory: photo is uploaded");
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "uploadPhotoFromDataInMemory: photo isn't uploaded. Check log", exception );
        });
    }

    public void getPhotoUri(StorageReference riversRef, int requestCode, int pos) {
        Log.d(TAG, "getPhotoUri() called with: riversRef = [" + riversRef + "], requestCode = [" + requestCode + "], pos = [" + pos + "]");
        riversRef.getDownloadUrl().addOnSuccessListener(uri -> callback.onPictureDownloaded(uri, requestCode, pos));
    }

    public Bitmap getBitmapImage(Bitmap bitmap, int resultCode, Intent data) {
        Log.d(TAG, "getBitmapImage() called with: bitmap = [" + bitmap + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    String path = getRealPathFromUri(context, uri);
                    File file = new File(path);
                    bitmap = new Compressor(context)
                            .setMaxHeight(640)
                            .setQuality(75)
                            .compressToBitmap(file);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return bitmap;
    }

    private static String getRealPathFromUri(Context context, Uri contentUri) {
        Log.d(TAG, "getRealPathFromUri() called with: context = [" + context + "], contentUri = [" + contentUri + "]");
        String[] proj = { MediaStore.Images.Media.DATA };
        try (Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null)) {
            int column_index = 0;
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }
}
