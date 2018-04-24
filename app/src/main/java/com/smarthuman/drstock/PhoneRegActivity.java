package com.smarthuman.drstock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneRegActivity extends AppCompatActivity {

    private static final String TAG = "PhoneAuth";

    private EditText phoneText;
    private TextInputLayout codeTextLayout;
    private EditText codeText;
    private Button verifyButton;
    private Button sendButton;
    private Button resendButton;


    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_reg);

        phoneText = (EditText) findViewById(R.id.phoneText);

        codeText = (EditText) findViewById(R.id.codeText);
        codeTextLayout = (TextInputLayout) findViewById(R.id.codeTextLayout) ;
        verifyButton = (Button) findViewById(R.id.verifyButton);
        sendButton = (Button) findViewById(R.id.sendSMS);
        resendButton = (Button) findViewById(R.id.resendButton);

        verifyButton.setEnabled(false);
        verifyButton.setVisibility(View.INVISIBLE);
        resendButton.setEnabled(false);
        resendButton.setVisibility(View.INVISIBLE);
        codeText.setHint("");
        codeText.setVisibility(View.INVISIBLE);
        codeTextLayout.setVisibility(View.INVISIBLE);


        mAuth = FirebaseAuth.getInstance();


    }

    public void onSendCode(View view) {

        String phoneNumber = phoneText.getText().toString();

        if(phoneNumber.length()<=6) {
            Toast.makeText(getApplicationContext(),R.string.invalid_phone_num,Toast.LENGTH_SHORT).show();
            return;
        }
        setUpVerificatonCallbacks();

        Toast.makeText(getApplicationContext(), getString(R.string.SMS_has_been_sent) + " " + phoneNumber ,
                Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    public void setUpVerificatonCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {

                        resendButton.setEnabled(false);
                        resendButton.setVisibility(View.INVISIBLE);
                        verifyButton.setEnabled(false);
                        verifyButton.setVisibility(View.INVISIBLE);

                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Log.d(TAG, "Invalid credential: "
                                    + e.getLocalizedMessage());
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(TAG, "SMS Quota exceeded.");
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;
                        resendToken = token;

                        phoneText.setVisibility(View.VISIBLE);
                        verifyButton.setEnabled(true);
                        verifyButton.setVisibility(View.VISIBLE);
                        sendButton.setEnabled(false);
                        sendButton.setVisibility(View.INVISIBLE);
                        resendButton.setEnabled(true);
                        resendButton.setVisibility(View.VISIBLE);
                        codeText.setVisibility(View.VISIBLE );
                        codeTextLayout.setVisibility(View.VISIBLE);

                        codeText.setHint(R.string.SMS_code);

                        Log.d(TAG, "code sent complete");

                    }
                };
    }

    public void SignIn(View view) {

        String code = codeText.getText().toString();
        if(code.equals(""))
            return;

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //successfully login
                            mUser = task.getResult().getUser();
                            //codeText.setText("");
                            resendButton.setEnabled(false);
                            resendButton.setVisibility(View.INVISIBLE);
                            verifyButton.setEnabled(false);
                            verifyButton.setVisibility(View.INVISIBLE);

                            Toast.makeText(getApplicationContext(), R.string.login_process, Toast.LENGTH_SHORT).show();
                            saveUserInformationFire();


                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), R.string.invalid_code, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void resendCode(View view) {

        String phoneNumber = phoneText.getText().toString();

        setUpVerificatonCallbacks();


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }

    public void saveUserInformationFire() {

        final String Uid = mUser.getUid();
        //Toast.makeText(this, "getUid: " + Uid, Toast.LENGTH_SHORT).show();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // mUser = mAuth.getCurrentUser();
        mDatabaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> postValues = new HashMap<String, Object>();
                boolean uidExist = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(Uid)) {
                        uidExist = true;
                        break;
                    }
                }

                if (!uidExist) {

                    final String phoneNumber = phoneText.getText().toString();

                    AlertDialog alertDialog = new AlertDialog.Builder(PhoneRegActivity.this).create();
                    alertDialog.setTitle(getString(R.string.prompt_username));
                    alertDialog.setMessage(getString(R.string.set_username));
                    final EditText edittext = new EditText(getApplicationContext());
                    alertDialog.setView(edittext);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String userName = edittext.getText().toString();
                                    UserInformation user = new UserInformation(userName, phoneNumber);
                                    mDatabaseReference.child("users").child(Uid).setValue(user);

                                    Log.d(TAG, "userName is " + userName);
                                    Log.d(TAG, "user information saved ...");

                                    MainActivity.mUid = Uid;
                                    PhoneRegActivity.this.finish();
                                    MainActivity.updateUserInfo();
                                    MainActivity.setViewPager(0);
                                }
                            });
                    alertDialog.show();




                }

                if(uidExist) {
                    MainActivity.mUid = Uid;
                    PhoneRegActivity.this.finish();
                    MainActivity.updateUserInfo();
                    MainActivity.setViewPager(0);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                MainActivity.mUid = Uid;
//                PhoneRegActivity.this.finish();
//                MainActivity.updateUserInfo();
//                MainActivity.setViewPager(0);
//            }
//        }, 2000);



    }

}

