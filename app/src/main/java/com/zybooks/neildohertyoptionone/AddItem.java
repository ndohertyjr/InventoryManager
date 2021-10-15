package com.zybooks.neildohertyoptionone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddItem extends AppCompatActivity {

    private InventoryDatabase mInvDb;
    private Item mItem;


    TextView itemName;
    EditText itemQuantity;
    Button saveItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        mInvDb = InventoryDatabase.getInstance(getApplicationContext());

        itemName = findViewById(R.id.item_name);
        itemQuantity = findViewById(R.id.show_item_quantity);
        saveItem = findViewById(R.id.save_changes);

        //Get request

        itemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String itemField = itemName.getText().toString();
                if (!itemField.isEmpty()) {
                    saveItem.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void saveChangesClick(View view) {
        //gather info from Edit text
        mItem = new Item(itemName.getText().toString().toUpperCase(), Integer.parseInt(
                                                    itemQuantity.getText().toString()));
        //Check if item exists in db
        Boolean checkItem = mInvDb.checkItem(mItem);
        if (checkItem == true) {
            setResult(RESULT_CANCELED);

        } else {
            mInvDb.addItem(mItem);
            Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        }
        finish();
    }
}