package com.selme.activity.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.selme.R;
import com.selme.activity.MainActivity;
import com.selme.dao.UserDAO;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private FirebaseAuth mAuth;

    private EditText firstNameText;
    private EditText lastNameText;
    private EditText aboutMeText;
    private EditText emailText;
    private EditText passwordText;
    private Button signUpButton;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        mAuth = FirebaseAuth.getInstance();

        firstNameText = findViewById(R.id.input_name);
        lastNameText = findViewById(R.id.input_last_name);
        aboutMeText = findViewById(R.id.input_about_me);
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        signUpButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.link_login);

        signUpButton.setOnClickListener(view -> signUp());

        loginLink.setOnClickListener((View v) -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

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
                        UserDAO userDAO = new UserDAO(getBaseContext());
                        userDAO.createNewUser(firstName, lastName,  description);
                        onSignUpSuccess();
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
}
