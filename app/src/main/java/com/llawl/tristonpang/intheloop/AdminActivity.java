package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    public void approveEvents(View v) {
        Intent intent = new Intent(AdminActivity.this, AdminApproveActivity.class);
        startActivity(intent);
    }

    public void delExisting(View v) {
        Intent intent = new Intent(AdminActivity.this, AdminDelActivity.class);
        startActivity(intent);
    }
}
