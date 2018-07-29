package com.llawl.tristonpang.intheloop;

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

public class EventDetailsAdminActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_event_details_admin);

        //retrieve event name
        mEventName = getIntent().getStringExtra("eventName");

        //link all views
        mEventImage = findViewById(R.id.eventDetailsAdminImg);
        mEventNameView = findViewById(R.id.eventDetailsAdminNameView);
        mEventDateView = findViewById(R.id.eventDetailsAdminDateView);
        mEventTimeView = findViewById(R.id.eventDetailsAdminTimeView);
        mEventVenueView = findViewById(R.id.eventDetailsAdminVenueView);
        mEventDescView = findViewById(R.id.eventDetailsAdminDescView);

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





    public void approve(View v) {
        approveEvent();
    }

    public void remove(View v) {
        removeEvent();
    }

    private void approveEvent() {
        //update details to have all same values + approved status
        HashMap<String, String> newValues = new HashMap<>();
        newValues.put("date", mEventDate);
        newValues.put("time", mEventTime);
        newValues.put("venue", mEventVenue);
        newValues.put("desc", mEventDescView.getText().toString());
        newValues.put("imageName", mImageName);
        newValues.put("name", mEventName);
        newValues.put("organiser", mOrganiser);
        //approved tag
        newValues.put("approved", "y");

        HashMap<String, Object> updates = new HashMap<>();
        String eventNameKey = mEventName.replace(" ", "_");
        updates.put(eventNameKey, newValues);

        mDatabaseReference.updateChildren(updates);

        finish();
    }

    private void removeEvent() {
        String eventNameKey = mEventName.replace(" ", "_");
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
}
