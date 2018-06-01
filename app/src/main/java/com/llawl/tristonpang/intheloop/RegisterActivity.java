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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private AutoCompleteTextView mNameView;
    private Spinner mRcSpinner;
    private String mRC;
    private EditText mContactNumView;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mEmailView = findViewById(R.id.emailTextView);
        mPasswordView = findViewById(R.id.passwordTextView);
        mConfirmPasswordView = findViewById(R.id.confirmPasswordTextView);
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

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

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
                        //store user info into database
                        addNewUserToDatabase();

                        //switch back to LoginActivity
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


    private boolean isEmailValid() {
        boolean result = mEmailView.getText().toString().contains("@");
        Log.d("InTheLoop", "isEmailValid(): " + result);
        return result;
    }
    private boolean isPasswordValid() {
        String password = mPasswordView.getText().toString();
        boolean result = password.length() >= 6
                && mConfirmPasswordView.getText().toString().equals(password);
        Log.d("InTheLoop", "isPasswordValid(): " + result);
        return result;
    }
    private boolean isRCValid() {
        boolean result = !mRC.equals("Select a Residential College");
        Log.d("InTheLoop", "isRCValid(): " + result);
        return result;
    }
    private boolean isNumValid() {
        boolean result = mContactNumView.getText().toString().length() == 8;
        Log.d("InTheLoop", "isNumValid(): " + result);
        return result;
    }

    private void showErrorDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Oh no!")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void addNewUserToDatabase() {
        String name = mNameView.getText().toString();
        String rc = mRC;
        String contactNum = mContactNumView.getText().toString();
        String email = mEmailView.getText().toString();

        UserData newUser = new UserData(name, rc, contactNum, email);

        //database paths cannot have . so we replace with -
        email = email.replace(".","-");

        //email forms the directory path that store the user data
        mDatabaseReference.child("users").child(email).setValue(newUser);
    }
}
