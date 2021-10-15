package com.zybooks.neildohertyoptionone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class InventoryDatabase extends SQLiteOpenHelper {

    private static final String TAG = "Inventory Database";
    //create db version and name
    private static final int VERSION = 16;
    private static final String DATABASE_NAME = "inventory.db";

    private static InventoryDatabase mInvDb;

    public static InventoryDatabase getInstance(Context context) {
        if (mInvDb == null) {
            mInvDb = new InventoryDatabase(context);
            Log.d(TAG, "Instantiated");
        }

        return mInvDb;
    }
    //Create helper
    private InventoryDatabase (Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //Create inventory table
    private static final class InventoryTable {
        private static final String TABLE = "inventory";
        private static final String COL_ITEM = "item";
        private static final String COL_QUANTITY = "quantity";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create inventory table
        db.execSQL("create table " + InventoryTable.TABLE + " (" +
                InventoryTable.COL_ITEM + " text not null," +
                InventoryTable.COL_QUANTITY + " int)");

        Log.d(TAG, "DB Creation Complete");

    }

    //Version upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + InventoryTable.TABLE);
        onCreate(db);
    }

    //get all items
    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String sqlQuery = "select * from " + InventoryTable.TABLE + " order by " +
                            InventoryTable.COL_ITEM + " collate nocase";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        while (cursor.moveToNext()) {
                Item item = new Item();
                item.setItem(cursor.getString(0));
                item.setQuantity(cursor.getInt(1));
                items.add(item);
                Log.d(TAG, "Item added to list");
            }

        cursor.close();

        return items;
    }
    //Add inventory item
    public boolean addItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COL_ITEM, item.getItem());
        values.put(InventoryTable.COL_QUANTITY, item.getQuantity());
        long id = db.insert(InventoryTable.TABLE, null, values);
        return id != -1;
    }
    //Update inventory item
    public void updateItem(Item updatedItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COL_ITEM, updatedItem.getItem());
        values.put(InventoryTable.COL_QUANTITY, updatedItem.getQuantity());
        Log.d(TAG, updatedItem.getItem() + " " + updatedItem.getQuantity());
        db.update(InventoryTable.TABLE, values,
                InventoryTable.COL_ITEM + " = ?", new String[] {updatedItem.getItem()});
    }

    //Delete inventory item
    public void deleteItem(String item) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(InventoryTable.TABLE,
                InventoryTable.COL_ITEM + " = ?", new String[] {item});
    }

    //Check if item exists
    public boolean checkItem(Item item) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sqlQuery = "select * from " + InventoryTable.TABLE +
                " where " + InventoryTable.COL_ITEM + " = ?";

        //Create cursor to check rows
        Cursor cursor = db.rawQuery(sqlQuery, new String[] {item.getItem()});

        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return false;
        }
        else {
            cursor.close();
            db.close();
            return true;
        }
    }

}
