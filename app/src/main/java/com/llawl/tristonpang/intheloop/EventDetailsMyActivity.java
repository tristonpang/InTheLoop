package com.llawl.tristonpang.intheloop;

import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
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

public class EventDetailsMyActivity extends AppCompatActivity {
    private ImageView mEventImgView;
    private TextView mEventNameView;
    private TextView mEventDateView;
    private TextView mEventTimeView;
    private TextView mEventVenueView;
    private TextView mEventDescView;
    private String mEventName;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private DatabaseReference mUserSignupRef;
    private String mCurrentUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_my);
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mCurrentUserKey = currentUser.replace(".", "-");
        mUserSignupRef = FirebaseDatabase.getInstance().getReference().child("user_signups").child(mCurrentUserKey);
        mStorageReference = FirebaseStorage.getInstance().getReference();

        //link all views
        mEventImgView = findViewById(R.id.eventDetailsMyImg);
        mEventNameView = findViewById(R.id.eventDetailsMyNameView);
        mEventDateView = findViewById(R.id.eventDetailsMyDateView);
        mEventTimeView = findViewById(R.id.eventDetailsMyTimeView);
        mEventVenueView = findViewById(R.id.eventDetailsMyVenueView);
        mEventDescView = findViewById(R.id.eventDetailsMyDescView);


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


    public void withdraw(View v) {
        showWithdrawAlertDialog();
    }

    private void showWithdrawAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to withdraw from this event?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attemptWithdrawFromEvent();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void attemptWithdrawFromEvent() {
        //update event attendance
        final String eventNameKey = mEventName.replace(" ","_");
        final DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child("event_attendance").child(eventNameKey);
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> data = (HashMap<String, Boolean>) dataSnapshot.getValue();

                data.remove(mCurrentUserKey);
                attendanceRef.setValue(data);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //grab initial list of signed up events, delete from list
        mUserSignupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> data = (HashMap<String, Boolean>) dataSnapshot.getValue();
                Log.d("InTheLoop", "Withdraw from event - removing " + eventNameKey + " from user's sign-ups list");
                data.remove(eventNameKey);
                mUserSignupRef.setValue(data);

                withdrawComplete();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void withdrawComplete() {
        Toast.makeText(this, R.string.withdrawn_from_event, Toast.LENGTH_LONG).show();
        finish();
    }


}
