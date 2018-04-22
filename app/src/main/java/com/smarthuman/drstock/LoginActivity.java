package com.smarthuman.drstock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    // : Add member variables here:
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private SignInButton mGooglebtn;

    //Google
    static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private final String TAG = "LoginActivity";
    boolean isGoogle = false;
    private String Uid;
    Button resendBtn;


    //Facebook
    public CallbackManager mCallbackManager;
    boolean isFacebook = false;

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

        resendBtn = findViewById(R.id.resend_email_btn);
        resendBtn.setVisibility(View.INVISIBLE);

        //**************Google Sign in***************************
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1051114279723-pejj6gaem6m1422kfcas7arv3qb9hkmr.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Build a GoogleSignInClient with the options specified by gso.

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, getString(R.string.toast_error),Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        MainActivity.mGoogleApiClient = mGoogleApiClient;
        mGooglebtn =(SignInButton) findViewById(R.id.google_sign_in_btn);
        mGooglebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGoogle = true;
                isFacebook = false;
                MainActivity.isGoogle = true;
                MainActivity.isFacebook = false;
                GoogleLogin();
            }
        });



        // *****************Facebook Sign in ***************
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton facebookloginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGoogle = false;
                isFacebook = true;
                MainActivity.isGoogle = false;
                MainActivity.isFacebook = true;
            }
        });
        facebookloginButton.setReadPermissions("email", "public_profile");
        facebookloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());


            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
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

                Log.d(TAG, "signInWithEmail() onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d(TAG, "Problem signing in: " + task.getException());
                    showErrorDialog(R.string.problem_signin_msg);
                } else {
                    //login successfully
                    //saveEmailtoPref();

                    if(mUser!=null && mUser.isEmailVerified()) {
                        finish();
                        MainActivity.updateUserInfo();
                        MainActivity.setViewPager(0);
                    } else {

                        resendBtn.setVisibility(View.VISIBLE);
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
    public void saveEmailtoPref() {
        String email = mEmailView.getText().toString();
        SharedPreferences prefs = getSharedPreferences(LOGIN_PREF, 0);
        prefs.edit().putString(PREF_EMAIL_KEY, email).apply();
    }


    public void onResend(View view) {
        final String email = mEmailView.getText().toString();
        if(mAuth.getCurrentUser().isEmailVerified()) {
            Toast.makeText(LoginActivity.this,getString(R.string.have_verified_email) + " " +email,
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.getCurrentUser()
                    .sendEmailVerification()
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //re-enable re-sent button
                            resendBtn.setEnabled(false);

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, getString(R.string.vertification_email_sent_to) + " " + email,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "send vertification email failed", task.getException());
                                Toast.makeText(LoginActivity.this, getString(R.string.vertification_email_sent_to) + " " + email,
                                        Toast.LENGTH_SHORT).show();
                                findViewById(R.id.resend_email_btn).setEnabled(true);
                            }
                        }
                    });
        }
    }

    public void onLoginPhone(View v) {
        Intent intent = new Intent(this, com.smarthuman.drstock.PhoneRegActivity.class);
        finish();
        startActivity(intent);
    }

    public void GoogleLogin() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        mUser = mAuth.getCurrentUser();
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            Uid = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            Log.d(TAG, "uesrname:" + personName + "email:" + personEmail);
            createUserFirebase(personName, personEmail);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(isGoogle) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    Toast.makeText(getApplicationContext(),R.string.Google_auth_fail,Toast.LENGTH_SHORT).show();
                    // ...
                }

            }
            isGoogle = false;
        }

        else if(isFacebook) {
            isFacebook = false;
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(LoginActivity.this, R.string.login_process,
                                    Toast.LENGTH_SHORT).show();

                            // here sign up


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.Google_auth_fail,
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    public void FacebookLogin() {
        isGoogle = false;
        isFacebook = true;

        if (mUser != null) {
            String userName = mUser.getDisplayName();
            String userEmail = mUser.getEmail();
            Uid = mUser.getUid();
            Log.d(TAG,"email:"+userEmail + ", name:" + userName);
            //Log.d(TAG,  "userName is  " + userName);
            // Log.d(TAG,  "userName is  " + userEmail);
            //UserInformation ui = new UserInformation(userEmail);
            createUserFirebase(userName, userEmail);
        }

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            mUser = task.getResult().getUser();

                            FacebookLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "facebook signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.vertification_email_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    //TODO:
    public void createUserFirebase(final String userName, final String Email) {
        //Toast.makeText(this, "Google/facebook sign in successfully, uid:" + Uid,Toast.LENGTH_SHORT).show();


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
       // mUser = mAuth.getCurrentUser();
        mDatabaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> postValues = new HashMap<String,Object>();
                boolean uidExist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals(Uid)) {
                        uidExist = true;
                        break;
                    }
                }

                //TODO: here can't create user for Google login
                if(!uidExist) {
                    UserInformation user = new UserInformation(userName, Email);

                    mDatabaseReference.child("users").child(Uid).setValue(user);
                    Log.d(TAG,"user information saved ...");
                    MainActivity.mUid = Uid;
                    LoginActivity.this.finish();
                    MainActivity.updateUserInfo();
                    MainActivity.setViewPager(0);
                }

                if(uidExist) {
                    MainActivity.mUid = Uid;
                    LoginActivity.this.finish();
                    MainActivity.updateUserInfo();
                    MainActivity.setViewPager(0);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void FacebookSignOut() {

        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    public void GoogleSignOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}