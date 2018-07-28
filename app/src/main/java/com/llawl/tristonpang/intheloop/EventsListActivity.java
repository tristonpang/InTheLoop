package com.llawl.tristonpang.intheloop;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventsListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private EventCardAdapter mAdapter;
    private List<EventInfo> mEventsDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        BottomNavigationView navView = findViewById(R.id.navigationView);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_myinfo:
                        Log.d("InTheLoop", "Nav from EventsList to MyInfo");
                        goToActivity(MyInfoActivity.class);
                        break;
                    case R.id.navigation_events:
                        break;
                    case R.id.navigation_org:
                        Log.d("InTheLoop", "Nav from EventsList to Organisers");
                        goToActivity(OrganisersActivity.class);
                        break;
                }



                return true;
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.eventsListRecycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mEventsDataset = new ArrayList<>();

        // specify an adapter (see also next example)
        mAdapter = new EventCardAdapter(this, mEventsDataset);
        mRecyclerView.setAdapter(mAdapter);

        //add on click listener
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView,
                new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                EventInfo event = mEventsDataset.get(position);
                String eventName = event.getName();
                Log.d("InTheLoop", "Events List - Event selected:" + eventName);
                goToEventDetails(eventName);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        prepareEventsData();

    }

    private void goToActivity(Class destActivity) {
        Intent intent = new Intent(EventsListActivity.this, destActivity);
        finish();
        overridePendingTransition(0,0);
        startActivity(intent);
    }


    private void prepareEventsData() {
        FirebaseDatabase.getInstance().getReference().child("events_info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String,String> data = (HashMap) snapshot.getValue();

                    //create event and load string values
                    EventInfo event = new EventInfo(data.get("name"), data.get("date"), data.get("time"), data.get("venue"),
                            data.get("desc"), data.get("imageName"), data.get("organiser"));
                    Log.d("InTheLoop", "Adding event to EventsList: " + event.getName());
                    mEventsDataset.add(event);

                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToEventDetails(String eventName) {
        Intent intent = new Intent(EventsListActivity.this, EventDetailsActivity.class);
        intent.putExtra("eventName", eventName);
        startActivity(intent);
    }

    public void goToSearch(View v) {
        Intent intent = new Intent(EventsListActivity.this, SearchQueryActivity.class);
        startActivity(intent);
    }
}
