package com.example.tearlist;

import android.graphics.Color;

public class Item {
    public int id;
    public String title = "", date = "", emoji = "";
    public int backColor;
    public boolean completed = false;

    public Item() { id = -1; }

    public boolean isNull() {
        return id == -1;
    }
}
