package com.llawl.tristonpang.intheloop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EditEventActivity extends AppCompatActivity {
    private int RESULT_LOAD_IMAGE = 300;
    private int PERMISSION_REQUEST_CODE = 400;

    private String mEventName;
    private EditText mEventDateView;
    private EditText mEventTimeView;
    private EditText mEventVenueView;
    private EditText mEventDescView;
    private Button mEventImgButton;
    private DatabaseReference mDatabaseReference;
    private String mImageName;
    private String mPicPath;
    private StorageReference mStorageReference;
    private String mOrganiser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        mStorageReference = FirebaseStorage.getInstance().getReference();

        //retrieve event name
        mEventName = getIntent().getStringExtra("eventName");

        mPicPath = null;

        //link all views
        mEventDateView = findViewById(R.id.editEventDateView);
        mEventTimeView = findViewById(R.id.editEventTimeView);
        mEventVenueView = findViewById(R.id.editEventVenueView);
        mEventDescView = findViewById(R.id.editEventDescView);
        mEventImgButton = findViewById(R.id.editEventImgButton);

        //set initial text/details for all views
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events_info");
        String eventNameKey = mEventName.replace(" ", "_");
        mDatabaseReference.child(eventNameKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrieveInitialDetails(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void retrieveInitialDetails(DataSnapshot snapshot) {
        HashMap<String, String> data = (HashMap) snapshot.getValue();
        mEventDateView.setText(data.get("date"));
        mEventTimeView.setText(data.get("time"));
        mEventVenueView.setText(data.get("venue"));
        mEventDescView.setText(data.get("desc"));
        mImageName = data.get("imageName");
        mOrganiser = data.get("organiser");
    }

    public void changeImage(View v) {
        openGallery();
    }

    private void openGallery() {
        //check for, and if necessary, ask for permission to read storage
        //then return and look at results in onRequestPermissionsResult callback
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return;
        }

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("InTheLoop", "onRequestPermissionsResult(): Read Permission Granted!");
                openGallery();
            } else {
                Log.d("Clima", "getRequestPermissionsResult(): Read Permission Denied" );
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            mPicPath = picturePath;
            cursor.close();

            // String picturePath contains the path of selected Image

            Log.d("InTheLoop", "onActivityResult, picturePath: " + picturePath);
            mEventImgButton.setText(R.string.change_selected_img);


        }
    }

    public void submit(View v) {
        confirmChanges();
    }

    private void confirmChanges() {
        if (mPicPath != null) {
            //delete old pic
            StorageReference pathRef = mStorageReference.child("images/" + mImageName);
            pathRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("InTheLoop", "Event image: " + mImageName + " successfully overwritten!");
                }
            });

            //upload new pic
            uploadSelectedPic();
        }

        //update text details first
        HashMap<String, String> newValues = new HashMap<>();
        newValues.put("date", mEventDateView.getText().toString());
        newValues.put("time", mEventTimeView.getText().toString());
        newValues.put("venue", mEventVenueView.getText().toString());
        newValues.put("desc", mEventDescView.getText().toString());
        newValues.put("imageName", mImageName);
        newValues.put("name", mEventName);
        newValues.put("organiser", mOrganiser);

        HashMap<String, Object> updates = new HashMap<>();
        String eventNameKey = mEventName.replace(" ", "_");
        updates.put(eventNameKey, newValues);

        mDatabaseReference.updateChildren(updates);

        finish();

    }

    private void uploadSelectedPic() {
        Uri file = Uri.fromFile(new File(mPicPath));
        StorageReference picLocation = mStorageReference.child("images/"+file.getLastPathSegment());
        mImageName = file.getLastPathSegment();
        Log.d("InTheLoop", "mPicPath - getLastPathSegment(): " + mImageName);
        UploadTask uploadTask = picLocation.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("InTheLoop", "uploadTask failure!" + exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Log.d("InTheLoop", "uploadTask Success!");
            }
        });
    }
}
