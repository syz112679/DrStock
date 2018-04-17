package com.smarthuman.drstock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    // : Add member variables here:
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    // Constants
    public static final String LOGIN_PREF = "LoginPrefs";
    public static final String PREF_EMAIL_KEY = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // : Grab an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        // : Call attemptLogin() here

        attemptLogin();
    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, com.smarthuman.drstock.RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    // : Complete the attemptLogin() method
    private void attemptLogin() {

        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (email.isEmpty())
            if (email.equals("") || password.equals("")) return;

        // : Use FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d("FlashChat", "signInWithEmail() onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d("login", "Problem signing in: " + task.getException());
                    showErrorDialog(R.string.problem_signin_msg);
                } else {
                    //login successfully
                    //saveEmailtoPref();
                    if(mUser!=null && mUser.isEmailVerified()) {
                        finish();
                        MainActivity.updateUserInfo();
                        MainActivity.setViewPager(0);
                    } else {
                        Toast.makeText(LoginActivity.this,getString(R.string.go_to_vertify_email),
                                    Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }

    // : Show error on screen with an alert dialog
    private void showErrorDialog(int message) {

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    // : Save the display name to Shared Preferences
    private void saveEmailtoPref() {
        String email = mEmailView.getText().toString();
        SharedPreferences prefs = getSharedPreferences(LOGIN_PREF, 0);
        prefs.edit().putString(PREF_EMAIL_KEY, email).apply();
    }


    private void onVertifyEmail(View view) {
        final String email = mEmailView.getText().toString();
        mAuth.getCurrentUser()
                .sendEmailVerification()
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //re-enable re-sent button
                        findViewById(R.id.resend_email_btn).setEnabled(false);

                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,getString(R.string.vertification_email_sent_to) + " " +email,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("RegisterActivity","send vertification email failed", task.getException());
                            Toast.makeText(LoginActivity.this,getString(R.string.vertification_email_sent_to) +" "+ email,
                                    Toast.LENGTH_SHORT).show();
                            findViewById(R.id.resend_email_btn).setEnabled(true);
                        }
                    }
                });
    }

    private void onLoginGoogle(View v) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void onLoginFacebook(View v) {}
}