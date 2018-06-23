package com.llawl.tristonpang.intheloop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class EventDetailsOrgActivity extends AppCompatActivity {
    private String mEventName;
    private DatabaseReference mDatabaseReference;
    private ImageView mEventImage;
    private TextView mEventNameView;
    private TextView mEventDateView;
    private TextView mEventTimeView;
    private TextView mEventVenueView;
    private TextView mEventDescView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_org);

        //link all views
        mEventImage = findViewById(R.id.eventDetailsOrgImg);
        mEventNameView = findViewById(R.id.eventDetailsOrgNameView);
        mEventDateView = findViewById(R.id.eventDetailsOrgDateView);
        mEventTimeView = findViewById(R.id.eventDetailsOrgTimeView);
        mEventVenueView = findViewById(R.id.eventDetailsOrgVenueView);
        mEventDescView = findViewById(R.id.eventDetailsOrgDescView);


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
        StorageReference pathRef = FirebaseStorage.getInstance().getReference().child("images/" + imgName);
        Glide.with(this).using(new FirebaseImageLoader()).load(pathRef).into(mEventImage);
    }
}
