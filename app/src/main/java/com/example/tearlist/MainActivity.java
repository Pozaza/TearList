package com.example.tearlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView listItem;
    ArrayList<Item> items = new ArrayList<>();
    ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listItem = findViewById(R.id.listItem);

        listItem.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ItemAdapter(this, items, this);
        listItem.setAdapter(adapter);

        Refresh();
    }

    void Refresh() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        items.clear();

        Cursor c = db.rawQuery("select * from task_db", null);

        int id_col = c.getColumnIndex("id");
        int task_col = c.getColumnIndex("task");

        if (c.moveToFirst())
            do {
                String text = c.getString(task_col);
                Item item = new Gson().fromJson(text, Item.class);
                item.id = c.getInt(id_col);
                items.add(item);
            } while (c.moveToNext());

        c.close();

        dbHelper.close();

        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    public void addTask(View view) {
        Intent intent = new Intent(this, TaskActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Refresh();
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Refresh();
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    public void clearTasks(View view) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            for (final Item item: items) {
                Animation animation =
                        AnimationUtils.loadAnimation(listItem.getChildAt(items.indexOf(item)).getContext(),
                                R.anim.delete_task);
                listItem.getChildAt(items.indexOf(item)).startAnimation(animation);
            }

            view.postDelayed(() -> {
                items.clear();
                runOnUiThread(() -> adapter.notifyDataSetChanged());

                DBHelper dbHelper = new DBHelper(view.getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("task_db", null, null);

                dbHelper.close();
            }, 250);
        };

        new AlertDialog.Builder(view.getContext())
            .setMessage("Вы уверены что хотите удалить все задачи?")
            .setPositiveButton("Да", dialogClickListener)
            .setNegativeButton("Нет", null).show();
    }
}