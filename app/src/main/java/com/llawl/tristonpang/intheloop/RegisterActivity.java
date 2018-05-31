package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private AutoCompleteTextView mNameView;
    private Spinner mRcSpinner;
    private String mRC;
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
        //creating adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rc_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mRcSpinner.setAdapter(adapter);

        mRcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                Log.d("InTheLoop", "RC Spinner - Selection: " + selection);
                mRC = selection;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mContactNumView = findViewById(R.id.contactNumView);

    }

    public void registerNewUser(View v) {
        attemptRegistration();
    }

    private void attemptRegistration() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (isEmailValid() && isPasswordValid() && isRCValid() && isNumValid()) { //begin registration attempt
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("InTheLoop", "attemptRegistration() onComplete Success: + " + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        //error dialog
                        showErrorDialog("Registration failed");
                    } else {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
            });
        } else { //toast to notify user to fill fields properly
            Toast.makeText(this, R.string.validate_text, Toast.LENGTH_SHORT).show();
        }
    }

    //TODO: Validity checking methods, replace with proper logic
    private boolean isEmailValid() {
        return true;
    }
    private boolean isPasswordValid() {
        return true;
    }
    private boolean isRCValid() {
        return mRC.equals("Select a Residential College");
    }
    private boolean isNumValid() {
        return true;
    }

    private void showErrorDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Oh no!")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
