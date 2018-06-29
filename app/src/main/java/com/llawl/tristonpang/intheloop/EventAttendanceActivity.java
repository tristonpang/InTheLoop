package com.llawl.tristonpang.intheloop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventAttendanceActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private EventAttendanceAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UserData> mUsersDataset;
    private String mEventName;
    private TextView mTotalAttendanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_attendance);
        mEventName = getIntent().getStringExtra("eventName");

        mTotalAttendanceView = findViewById(R.id.totalAttendanceView);
        mRecyclerView = (RecyclerView) findViewById(R.id.attendance_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mUsersDataset = new ArrayList<>();

        // specify an adapter (see also next example)
        mAdapter = new EventAttendanceAdapter(mUsersDataset);
        mRecyclerView.setAdapter(mAdapter);

        //add on click listener
        /*
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
        */

        prepareUsersData();
    }

    private void prepareUsersData() {
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadAttendingUsers(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadAttendingUsers(final DataSnapshot usersDataSnapshot) {
        String eventNameKey = mEventName.replace(" ", "_");

        FirebaseDatabase.getInstance().getReference().child("event_attendance").child(eventNameKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> data = (HashMap) dataSnapshot.getValue();
                for (String userKey : data.keySet()) {
                    HashMap<String, String> userData = (HashMap<String, String>) usersDataSnapshot.child(userKey).getValue();
                    UserData user = new UserData(userData.get("name"), userData.get("rc"),
                            userData.get("contactNum"), userData.get("email"));
                    mUsersDataset.add(user);
                }
                mAdapter.notifyDataSetChanged();
                String totalAttendance = getString(R.string.total_attendance_formatted, Integer.toString(mUsersDataset.size()));
                mTotalAttendanceView.setText(totalAttendance);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
