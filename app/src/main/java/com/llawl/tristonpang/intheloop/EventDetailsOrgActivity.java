package com.llawl.tristonpang.intheloop;

import android.content.DialogInterface;
import android.content.Intent;
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

public class EventDetailsOrgActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_org);

        mStorageReference = FirebaseStorage.getInstance().getReference();

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
        mImageName = imgName;
        StorageReference pathRef = mStorageReference.child("images/" + imgName);
        Glide.with(this).using(new FirebaseImageLoader()).load(pathRef).into(mEventImage);
    }

    public void delete(View v) {
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
        //find and delete image
        StorageReference pathRef = mStorageReference.child("images/" + mImageName);
        pathRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("InTheLoop", "Event image: " + mImageName + " successfully deleted");
            }
        });

        //find and delete event details
        String eventNameKey = mEventName.replace(" ", "_");
        mDatabaseReference.child(eventNameKey).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d("InTheLoop", "Event " + mEventName + " successfully deleted");
            }
        });

        finish();
    }



    public void edit(View v) {
        beginEdit();
    }

    private void beginEdit() {
        Intent intent = new Intent(EventDetailsOrgActivity.this, EditEventActivity.class);
        intent.putExtra("eventName", mEventName);
        startActivity(intent);
    }
}
