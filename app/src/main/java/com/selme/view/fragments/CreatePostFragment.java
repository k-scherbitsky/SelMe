package com.selme.view.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.selme.R;
import com.selme.presenter.dao.PostDAO;
import com.selme.presenter.service.PictureLoader;
import com.selme.view.activity.auth.SignUpActivity;

import java.util.Arrays;
import java.util.UUID;

public class CreatePostFragment extends Fragment {

    private static final String TAG = "CreatePostFragment";
    private static final int REQUEST_CODE_PICTURE_1 = 100;
    private static final int REQUEST_CODE_PICTURE_2 = 200;
    private static final int REQUEST_PERMISSIONS = 20;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    private TextView errorTextPic1;
    private TextView errorTextPic2;
    private EditText description;
    private ImageView picture1;
    private ImageView picture2;
    private Button uploadPicture1;
    private Button uploadPicture2;
    private Button share;

    private Uri uriPicture1;
    private Uri uriPicture2;
    private Bitmap bitmapPicture1;
    private Bitmap bitmapPicture2;

    private PictureLoader pictureUploader;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if(data == null){
            Snackbar.make(getView(), "You didn't choose a picture!", Snackbar.LENGTH_SHORT).show();
            Log.wtf(TAG, "onActivityResult: Picture is not chosen");
            return;
        }

        switch (requestCode){
            case REQUEST_CODE_PICTURE_1:
                uriPicture1 = data.getData();
                try {
                    bitmapPicture1 = pictureUploader.getBitmapImage(bitmapPicture1, resultCode, data);
                    picture1.setImageBitmap(bitmapPicture1);
                    Log.d(TAG, "onActivityResult: user select picture 1 from gallery");
                } catch (Exception ex) {
                    Log.e(TAG, "onActivityResult: bitmap is empty for picture 1!", ex);
                }
                break;
            case REQUEST_CODE_PICTURE_2:
                uriPicture2 = data.getData();
                try {
                    bitmapPicture2 = pictureUploader.getBitmapImage(bitmapPicture2, resultCode, data);
                    picture2.setImageBitmap(bitmapPicture2);
                    Log.d(TAG, "onActivityResult: user select picture 2 from gallery");
                } catch (Exception ex){
                    Log.e(TAG, "onActivityResult: bitmap is empty for picture 2!", ex);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        pictureUploader = new PictureLoader(mStorageRef, view.getContext());

        errorTextPic1 = view.findViewById(R.id.error_text_picture_1);
        errorTextPic2 = view.findViewById(R.id.error_text_picture_2);
        description = view.findViewById(R.id.input_description);
        picture1 = view.findViewById(R.id.input_photo1);
        picture2 = view.findViewById(R.id.input_photo2);
        uploadPicture1 = view.findViewById(R.id.upload_photo_1);
        uploadPicture2 = view.findViewById(R.id.upload_photo_2);
        share = view.findViewById(R.id.share_new_post);

        uploadPicture1.setOnClickListener(view1 -> verifyPermissions(REQUEST_CODE_PICTURE_1));
        uploadPicture2.setOnClickListener(view1 -> verifyPermissions(REQUEST_CODE_PICTURE_2));
        share.setOnClickListener(view1 -> createNewPost());

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() called with: " +
                "requestCode = [" + requestCode + "], " +
                "permissions = [" + Arrays.toString(permissions) + "], " +
                "grantResults = [" + Arrays.toString(grantResults) + "]");

        verifyPermissions(requestCode);
    }

    private void verifyPermissions(int requestCode){
        Log.d(TAG, "verifyPermissions: asking user for permissions");

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET};

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        permissions[2]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        permissions[3]) == PackageManager.PERMISSION_GRANTED) {
            openGallery(requestCode);
        } else {
            Log.wtf(TAG, "verifyPermissions: permissions was reject");
            ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
        }
    }

    private void openGallery(int requestCode) {
        Log.d(TAG, "openGallery() called with: requestCode = [" + requestCode + "]");
        Intent photoIntent = new Intent(Intent.ACTION_PICK);
        photoIntent.setType("image/*");
        startActivityForResult(photoIntent, requestCode);
    }

    private void createNewPost() {
        Log.d(TAG, "createNewPost() called");

        if(!validate()){
            share.setEnabled(true);
            return;
        }

        share.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.posting_progress_dialog));
        progressDialog.show();

        String descText = description.getText().toString();
        String namePic1 = getUniqName(bitmapPicture1);
        String namePic2 = getUniqName(bitmapPicture2);

        PostDAO postDAO = new PostDAO(getContext());
        postDAO.addNewPost(progressDialog, descText, mAuth.getCurrentUser().getUid(), namePic1, namePic2);

        clearForm();
    }

    private void clearForm() {
        description.getText().clear();
        picture1.setImageResource(0);
        picture2.setImageResource(0);
    }


    private String getUniqName(Bitmap bitmap) {
        String fileName = UUID.randomUUID().toString();
        pictureUploader.uploadPhotoFromDataInMemory(bitmap, "post", fileName);

        return fileName;
    }

    private boolean validate() {
        boolean validate = true;

        String desc = description.getText().toString();
        if (desc.isEmpty()){
            description.setError(getString(R.string.error_input_desc_post));
            validate = false;
        } else {
            description.setError(null);
        }

        if(uriPicture1 == null){
            Snackbar.make(getView(), R.string.upload_picture_error, Snackbar.LENGTH_SHORT).show();
            errorTextPic1.setVisibility(View.VISIBLE);
            validate = false;
        } else {
            errorTextPic1.setVisibility(View.INVISIBLE);
        }

        if(uriPicture2 == null){
            Snackbar.make(getView(), R.string.upload_picture_error, Snackbar.LENGTH_SHORT).show();
            errorTextPic2.setVisibility(View.VISIBLE);
            validate = false;
        } else {
            errorTextPic2.setVisibility(View.INVISIBLE);
        }

        return validate;
    }
}
