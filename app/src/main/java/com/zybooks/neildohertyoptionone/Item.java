package com.zybooks.neildohertyoptionone;

public class Item {

    private String mItem;
    private int mQuantity;

    public Item() {}

    public Item(String item, int quantity) {
        mItem = item;
        mQuantity = quantity;
    }

    public String getItem() {
        return mItem;
    }

    public void setItem(String item) {
        mItem = item;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }


}
