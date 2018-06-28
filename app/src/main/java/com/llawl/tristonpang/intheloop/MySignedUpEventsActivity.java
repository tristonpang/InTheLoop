package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySignedUpEventsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private OrganisedEventsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EventInfo> mEventsDataset;
    private DataSnapshot mEventsInfoSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_signed_up_events);
        mEventsInfoSnapshot = null;

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mEventsDataset = new ArrayList<>();

        // specify an adapter (see also next example)
        mAdapter = new OrganisedEventsAdapter(mEventsDataset);
        mRecyclerView.setAdapter(mAdapter);

        //add on click listener

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView,
                new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                EventInfo event = mEventsDataset.get(position);
                String eventName = event.getName();
                Log.d("InTheLoop", "My Signed-up Events - Event selected: " + eventName);
                goToEvent(eventName);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        prepareEventsData();
    }

    private void prepareEventsData() {
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d("InTheLoop", "prepareEventsData(), currentUser = " + currentUser);
        String currentUserKey = currentUser.replace(".", "-");
        //grab events_info snapshot first
        DatabaseReference eventsInfoRef = FirebaseDatabase.getInstance().getReference().child("events_info");
        eventsInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEventsInfoSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //retrieve signed up list
        FirebaseDatabase.getInstance().getReference().child("user_signups").child(currentUserKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> eventMap = (HashMap) dataSnapshot.getValue();

                for (String eventName : eventMap.keySet()) {
                    String eventNameKey = eventName.replace(" ", "_");
                    HashMap<String, String> eventData = (HashMap<String, String>) mEventsInfoSnapshot.child(eventNameKey).getValue();
                    EventInfo event = new EventInfo(eventData.get("name"), eventData.get("date"), eventData.get("time"), eventData.get("venue"),
                            eventData.get("desc"), eventData.get("imageName"), eventData.get("organiser"));
                    Log.d("InTheLoop", "Adding event: " + event.getName());
                    mEventsDataset.add(event);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void goToEvent(String eventName) {
        Intent intent = new Intent(MySignedUpEventsActivity.this, EventDetailsMyActivity.class);
        intent.putExtra("eventName", eventName);
        startActivity(intent);
    }
}
