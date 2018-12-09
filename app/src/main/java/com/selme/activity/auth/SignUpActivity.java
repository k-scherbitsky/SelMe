package com.selme.activity.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.selme.R;
import com.selme.activity.MainActivity;
import com.selme.dao.UserDAO;
import com.selme.service.PictureLoader;

import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    private EditText firstNameText;
    private EditText lastNameText;
    private EditText aboutMeText;
    private EditText emailText;
    private EditText passwordText;
    private EditText passwordRepeatText;
    private Button signUpButton;
    private TextView loginLink;

    private PictureLoader pictureUploader;

    private ImageView profileImage;
    private Bitmap currentPhoto;
    private Uri photoUri;
    private String pathToAvatar;
    private String userId;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        pictureUploader = new PictureLoader(mStorageRef,  getBaseContext());

        profileImage = findViewById(R.id.profileImageSignUp);
        firstNameText = findViewById(R.id.input_name);
        lastNameText = findViewById(R.id.input_last_name);
        aboutMeText = findViewById(R.id.input_about_me);
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        passwordRepeatText = findViewById(R.id.input_password_repeat);
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
        if (currentPhoto != null) {
            currentPhoto.recycle();
            currentPhoto = null;
            System.gc();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentPhoto != null) {
            currentPhoto.recycle();
            currentPhoto = null;
            System.gc();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){
            Snackbar.make(getCurrentFocus(), "You didn't choose a picture!", Snackbar.LENGTH_SHORT).show();
            Log.wtf(TAG, "onActivityResult: Picture is not chosen");
            return;
        }

        if (resultCode == RESULT_OK) {
            photoUri = data.getData();
            if (photoUri != null) {
                currentPhoto = pictureUploader.getBitmapImage(currentPhoto, resultCode, data);
                profileImage.setImageBitmap(currentPhoto);
                Log.d(TAG, "onActivityResult: user select picture from gallery");
            }
        }
    }

    private void signUp() {
        Log.d(TAG, "signUp");

        if (!validate()) {
            onSignUpFailed();
            return;
        }

        signUpButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignUpActivity.this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_authenticating));
        progressDialog.show();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String avatarFileName = UUID.randomUUID().toString();
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String description = aboutMeText.getText().toString();

        pictureUploader.uploadPhotoFromDataInMemory(currentPhoto, "profileImage", avatarFileName);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        userId = mAuth.getCurrentUser().getUid();

                        UserDAO userDAO = new UserDAO(getBaseContext());
                        userDAO.createNewUser(firstName, lastName, description, userId, avatarFileName);
                        onSignUpSuccess();

                        progressDialog.dismiss();

                        Log.d(TAG, "createUserWithEmailAndPassword:  success.");
                    } else {
                        progressDialog.dismiss();
                        onSignUpFailed();
                        Log.d(TAG, "createUserWithEmailAndPassword:  failure");
                    }
                });
    }

    private void onSignUpFailed() {
        Snackbar.make(getCurrentFocus(), R.string.toast_sign_up_failed, Snackbar.LENGTH_SHORT).show();
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
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String passwordRepeat = passwordRepeatText.getText().toString();

        if(photoUri == null){
            Snackbar.make(getCurrentFocus(), R.string.upload_your_avatar, Snackbar.LENGTH_SHORT).show();
            validate = false;
        }

        if (firstName.isEmpty()) {
            firstNameText.setError(getString(R.string.first_name_input_error));
            validate = false;
        } else {
            firstNameText.setError(null);
        }

        if (lastName.isEmpty()) {
            lastNameText.setError(getString(R.string.last_name_input_error));
            validate = false;
        } else {
            lastNameText.setError(null);
        }

        if(email.isEmpty()){
            emailText.setError("Enter your e-mail");
            validate = false;
        } else {
            emailText.setError(null);
        }

        if(password.isEmpty()){
            passwordText.setError("Enter your password");
            validate = false;
        } else {
            passwordText.setError(null);
        }

        if(passwordRepeat.isEmpty()){
            passwordRepeatText.setError("Repeat your password");
            validate = false;
        } else {
            passwordRepeatText.setError(null);
        }

        if(!password.equals(passwordRepeat)){
            passwordText.setError(getString(R.string.password_not_equals));
            passwordRepeatText.setError(getString(R.string.password_not_equals));
            validate = false;
        } else {
            passwordText.setError(null);
            passwordRepeatText.setError(null);
        }

        return validate;
    }

}
