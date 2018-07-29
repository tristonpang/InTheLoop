package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class OrganisersActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private String mUsernameKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisers);

        BottomNavigationView navView = findViewById(R.id.navigationView);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_myinfo:
                        Log.d("InTheLoop", "Nav from Organisers to MyInfo");
                        goToActivity(MyInfoActivity.class);
                        break;
                    case R.id.navigation_events:
                        Log.d("InTheLoop", "Nav from Organisers to EventsList");
                        goToActivity(EventsListActivity.class);
                        break;
                    case R.id.navigation_org:
                        break;
                }



                return true;
            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("admin_info");
        mUsernameKey = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "-");
    }

    private void goToActivity(Class destActivity) {
        Intent intent = new Intent(OrganisersActivity.this, destActivity);
        finish();
        overridePendingTransition(0,0);
        startActivity(intent);
        //overridePendingTransition(0,0);
    }

    /**
     * Goes to CreateEventActivity. Linked to Create new event button.
     */
    public void createNewEvent(View v) {
        Intent intent = new Intent(OrganisersActivity.this, CreateEventActivity.class);
        startActivity(intent);
    }

    /**
     * Goes to MyOrganisedEventsActivity. Linked to My Organised Events button.
     */
    public void seeMyOrgEvents(View v) {
        Intent intent = new Intent(OrganisersActivity.this, MyOrganisedEventsActivity.class);
        startActivity(intent);
    }

    public void adminFunctions(View v) {
        checkIfAdmin();
    }

    private void checkIfAdmin() {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Boolean> data = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (data.get(mUsernameKey) != null) {
                    goToAdmin();
                } else {
                    Toast.makeText(OrganisersActivity.this, "You do not have admin privileges!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void goToAdmin() {
        Intent intent = new Intent(OrganisersActivity.this, AdminActivity.class);
        startActivity(intent);
    }
}
