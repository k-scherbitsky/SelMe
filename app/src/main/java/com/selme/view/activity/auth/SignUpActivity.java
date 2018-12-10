package com.selme.view.activity.auth;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.selme.view.activity.MainActivity;
import com.selme.presenter.dao.UserDAO;
import com.selme.presenter.service.PictureLoader;

import java.util.Arrays;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private static final int REQUEST_PERMISSIONS = 10;

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
            verifyPermissions();
        });

        signUpButton.setOnClickListener(view -> signUp());

        loginLink.setOnClickListener((View v) -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void verifyPermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions");

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[3]) == PackageManager.PERMISSION_GRANTED){
            openGallery();
        } else {
            Log.wtf(TAG, "verifyPermissions: permissions was reject");
            ActivityCompat.requestPermissions(SignUpActivity.this, permissions, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() called with: requestCode = [" + requestCode + "], permissions = [" + Arrays.toString(permissions) + "], grantResults = [" + Arrays.toString(grantResults) + "]");
        Log.wtf(TAG, "onRequestPermissionsResult: try get permissions again");
        verifyPermissions();
    }

    private void openGallery(){
        Log.d(TAG, "openGallery() called");
        Intent photoIntent = new Intent(Intent.ACTION_PICK);
        photoIntent.setType("image/*");
        startActivityForResult(photoIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

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
                Log.d(TAG, "onActivityResult: user selected picture from gallery");
            }
        }
    }

    private void signUp() {
        Log.d(TAG, "signUp() called");

        if (!validate()) {
            onSignUpFailed();
            return;
        }

        signUpButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignUpActivity.this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_authenticating));
        progressDialog.show();

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString();
        String avatarFileName = UUID.randomUUID().toString();
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String description = aboutMeText.getText().toString();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        userId = mAuth.getCurrentUser().getUid();

                        UserDAO userDAO = new UserDAO(getBaseContext());
                        userDAO.createNewUser(firstName, lastName, description, userId, avatarFileName);

                        pictureUploader.uploadPhotoFromDataInMemory(currentPhoto, "profileImage", avatarFileName);

                        onSignUpSuccess();

                        progressDialog.dismiss();

                        Log.d(TAG, "createUserWithEmailAndPassword: success.");
                    } else {
                        progressDialog.dismiss();
                        onSignUpFailed();
                        Log.e(TAG, "createUserWithEmailAndPassword:  Sign up is failed!. Check log", task.getException());
                    }
                });
    }

    private void onSignUpFailed() {
        Log.d(TAG, "onSignUpFailed() called");
        Snackbar.make(getCurrentFocus(), R.string.toast_sign_up_failed, Snackbar.LENGTH_SHORT).show();
        signUpButton.setEnabled(true);
    }

    private void onSignUpSuccess() {
        Log.d(TAG, "onSignUpSuccess() called");
        signUpButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validate() {
        Log.d(TAG, "validate() called");
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
