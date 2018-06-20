package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class OrganisersActivity extends AppCompatActivity {

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
}
