package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;

public class MyInfoActivity extends AppCompatActivity {

    public final static int QRcodeWidth = 500 ;
    private TextView mNameView;
    private TextView mEmailView;
    private TextView mRcView;
    private DatabaseReference mDatabaseReference;
    private ImageView mQRCodeView;
    private String mUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        //nav bar set up
        BottomNavigationView navView = findViewById(R.id.navigationView);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_myinfo:
                        break;
                    case R.id.navigation_events:
                        Log.d("InTheLoop", "Nav from MyInfo to EventsList");
                        goToActivity(EventsListActivity.class);
                        break;
                    case R.id.navigation_org:
                        Log.d("InTheLoop", "Nav from MyInfo to Organisers");
                        goToActivity(OrganisersActivity.class);
                        break;
                }



                return true;
            }
        });

        //get user email
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUserEmail = user.getEmail();

        //link all views
        mNameView = findViewById(R.id.myNameView);
        mEmailView = findViewById(R.id.myEmailView);
        mRcView = findViewById(R.id.myRcView);
        mQRCodeView = findViewById(R.id.myQRCodeView);

        //retrieve all user details
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //update views
                updateViews(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //generate and display QR code from user email
        try {
            Bitmap bitmap = TextToImageEncode(mUserEmail);

            mQRCodeView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
            Log.d("InTheLoop", "There was a problem with the QR Code generation!");
        }

    }

    private void updateViews(DataSnapshot dataSnapshot) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //retrieve details from snapshot
        String emailKey = mUserEmail.replace(".","-");

        HashMap<String,String> data = (HashMap) dataSnapshot.child("users").child(emailKey).getValue();
        String name = data.get("name");
        String rc = data.get("rc");
        String contactNum = data.get("contactNum");

        Log.d("InTheLoop", "updateViews() data retrieved from snapshot, Name: " + data.get("name"));


        //update UI
        mEmailView.setText(getString(R.string.email_formatted, mUserEmail));
        mNameView.setText(getString(R.string.name_formatted, name));
        mRcView.setText(getString(R.string.rc_formatted, rc));
    }

    private void goToActivity(Class destActivity) {
        Intent intent = new Intent(MyInfoActivity.this, destActivity);
        finish();
        overridePendingTransition(0,0);
        startActivity(intent);
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? getResources().getColor(R.color.colorPrimaryDark):getResources().getColor(R.color.white);            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void scanSignUp(View v) {
        goToScanner();
    }

    private void goToScanner() {
        Intent intent = new Intent(MyInfoActivity.this, ScannerActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * Method that receives and processes information given back by the ScannerActivity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                final String scanValue = data.getStringExtra("scanValue");
                //Toast.makeText(this, scanValue, Toast.LENGTH_SHORT).show();

                //check if scanValue is a valid event code
                mDatabaseReference.child("events_info").child(scanValue).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Boolean> data = (HashMap) dataSnapshot.getValue();
                        if (data == null) {
                            Toast.makeText(MyInfoActivity.this, R.string.invalid_event_code, Toast.LENGTH_LONG).show();
                        } else {
                            signUpForEvent(scanValue);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
    }

    private void signUpForEvent(String scanVal) {
        final String scanValue = scanVal;
        final String userEmailKey = mUserEmail.replace(".", "-");
        final DatabaseReference userSignUpRef = mDatabaseReference.child("user_signups").child(userEmailKey);
        final DatabaseReference attendanceRef = mDatabaseReference.child("event_attendance").child(scanValue);

        //add to user signup list
        userSignUpRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> data = (HashMap) dataSnapshot.getValue();
                if (data == null) {
                    HashMap<String, Boolean> newMap = new HashMap<>();
                    newMap.put(scanValue, true);
                    userSignUpRef.setValue(newMap);
                } else {
                    HashMap<String, Boolean> newMap = new HashMap<>(data);
                    newMap.put(scanValue, true);
                    userSignUpRef.setValue(newMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //add to event attendance list
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> data = (HashMap) dataSnapshot.getValue();
                if (data == null) {
                    HashMap<String, Boolean> newMap = new HashMap<>();
                    newMap.put(userEmailKey, true);
                    attendanceRef.setValue(newMap);
                } else {
                    HashMap<String, Boolean> newMap = new HashMap<>(data);
                    newMap.put(userEmailKey, true);
                    attendanceRef.setValue(newMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(this, R.string.signup_success, Toast.LENGTH_LONG).show();
    }

    public void mySignedUpEvents(View v) {
        goToMySignedUpEvents();
    }

    private void goToMySignedUpEvents() {
        Intent intent = new Intent(MyInfoActivity.this, MySignedUpEventsActivity.class);
        startActivity(intent);
    }
}
