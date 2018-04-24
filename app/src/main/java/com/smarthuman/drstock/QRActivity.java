package com.smarthuman.drstock;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.api.report.UsersResult;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler  {

    private ImageView imageView;
    final String TAG = "QRActiviey";
    Bitmap bitmap;

    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        imageView = (ImageView) this.findViewById(R.id.qr_imageview);

        String text2Qr = MainActivity.mUid;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    public void scan(View view){
        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(zXingScannerView!=null) {
            zXingScannerView.removeAllViews(); //<- here remove all the views, it will make an Activity having no View
            zXingScannerView.stopCamera(); //<- then stop the camera
            setContentView(R.layout.activity_qr); //<- and set the View again.
            imageView.setImageBitmap(bitmap);
        }
    }

    public UserInformation otherUser;

    @Override
    public void handleResult(final Result result) {
        zXingScannerView.stopCamera();

        // zXingScannerView.resumeCameraPreview(this);
        final String otherUserId = result.getText();
        Log.d(TAG, "otherUserID: " + otherUserId);

        //other user info
        final GenericTypeIndicator<ArrayList<String>> favorite_t = new GenericTypeIndicator<ArrayList<String>>() {};
        final GenericTypeIndicator<ArrayList<StockSnippet>> stockRecord_t = new GenericTypeIndicator<ArrayList<StockSnippet>>() {};


        MainActivity.mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
//                otherUser =  (UserInformation) dataSnapshot.child("users").child(otherUserId).getValue(UserInformation.class);
//                 Log.d(TAG, "inside, otherUserID" + otherUser.getUserName());
                 String UserName = dataSnapshot.child("users").child(otherUserId).child("userName").getValue(String.class);
                 String Email = dataSnapshot.child("users").child(otherUserId).child("email").getValue(String.class);
                 double Money = dataSnapshot.child("users").child(otherUserId).child("money").getValue(double.class);
                 double Earning  = dataSnapshot.child("users").child(otherUserId).child("earning").getValue(double.class);
                 double Balance = dataSnapshot.child("users").child(otherUserId).child("balance").getValue(double.class);

                 ArrayList<String> Favorites = dataSnapshot.child("users").child(otherUserId).child("favorites").getValue(favorite_t);
                 ArrayList<StockSnippet> StockRecords = dataSnapshot.child("users").child(otherUserId).child("myStocks").getValue(stockRecord_t);

                 otherUser = new UserInformation(UserName,Email,Money,Earning,Balance,StockRecords,Favorites,false);
             }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



        if(otherUser!=null) {


            final AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
            final EditText input_et = new EditText(this);
            String message = getString(R.string.text_how_much_money_transfer) + " " + otherUser.getUserName();

            alert3.setMessage(message);
            alert3.setTitle(R.string.text_transfer_btn);
            alert3.setView(input_et);

            alert3.setPositiveButton(R.string.text_transfer_btn, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String inputMoney = input_et.getText().toString();
                    double inputMoney_db = Double.parseDouble(inputMoney);

                    if (inputMoney_db <= MainActivity.mBalance) {

                        MainActivity.mMoney -= inputMoney_db;
                        MainActivity.mBalance -= inputMoney_db;

                        otherUser.setBalance(otherUser.getBalance() + inputMoney_db);
                        otherUser.setMoney(otherUser.getMoney() + inputMoney_db);

                        MainActivity.mDatabaseReference.child("users").child(otherUserId).setValue(otherUser);

                        Toast.makeText(getApplicationContext(), R.string.transfer_success, Toast.LENGTH_SHORT).show();


                    } else if (inputMoney_db <= 0) {
                        Toast.makeText(getApplicationContext(), R.string.toast_invalid_input, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.toast_you_dont_have_enough_money, Toast.LENGTH_SHORT).show();
                    }


                    zXingScannerView.removeAllViews(); //<- here remove all the views, it will make an Activity having no View
                    zXingScannerView.stopCamera(); //<- then stop the camera
                    setContentView(R.layout.activity_qr); //<- and set the View again.
                    imageView = (ImageView) findViewById(R.id.qr_imageview);
                    imageView.setImageBitmap(bitmap);

                }
            });

            alert3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    zXingScannerView.removeAllViews(); //<- here remove all the views, it will make an Activity having no View
                    zXingScannerView.stopCamera(); //<- then stop the camera
                    setContentView(R.layout.activity_qr); //<- and set the View again.
                    imageView.setImageBitmap(bitmap);
                }
            });

            alert3.show();
            //zXingScannerView.resumeCameraPreview(this);
        } else {

            Toast.makeText(this, R.string.cannot_understand_qr, Toast.LENGTH_SHORT).show();
            zXingScannerView.removeAllViews(); //<- here remove all the views, it will make an Activity having no View
            zXingScannerView.stopCamera(); //<- then stop the camera
            setContentView(R.layout.activity_qr); //<- and set the View again.
            imageView = (ImageView) this.findViewById(R.id.qr_imageview);
            imageView.setImageBitmap(bitmap);

        }
    }
}
