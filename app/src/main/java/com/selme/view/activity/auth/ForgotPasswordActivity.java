package com.selme.view.activity.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.selme.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private final static String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button reset = findViewById(R.id.btn_reset_password);
        TextView loginLink = findViewById(R.id.link_login_forgot);

        reset.setOnClickListener(view -> resetPassword());
        loginLink.setOnClickListener(view -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void resetPassword() {
        EditText emailText = findViewById(R.id.input_email_forgot);

        final ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.sending_email));
        progressDialog.show();

        String email = emailText.getText().toString();
        if(email.isEmpty()){
            emailText.setError(getString(R.string.email_input_error));
            return;
        } else {
            emailText.setError(null);
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Snackbar.make(getCurrentFocus(), "Email is sent!", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Snackbar.make(getCurrentFocus(), "Email isn't sent!", Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "resetPassword: failure", task.getException());
            }
            progressDialog.dismiss();
        });


    }
}
