package com.llawl.tristonpang.intheloop;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Set;

public class EventDetailsAdminDelActivity extends AppCompatActivity {
    private String mEventName;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private ImageView mEventImage;
    private TextView mEventNameView;
    private TextView mEventDateView;
    private TextView mEventTimeView;
    private TextView mEventVenueView;
    private TextView mEventDescView;
    private String mImageName;
    private String mOrganiser;
    private String mEventDate;
    private String mEventTime;
    private String mEventVenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_admin_del);

        //retrieve event name
        mEventName = getIntent().getStringExtra("eventName");

        //link all views
        mEventImage = findViewById(R.id.eventDetailsAdminDelImg);
        mEventNameView = findViewById(R.id.eventDetailsAdminDelNameView);
        mEventDateView = findViewById(R.id.eventDetailsAdminDelDateView);
        mEventTimeView = findViewById(R.id.eventDetailsAdminDelTimeView);
        mEventVenueView = findViewById(R.id.eventDetailsAdminDelVenueView);
        mEventDescView = findViewById(R.id.eventDetailsAdminDelDescView);

        mStorageReference = FirebaseStorage.getInstance().getReference();

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
        mEventDate = data.get("date");
        mEventTime = data.get("time");
        mEventVenue = data.get("venue");
        //set all text views
        String date = getString(R.string.date_formatted, mEventDate);
        String time = getString(R.string.time_formatted, mEventTime);
        String venue = getString(R.string.venue_formatted, mEventVenue);

        mEventNameView.setText(mEventName);
        mEventDateView.setText(date);
        mEventTimeView.setText(time);
        mEventVenueView.setText(venue);
        mEventDescView.setText(data.get("desc"));
        mOrganiser = data.get("organiser");

        //retrieve and set image
        String imgName = data.get("imageName");
        mImageName = imgName;
        StorageReference pathRef = mStorageReference.child("images/" + imgName);
        Glide.with(this).using(new FirebaseImageLoader()).load(pathRef).into(mEventImage);
    }

    public void adminDelete(View v) {
        showDeleteDialog("Are you sure you want to delete this event?");
    }

    private void showDeleteDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        proceedWithDelete();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void proceedWithDelete() {
        String eventNameKey = mEventName.replace(" ", "_");

        //iterate through attendance list and remove from each user signups, then delete attendance list
        final DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child("event_attendance").child(eventNameKey);
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> data = (HashMap) dataSnapshot.getValue();
                if(data != null) deleteFromAttendance(data.keySet());
                attendanceRef.removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //find and delete image
        StorageReference pathRef = mStorageReference.child("images/" + mImageName);
        pathRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("InTheLoop", "Event image: " + mImageName + " successfully deleted");
            }
        });

        //find and delete event details
        mDatabaseReference.child(eventNameKey).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d("InTheLoop", "Event " + mEventName + " successfully deleted");
            }
        });

        finish();
    }

    private void deleteFromAttendance(Set<String> dataSet) {
        final String eventNameKey = mEventName.replace(" ", "_");
        final DatabaseReference userSignUps = FirebaseDatabase.getInstance().getReference().child("user_signups");
        for (final String user : dataSet) {
            userSignUps.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Boolean> data = (HashMap) dataSnapshot.getValue();
                    data.remove(eventNameKey);
                    if (data.isEmpty()) userSignUps.child(user).setValue(null);
                    else userSignUps.child(user).setValue(data);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }




}
