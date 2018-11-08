package com.selme.activity.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.selme.R;
import com.selme.activity.MainActivity;
import com.selme.dao.UserDAO;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    private EditText firstNameText;
    private EditText lastNameText;
    private EditText aboutMeText;
    private EditText emailText;
    private EditText passwordText;
    private Button signUpButton;
    private TextView loginLink;

    private ImageView profileImage;
    private Bitmap currentPhoto;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        profileImage = findViewById(R.id.profileImageSignUp);
        firstNameText = findViewById(R.id.input_name);
        lastNameText = findViewById(R.id.input_last_name);
        aboutMeText = findViewById(R.id.input_about_me);
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        signUpButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.link_login);

        profileImage.setOnClickListener(view -> {
            Intent photoIntent = new Intent(Intent.ACTION_PICK);
            photoIntent.setType("image/*");
            startActivityForResult(photoIntent, 1);
        });

        signUpButton.setOnClickListener(view -> signUp());

        loginLink.setOnClickListener((View v) -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentPhoto != null){
            currentPhoto.recycle();
            currentPhoto = null;
            System.gc();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentPhoto != null){
            currentPhoto.recycle();
            currentPhoto = null;
            System.gc();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            photoUri = data.getData();
            if(photoUri != null){
                try {
                    currentPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    profileImage.setImageBitmap(currentPhoto);
                    Toast.makeText(getBaseContext(), photoUri.getPath(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void signUp() {
        Log.d(TAG, "signUp");

        if(!validate()){
            onSignUpFailed();
            return;
        }

        signUpButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_authenticating));
        progressDialog.show();

        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String description = aboutMeText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        UserDAO userDAO = new UserDAO(getBaseContext());
                        userDAO.createNewUser(firstName, lastName,  description, userId);
                        onSignUpSuccess();
                        uploadPhoto();
                        progressDialog.dismiss();
                        Log.d(TAG, "createUserWithEmailAndPassword:  success.");
                    } else {
                        progressDialog.dismiss();
                        Log.d(TAG, "createUserWithEmailAndPassword:  failure");
                    }
                });
    }

    private void onSignUpFailed() {
        Toast.makeText(getBaseContext(), R.string.toast_sign_up_failed, Toast.LENGTH_SHORT).show();

        signUpButton.setEnabled(true);
    }

    private void onSignUpSuccess() {
        signUpButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validate() {
        boolean validate = true;

        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();

        if(firstName.isEmpty()){
            firstNameText.setError(getString(R.string.first_name_input_error));
            validate = false;
        } else {
            firstNameText.setError(null);
        }

        if(lastName.isEmpty()){
            lastNameText.setError(getString(R.string.last_name_input_error));
            validate = false;
        } else {
            lastNameText.setError(null);
        }

        return validate;
    }

    private void uploadPhoto(){
        String filePath = "profileImage/" + mAuth.getCurrentUser().getUid() + ".jpg";
        StorageReference riversRef = mStorageRef.child(filePath);

        riversRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    Log.d(TAG, "uploadPhoto: profile photo is uploaded");
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "uploadPhoto: profile photo isn't uploaded. Check log");
                });
    }
}