package com.smarthuman.drstock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    // Constants
    public static final String CHAT_PREFS = "ChatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";

    // : Add member variables here:
    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.register_confirm_password);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.register_username);

        // Keyboard sign in action
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.register_form_finished || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        // : Get hold of an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();



    }

    // Executed when Sign Up button is pressed.
    public void signUp(View v) {
        attemptRegistration();
    }

    public void backSignIn(View v) {
        Intent intent = new Intent(RegisterActivity.this, com.smarthuman.drstock.LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void attemptRegistration() {

        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // : Call create FirebaseUser() here
            createFirebaseUser();

        }
    }

    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        String confirmPassword = mConfirmPasswordView.getText().toString();
        return confirmPassword.equals(password) && password.length() > 4;
    }

    // : Create a Firebase user
    private void createFirebaseUser() {

        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();



        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("RegisterActivity", "createUser onComplete: " + task.isSuccessful());

                        if(!task.isSuccessful()){
                            Log.d("RegisterActivity", "user creation failed");
                            showErrorDialog("Registration attempt failed");
                        } else {
                            mAuth.getCurrentUser()
                                    .sendEmailVerification()
                                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //re-enable re-sent button

                                            if(task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this,getString(R.string.vertification_email_sent_to) + email,
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e("RegisterActivity","send vertification email failed", task.getException());
                                                Toast.makeText(RegisterActivity.this,getString(R.string.vertification_email_sent_to) + email,
                                                        Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                            saveUserInformationFire();
                            final Intent intent = new Intent(RegisterActivity.this, com.smarthuman.drstock.LoginActivity.class);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    RegisterActivity.this.finish();
                                    startActivity(intent);
                                }
                            }, 2000);


                        }
                    }
                });
    }

    private void saveUserInformationFire() {
        String userName = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();

        UserInformation userInformation = new UserInformation(userName, email);

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("users").child(firebaseUser.getUid()).setValue(userInformation);

        Log.d("RegisterActivity","user information saved ...");
    }

   // : Save the display name to Shared Preferences
//    private void saveDisplayName() {
//        String displayName = mUsernameView.getText().toString();
//        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS, 0);
//        prefs.edit().putString(DISPLAY_NAME_KEY, displayName).apply();
//    }

    // : Create an alert dialog to show in case registration failed
    private void showErrorDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }



}
