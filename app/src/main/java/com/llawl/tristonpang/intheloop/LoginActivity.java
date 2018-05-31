package com.llawl.tristonpang.intheloop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.emailEditText);
        mPasswordView = (EditText) findViewById(R.id.passwordEditText);

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Method executed when Sign in Button is pressed. Attempts to log users in with
     * content provided in Email field and Password field
     */
    public void signIn(View v) {
        attemptLogin();
    }

    /**
     * Method executed when Register Button is pressed. Ends this activity, starts and directs
     * to the RegisterActivity.
     */
    public void register(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    /**
     * Method that encapsulates logic that logs users in with a given Email and Password.
     * Checks for valid email and password first, alerts user if invalid
     * Creates and shows toast message if valid
     * Alerts user if login attempt failed
     * Else logs the user in - ends this activity and starts the EventsListActivity
     */
    private void attemptLogin() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(this, R.string.no_email_pw, Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, R.string.login_in_progress, Toast.LENGTH_SHORT).show();
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("InTheLoop", "attemptLogin() signInWithEmail...() onComplete Success: " + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.d("InTheLoop", "Sign in failed: " + task.getException());
                    showErrorDialog("There was a problem signing in");
                } else {
                    Intent loggedOn = new Intent(LoginActivity.this, EventsListActivity.class);
                    finish();
                    startActivity(loggedOn);
                }
            }
        });

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
