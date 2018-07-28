package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SearchQueryActivity extends AppCompatActivity {

    private TextView mSearchQueryView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_query);

        mSearchQueryView = findViewById(R.id.searchQueryTextView);
    }

    public void goSearch(View v) {
        Log.d("InTheLoop", "Go Button pressed");
        search();
    }

    private void search() {
        String query = mSearchQueryView.getText().toString().toLowerCase();
        goToResults(query);
    }

    private void goToResults(String query) {
        Intent intent = new Intent(SearchQueryActivity.this, SearchResultsActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }
}
