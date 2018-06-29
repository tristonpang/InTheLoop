package com.llawl.tristonpang.intheloop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

public class EventDetailsActivity extends AppCompatActivity {
    private ImageView mEventImgView;
    private TextView mEventNameView;
    private TextView mEventDateView;
    private TextView mEventTimeView;
    private TextView mEventVenueView;
    private TextView mEventDescView;
    private String mEventName;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Button mSignupButton;
    private boolean mAlreadySignedUp;
    private DatabaseReference mUserSignupRef;
    private String mCurrentUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mCurrentUserKey = currentUser.replace(".", "-");
        mUserSignupRef = FirebaseDatabase.getInstance().getReference().child("user_signups").child(mCurrentUserKey);
        mStorageReference = FirebaseStorage.getInstance().getReference();

        //link all views
        mEventImgView = findViewById(R.id.eventDetailsImg);
        mEventNameView = findViewById(R.id.eventDetailsNameView);
        mEventDateView = findViewById(R.id.eventDetailsDateView);
        mEventTimeView = findViewById(R.id.eventDetailsTimeView);
        mEventVenueView = findViewById(R.id.eventDetailsVenueView);
        mEventDescView = findViewById(R.id.eventDetailsDescView);
        mSignupButton = findViewById(R.id.eventDetailsSignupButton);

        //retrieve event name
        mEventName = getIntent().getStringExtra("eventName");

        //retrieve all relevant event data from database
        String eventNameKey = mEventName.replace(" ", "_");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events_info");
        mDatabaseReference.child(eventNameKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrieveDetails(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //check if user is already signed up
        mUserSignupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> data = (HashMap<String, Boolean>) dataSnapshot.getValue();
                String eventNameKey = mEventName.replace(" ", "_");
                mAlreadySignedUp = data != null && data.get(eventNameKey) != null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set custom click listener for button
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAlreadySignedUp) {
                    attemptSignupForEvent();
                } else {
                    Toast.makeText(EventDetailsActivity.this, R.string.already_signed_up, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void retrieveDetails(DataSnapshot snapshot) {
        HashMap<String, String> data = (HashMap) snapshot.getValue();
        //set all text views
        String date = getString(R.string.date_formatted, data.get("date"));
        String time = getString(R.string.time_formatted, data.get("time"));
        String venue = getString(R.string.venue_formatted, data.get("venue"));

        mEventNameView.setText(mEventName);
        mEventDateView.setText(date);
        mEventTimeView.setText(time);
        mEventVenueView.setText(venue);
        mEventDescView.setText(data.get("desc"));

        //retrieve and set image
        String imgName = data.get("imageName");
        StorageReference pathRef = mStorageReference.child("images/" + imgName);
        Glide.with(this).using(new FirebaseImageLoader()).load(pathRef).into(mEventImgView);
    }

    /*
    public void signup(View v) {
        attemptSignupForEvent();
    }
    */

    private void attemptSignupForEvent() {
        //update event attendance
        final String eventNameKey = mEventName.replace(" ", "_");
        final DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child("event_attendance").child(eventNameKey);

        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                HashMap<String, Boolean> data = (HashMap<String, Boolean>) snapshot.getValue();

                if (data == null) {
                    Log.d("InTheLoop", "Null attendance HashMap");
                    HashMap<String, Boolean> newMap = new HashMap<>();
                    newMap.put(mCurrentUserKey, true);
                    attendanceRef.setValue(newMap);
                } else {
                    Log.d("InTheLoop", "HashMap exists, update");
                    HashMap<String, Boolean> newMap = new HashMap<>(data);
                    newMap.put(mCurrentUserKey, true);
                    attendanceRef.setValue(newMap);
                }
            }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        //grab initial list of signed up events, update list
        mUserSignupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> data = (HashMap<String, Boolean>) dataSnapshot.getValue();

                if (data == null) {
                    Log.d("InTheLoop", "HashMap is null, user has not signed up for any events yet");
                    HashMap<String, Boolean> newMap = new HashMap<>();
                    newMap.put(eventNameKey, true);
                    mUserSignupRef.setValue(newMap);
                } else {
                    Log.d("InTheLoop", "HashMap exists, update");
                    HashMap<String, Boolean> newMap = new HashMap<>(data);
                    newMap.put(eventNameKey, true);
                    mUserSignupRef.setValue(newMap);
                }
                signupComplete();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void signupComplete() {
        Toast.makeText(this, R.string.signup_success, Toast.LENGTH_LONG).show();
        finish();
    }

}
