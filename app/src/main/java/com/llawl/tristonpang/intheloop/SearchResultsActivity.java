package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private String mQuery;
    private RecyclerView mRecyclerView;
    private SearchResultsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<EventInfo> mResultsDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //set up database reference to events_info
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("events_info");

        //retrieve query
        mQuery = getIntent().getStringExtra("query");

        //set up RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.searchResultsRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mResultsDataset = new ArrayList<>();

        // specify an adapter (see also next example)
        mAdapter = new SearchResultsAdapter(mResultsDataset);
        mRecyclerView.setAdapter(mAdapter);

        //add on click listener
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                EventInfo event = mResultsDataset.get(position);
                Log.d("InTheLoop", "Event selected: " + event.getName());
                goToEventDetails(event.getName());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareResultsData();
    }

    private void prepareResultsData() {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String,String> data = (HashMap) snapshot.getValue();
                    if (data.get("name").toLowerCase().contains(mQuery)) {
                        EventInfo event = new EventInfo(data.get("name"), data.get("date"), data.get("time"), data.get("venue"),
                                data.get("desc"), data.get("imageName"), data.get("organiser"));
                        mResultsDataset.add(event);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToEventDetails(String eventName) {
        Intent intent = new Intent(SearchResultsActivity.this, EventDetailsActivity.class);
        intent.putExtra("eventName", eventName);
        startActivity(intent);
    }

}
