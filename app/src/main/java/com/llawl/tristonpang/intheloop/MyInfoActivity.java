package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MyInfoActivity extends AppCompatActivity {

    TextView mNameView;
    TextView mEmailView;
    TextView mRcView;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        BottomNavigationView navView = findViewById(R.id.navigationView);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_myinfo:
                        break;
                    case R.id.navigation_events:
                        Log.d("InTheLoop", "Nav from MyInfo to EventsList");
                        goToActivity(EventsListActivity.class);
                        break;
                    case R.id.navigation_org:
                        Log.d("InTheLoop", "Nav from MyInfo to Organisers");
                        goToActivity(OrganisersActivity.class);
                        break;
                }



                return true;
            }
        });

        mNameView = findViewById(R.id.myNameView);
        mEmailView = findViewById(R.id.myEmailView);
        mRcView = findViewById(R.id.myRcView);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //update views
                updateViews(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateViews(DataSnapshot dataSnapshot) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //retrieve details from snapshot
        String email = user.getEmail();
        String emailKey = email.replace(".","-");

        HashMap<String,String> data = (HashMap) dataSnapshot.child("users").child(emailKey).getValue();
        String name = data.get("name");
        String rc = data.get("rc");
        String contactNum = data.get("contactNum");

        Log.d("InTheLoop", "updateViews() data retrieved from snapshot, Name: " + data.get("name"));

        //update UI
        mEmailView.setText(email);
        mNameView.setText(name);
        mRcView.setText(rc);
    }

    private void goToActivity(Class destActivity) {
        Intent intent = new Intent(MyInfoActivity.this, destActivity);
        finish();
        overridePendingTransition(0,0);
        startActivity(intent);
    }
}
