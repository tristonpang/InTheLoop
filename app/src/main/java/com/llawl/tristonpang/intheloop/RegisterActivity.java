package com.llawl.tristonpang.intheloop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private AutoCompleteTextView mNameView;
    private Spinner mRcSpinner;
    private EditText mContactNumView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mEmailView = findViewById(R.id.emailTextView);
        mPasswordView = findViewById(R.id.passwordTextView);
        mNameView = findViewById(R.id.nameTextView);
        mRcSpinner = findViewById(R.id.rcSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rc_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mRcSpinner.setAdapter(adapter);


        mContactNumView = findViewById(R.id.contactNumView);

    }

    public void registerNewUser(View v) {
        attemptRegistration();
    }

    private void attemptRegistration() {
        if (isEmailValid() && isPasswordValid())
    }
}
