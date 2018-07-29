package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminDelActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private AdminApproveAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EventInfo> mEventsDataset;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_del);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events_info");

        mRecyclerView = (RecyclerView) findViewById(R.id.existingEventsList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mEventsDataset = new ArrayList<>();

        // specify an adapter (see also next example)
        mAdapter = new AdminApproveAdapter(mEventsDataset);
        mRecyclerView.setAdapter(mAdapter);

        //add on click listener
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                EventInfo event = mEventsDataset.get(position);
                Log.d("InTheLoop", "Event selected: " + event.getName());
                goToEvent(event.getName());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareEventsData();
    }

    private void prepareEventsData() {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEventsDataset.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> data = (HashMap<String, String>) snapshot.getValue();
                    //check for approved tag (to be applied backend)
                    if (data.get("approved") != null) {
                        EventInfo event = new EventInfo(data.get("name"), data.get("date"), data.get("time"), data.get("venue"),
                                data.get("desc"), data.get("imageName"), data.get("organiser"));
                        Log.d("InTheLoop", "Adding event: " + event.getName());
                        mEventsDataset.add(event);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToEvent(String eventName) {
        Intent intent = new Intent(AdminDelActivity.this, EventDetailsAdminDelActivity.class);
        intent.putExtra("eventName", eventName);
        startActivity(intent);
    }
}
