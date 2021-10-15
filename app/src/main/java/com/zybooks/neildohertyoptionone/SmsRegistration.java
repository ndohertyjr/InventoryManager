package com.zybooks.neildohertyoptionone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SmsRegistration extends AppCompatActivity {

    //Set permission code
    private int SMS_PERMISSION_CODE = 1;
    private String TAG = "SMS Registration";

    //Set Extras
    public static String EXTRA_USERNAME;

    ConstraintLayout phoneOptions;

    EditText phoneNumber;
    EditText inventoryQuantity;
    Button saveChanges;
    SwitchCompat toggle;
    UserDatabase mUserDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_registration);

        //Find views
        phoneNumber = findViewById(R.id.phone_number);
        inventoryQuantity = findViewById(R.id.minimum_quantity);
        saveChanges = findViewById(R.id.save);
        phoneOptions = findViewById(R.id.phone_options);
        toggle = findViewById(R.id.sms_toggle);

        //Singleton
        mUserDb = UserDatabase.getInstance(getApplicationContext());

        EXTRA_USERNAME = getIntent().getStringExtra("username");

        //Toggle on text message notifications
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (toggle.isChecked()) {
                    phoneOptions.setVisibility(View.VISIBLE);

                    //Check and request permissions
                    checkPermissions(Manifest.permission.SEND_SMS, SMS_PERMISSION_CODE);
                }
                else {
                    phoneOptions.setVisibility(View.GONE);
                }
            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!phoneNumber.getText().toString().isEmpty())
                    saveChanges.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    //Check toggle's last position
    @Override
    public void onStart(){
        super.onStart();

        toggle.setChecked(getPrefs("toggle", this));

        if (toggle.isChecked()) {
            phoneOptions.setVisibility(View.VISIBLE);

            //Check and request permissions
            checkPermissions(Manifest.permission.SEND_SMS, SMS_PERMISSION_CODE);
        }
        else {
            phoneOptions.setVisibility(View.GONE);
        }
    }
    @Override
    public void onStop() {
        super.onStop();

        setPrefs("toggle", toggle.isChecked(), this);
    }


    //Check for permissions
    public void checkPermissions(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(SmsRegistration.this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SmsRegistration.this,
                    new String[] {permission}, requestCode);
        }
    }

    //Save phone number and quantity to user profile in db
    public void saveSMSClick(View view) {

            String checkPhone = phoneNumber.getText().toString();
            String checkQuantity = inventoryQuantity.getText().toString();

            if (checkPhone.isEmpty() || checkQuantity.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please complete all required fields",
                        Toast.LENGTH_SHORT).show();
            } else {
                //Store changes to user db
                try {
                    Log.d(TAG, "save tried");
                    mUserDb.updateUserPhone(EXTRA_USERNAME, checkPhone);
                    mUserDb.updateMinQuantity(EXTRA_USERNAME, Integer.parseInt(checkQuantity));
                    setResult(RESULT_OK);
                    finish();
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Please check number format!",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "NumberFormatException");

                }
            }
    }

    //preferences to remember toggle position
    public static void setPrefs(String key, Boolean value, Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static Boolean getPrefs(String key, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(key, true);
    }
}