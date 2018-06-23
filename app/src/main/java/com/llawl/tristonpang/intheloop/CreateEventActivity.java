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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class CreateEventActivity extends AppCompatActivity {
    private int RESULT_LOAD_IMAGE = 100;
    private int PERMISSION_REQUEST_CODE = 200;

    private EditText mEventNameView;
    private EditText mEventDateView;
    private EditText mEventTimeView;
    private EditText mEventVenueView;
    private EditText mEventDescView;
    private Button mEventPicSubmit;
    private String mImageName;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference; //used to store image


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //link all views
        mEventNameView = findViewById(R.id.createEventNameField);
        mEventDateView = findViewById(R.id.createEventDateField);
        mEventTimeView = findViewById(R.id.createEventTimeField);
        mEventVenueView = findViewById(R.id.createEventVenueField);
        mEventDescView = findViewById(R.id.createEventDescField);
        mEventPicSubmit = findViewById(R.id.createEventImageButton);

        //initialise image name (to empty)
        mImageName = null;

        //set up database and storage references
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();


    }

    public void uploadImage(View v) {
        openGallery();
    }

    private void openGallery() {
        //check for, and if necessary, ask for permission to read storage
        //then return and look at results in onRequestPermissionsResult callback
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
            cursor.close();

            // String picturePath contains the path of selected Image

            Log.d("InTheLoop", "onActivityResult, picturePath: " + picturePath);

            Uri file = Uri.fromFile(new File(picturePath));
            StorageReference picLocation = mStorageReference.child("images/"+file.getLastPathSegment());
            mImageName = file.getLastPathSegment();
            Log.d("InTheLoop", "picturePath - getLastPathSegment(): " + mImageName);
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
                    mEventPicSubmit.setText(R.string.change_selected_img);
                }
            });
        }
    }

    public void createNewEvent(View v) {
        attemptCreation();
    }

    private void attemptCreation() {
        if (isNameValid() && isDateValid() && isTimeValid() && isVenueValid() && isDescValid() && isImageValid()) {
            addNewEventToDatabase();
            //go back to previous page (OrganisersActivity)
            finish();
        } else {
            Toast.makeText(this, R.string.validate_text, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNameValid() {
        return mEventNameView.getText().toString().length() > 0;
    }

    private boolean isDateValid() {
        return mEventDateView.getText().toString().length() > 0;
    }

    private boolean isTimeValid() {
        return mEventTimeView.getText().toString().length() > 0;
    }

    private boolean isVenueValid() {
        return mEventVenueView.getText().toString().length() > 0;
    }

    private boolean isDescValid() {
        return mEventDescView.getText().toString().length() > 0;
    }

    private boolean isImageValid() {
        return mImageName != null;
    }

    private void addNewEventToDatabase() {
        //retrieve details from fields
        String name = mEventNameView.getText().toString();
        String date = mEventDateView.getText().toString();
        String time = mEventTimeView.getText().toString();
        String venue = mEventVenueView.getText().toString();
        String desc = mEventDescView.getText().toString();
        String organiser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //create new event instance
        EventInfo newEvent = new EventInfo(name, date, time, venue, desc, mImageName, organiser);

        //store in database under name (spaces replaced with underscores)
        name = name.replace(" ", "_");
        mDatabaseReference.child("events_info").child(name).setValue(newEvent);
        Log.d("InTheLoop", "New event created and added: " + name + ", by " + organiser);
    }

}
