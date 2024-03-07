package com.example.tearlist;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder>{
    LayoutInflater inflater;
    List<Item> items;
    Activity activity;

    public ItemAdapter(Context context, List<Item> items, Activity activity) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.activity = activity;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Item item = items.get(position);

        holder.id = item.id;
        holder.title.setText(item.title);

        if (item.date.isBlank())
            holder.date.setVisibility(View.GONE);
        else {
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(item.date);

            int year = Integer.parseInt(item.date.split("\\.")[2]);
            int month = Integer.parseInt(item.date.split("\\.")[1]);
            int day = Integer.parseInt(item.date.split("\\.")[0]);

            holder.date.setTextColor(Color.parseColor(LocalDate.now().isAfter(LocalDate.of(year, month, day)) ?
                    "#FF6666" : "#FFFFFF"));
        }

        holder.emoji.setText(item.emoji);
        holder.itemView.setBackgroundTintList(ColorStateList.valueOf(item.backColor == 0 ? Color.parseColor("#262133") :
                item.backColor));
        holder.itemView.setAlpha(item.completed ? 0.3f : 1f);

        if (item.completed)
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.itemView.setOnClickListener(l -> {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                    R.anim.complete_task);
            Animator animator = AnimatorInflater.loadAnimator(holder.itemView.getContext(),
                    R.animator.complete_task_rotation);

            holder.itemView.startAnimation(animation);
            animator.setTarget(holder.itemView);
            animator.start();

            holder.itemView.postDelayed(() -> {
                item.completed = !item.completed;
                holder.itemView.setAlpha(item.completed ? 0.3f : 1f);
                holder.title.setPaintFlags(item.completed ?
                        (holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG) : Paint.CURSOR_AFTER);
                UpdateTask(item);
            }, 175);
        });

        holder.editIcon.setOnClickListener(l -> {
            Intent intent = new Intent(activity, TaskActivity.class).putExtra("itemData",
                    new Gson().toJson(item));
            activity.startActivityForResult(intent, 0);
        });

        holder.deleteIcon.setOnClickListener(l -> {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                    R.anim.delete_task);
            holder.itemView.startAnimation(animation);

            holder.itemView.postDelayed(() -> {
                items.remove(item);
                notifyDataSetChanged();
            }, 250);

            DeleteTask(item);
        });
    }

    void UpdateTask(Item item) {
        DBHelper dbHelper = new DBHelper(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("task", new Gson().toJson(item));

        db.update("task_db", cv, "id = " + item.id, null);

        dbHelper.close();
    }

    void DeleteTask(Item item) {
        DBHelper dbHelper = new DBHelper(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("task_db","id = " + item.id, null);

        dbHelper.close();
    }
}