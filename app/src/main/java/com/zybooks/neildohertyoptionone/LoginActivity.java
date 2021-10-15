package com.zybooks.neildohertyoptionone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private UserDatabase mUserDb;
    private User mUser;
    public static String EXTRA_USERNAME = "";

    EditText username;
    EditText password;
    Button createUser;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        createUser = findViewById(R.id.newUserButton);
        login = findViewById(R.id.loginButton);

        mUserDb = UserDatabase.getInstance(getApplicationContext());

        //listen for input to enable login
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String loginField = username.getText().toString();
                String passwordField = password.getText().toString();
                if (!loginField.isEmpty() && !passwordField.isEmpty()) {
                    login.setEnabled(true);
                    createUser.setEnabled(true);
                }
                else {
                    login.setEnabled(false);
                    createUser.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    //Existing user login
    public void onLoginClicked(View view) {
        Boolean validLogin = mUserDb.validateUser(username.getText().toString().toLowerCase(),
                                            password.getText().toString());


        if (validLogin == true) {
            EXTRA_USERNAME = mUserDb.getUser(username.getText().toString().toLowerCase(),
                    password.getText().toString());
            Intent intent = new Intent(view.getContext(),InventoryActivity.class);
            intent.putExtra("username", EXTRA_USERNAME);
            startActivity(intent);
            Log.d(TAG, "onLogin called");
        }
        //FIXME: Toast not appearing
        else {
            Toast.makeText(getApplicationContext(), "Invalid login", Toast.LENGTH_SHORT).show();
            Log.d("Toast", "Took this path");
        }
    }

    //New user login
    public void onNewUserClicked(View view) {
        //Add use to db
        mUser = new User(username.getText().toString().toLowerCase(), password.getText().toString());
        mUserDb.addUser(mUser);

        //pass username as extra
        EXTRA_USERNAME = mUserDb.getUser(username.getText().toString().toLowerCase(),
                password.getText().toString());

        Toast.makeText(getApplicationContext(), "User added", Toast.LENGTH_LONG).show();

        Log.d(TAG, "onNewUser called");

        Intent intent = new Intent(view.getContext(),InventoryActivity.class);
        intent.putExtra("username", EXTRA_USERNAME);
        startActivity(intent);
    }

}