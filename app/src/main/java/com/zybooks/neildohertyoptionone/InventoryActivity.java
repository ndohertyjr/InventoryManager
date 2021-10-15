package com.zybooks.neildohertyoptionone;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private static final String TAG = "Inventory Activity";
    private static final int REQUEST_CODE_NEW_ITEM = 0;
    private static final int REQUEST_CODE_SMS_REGISTER = 1;

    public static String EXTRA_USERNAME = "";

    private InventoryDatabase mInvDb;
    private UserDatabase mUserDb;

    private ItemAdapter mItemAdapter;
    private RecyclerView mRecyclerView;
    private Boolean editModeActive = false;
    private int mMinQuantity;
    private String mPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //Create username extra
        EXTRA_USERNAME = getIntent().getStringExtra("username");

        //Db Singletons
        mInvDb = InventoryDatabase.getInstance(getApplicationContext());
        mUserDb = UserDatabase.getInstance(getApplicationContext());

        //Initiate RecyclerView
        mRecyclerView = findViewById(R.id.list_view);

        //Create RecyclerView layout
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //Load items into adapter and populate RecyclerView
        mItemAdapter = new ItemAdapter(loadItems());
        mRecyclerView.setAdapter(mItemAdapter);

    }



    //Load db items
    private List<Item> loadItems() {
        Log.d(TAG, "Items loaded");
        return mInvDb.getItems();
    }

    //Floating action button to add item
    public void addInventoryClick(View view) {
        Intent intent = new Intent(this, AddItem.class);
        startActivityForResult(intent, REQUEST_CODE_NEW_ITEM);
    }

    //Implement action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu for the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //Item selection options
        switch (item.getItemId()) {
            case R.id.sms_settings:
                Intent intent = new Intent(InventoryActivity.this, SmsRegistration.class);
                intent.putExtra("username", EXTRA_USERNAME);
                startActivityForResult(intent, REQUEST_CODE_SMS_REGISTER);
                return true;

            case R.id.action_edit:
                // Set button while editing is inactive
                if (editModeActive) {
                    item.setIcon(R.drawable.edit);
                    Toast.makeText(getApplicationContext(), "Edit Mode Off", Toast.LENGTH_SHORT).show();
                    mItemAdapter = new ItemAdapter(loadItems());
                    editModeActive = false;

                }
                // Set button while editing is active
                else {
                    item.setIcon(R.drawable.save);
                    editModeActive = true;
                    Toast.makeText(getApplicationContext(), "Edit Mode On", Toast.LENGTH_SHORT).show();


                }
                mRecyclerView.setAdapter(mItemAdapter);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onResume() {
        super.onResume();
        mItemAdapter = new ItemAdapter(loadItems());
        mRecyclerView.setAdapter(mItemAdapter);
        Log.d(TAG, "onResume: called");
    }
    //ItemAdapter class to adapt recyclerview data to correct layout
    private class ItemAdapter extends RecyclerView.Adapter<InventoryActivity.ItemHolder> {

        private List<Item> mItemList;

        public ItemAdapter(List<Item> items) {
            mItemList = items;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: Called");
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());

            return new ItemHolder(layoutInflater, parent);

        }

        @Override
        public void onBindViewHolder(final ItemHolder holder, final int position) {
            Log.d(TAG, "onBindViewHolder: Called");
            holder.bind(mItemList.get(position), position);

            //Delete current item when button pressed
            holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_LONG).show();
                    String itemToDelete = mItemList.get(position).getItem();
                    mInvDb.deleteItem(itemToDelete);
                    mItemAdapter = new ItemAdapter(loadItems());
                    mRecyclerView.setAdapter(mItemAdapter);
                }
            });

            //Make quantity editable in the list
            if (editModeActive) {
                Log.d(TAG, "onBindViewHolder: if path called");
                holder.bind(mItemList.get(position), position);
                holder.mItemQuantity.setEnabled(true);
                holder.mItemQuantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try{
                            Item updatedItem = new Item(holder.mItemName.getText().toString().toUpperCase(),
                                    Integer.parseInt(holder.mItemQuantity.getText().toString()));
                            saveToDb(updatedItem);
                            Log.d(TAG, updatedItem.getItem() + updatedItem.getQuantity());
                        } catch (NumberFormatException exception) {
                            Toast.makeText(getApplicationContext(), "Quantity cannot be empty",
                                            Toast.LENGTH_SHORT).show();
                        }





                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                    }
                });

            } else {
                Log.d(TAG, "onBindViewHolder: else path called");
                holder.bind(mItemList.get(position), position);

                //Extract phone number and min quantity
                mMinQuantity = mUserDb.getUserMinQuantity(EXTRA_USERNAME);
                mPhoneNumber = mUserDb.getUserPhone(EXTRA_USERNAME);

                //implement minimum quantity text alert
                if (Integer.parseInt(holder.mItemQuantity.getText().toString()) < mMinQuantity) {

                    if (mPhoneNumber == "") {
                        Toast.makeText(getApplicationContext(), "No phone number saved",
                                    Toast.LENGTH_SHORT).show();
                    } else {
                        String message = holder.mItemName.getText().toString() + " is below " +
                                mMinQuantity + " units.";
                        SmsManager textMgr = SmsManager.getDefault();
                        textMgr.sendTextMessage(mPhoneNumber, null, message, null, null);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }


    }


    //Item holder for each item in the recyclerview
    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView mItemName;
        private EditText mItemQuantity;
        private ImageButton mDeleteButton;

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recyclerview_item, parent, false));
            mItemName = itemView.findViewById(R.id.display_name);
            mItemQuantity = itemView.findViewById(R.id.display_quantity);
            mDeleteButton = itemView.findViewById(R.id.delete_item);

        }

        //Bind Item name and quantity
        public void bind(Item item, int position) {

            mItemName.setText(item.getItem());
            mItemQuantity.setText(String.valueOf(item.getQuantity()));
            Log.d(TAG, mItemQuantity.toString());

        }
    }

    //Result of adding an item
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_ITEM) {
            mItemAdapter = new ItemAdapter(loadItems());
            mRecyclerView.setAdapter(mItemAdapter);
        }

        if (resultCode == RESULT_CANCELED && requestCode == REQUEST_CODE_NEW_ITEM) {
            Toast.makeText(getApplicationContext(), "Item already exists." +
                            "To edit an item, tap the edit button.",
                    Toast.LENGTH_LONG).show();
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SMS_REGISTER) {
            Toast.makeText(getApplicationContext(), "Changes saved!", Toast.LENGTH_SHORT).show();
            mRecyclerView.setAdapter(mItemAdapter);

        }

        if (resultCode == RESULT_CANCELED && requestCode == REQUEST_CODE_SMS_REGISTER) {
            Toast.makeText(getApplicationContext(), "Changes not saved.",
                    Toast.LENGTH_LONG).show();
        }

    }

    //Save quantity update to database
    public void saveToDb(Item updatedItem) {
        mInvDb.updateItem(updatedItem);
    }
}
