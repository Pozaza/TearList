package com.example.tearlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnAdd, btnDelete;
    EditText etName;
    RecyclerView listItem;
    DBHelper dbHelper;
    ArrayList<Item> items = new ArrayList<>();
    ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        btnDelete = findViewById(R.id.btnDelete);
        etName = findViewById(R.id.etName);
        listItem = findViewById(R.id.listItem);

        listItem.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ItemAdapter(this, items);
        listItem.setAdapter(adapter);

        btnDelete.setOnClickListener(l -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.delete("mytable", null, null);
            items.clear();
        });

        dbHelper = new DBHelper(this);
    }

    void Refresh() {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            items.clear();

            Cursor c = db.rawQuery("select * from mytable", null);

            c.moveToFirst();

            int idCol = c.getColumnIndex("id");
            int nameCol = c.getColumnIndex("name");

            do {
                int id = c.getInt(idCol);
                String name = c.getString(nameCol);

                items.add(new Item(id, name));
            } while (c.moveToNext());

            c.close();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnAdd_Click(View view) {
        String name = etName.getText().toString();
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        cv.put("name", name);
        db.insert("mytable", null, cv);
        Refresh();
    }
}

