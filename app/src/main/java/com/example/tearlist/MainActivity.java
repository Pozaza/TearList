package com.example.tearlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText etName;
    RecyclerView listItem;
    DBHelper dbHelper;
    SQLiteDatabase db;
    ArrayList<Item> items = new ArrayList<>();
    ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        listItem = findViewById(R.id.listItem);

        dbHelper = new DBHelper(this);

        listItem.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ItemAdapter(this, items);
        listItem.setAdapter(adapter);

        Refresh();
    }

    void Refresh() {
        db = dbHelper.getReadableDatabase();
        items.clear();

        Cursor c = db.rawQuery("select * from mytable", null);

        int col_id = c.getColumnIndex("id"), col_name = c.getColumnIndex("name");

        if (c.moveToFirst())
            do {
                int id = c.getInt(col_id);
                String name = c.getString(col_name);
                items.add(new Item(id, name));
            } while (c.moveToNext());

        c.close();

        dbHelper.close();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void btnAdd_Click(View view) {
        db = dbHelper.getWritableDatabase();
        String name = etName.getText().toString();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        db.insert("mytable", null, cv);

        dbHelper.close();

        Refresh();
    }

    public void btnDelete_Click(View view) {
        db = dbHelper.getWritableDatabase();
        db.delete("mytable", null, null);
        items.clear();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}