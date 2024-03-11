package com.example.tearlist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji2.emojipicker.EmojiPickerView;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.gson.Gson;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.util.Calendar;

public class TaskActivity extends AppCompatActivity {
    Item item;
    EditText titleText;
    EmojiPickerView emojiPickerView;
    ImageView colorView;
    TextView textView;
    MaterialSwitch dateCheck;
    TextView dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Bundle data = getIntent().getExtras();

        item = data != null && data.containsKey("itemData") ?
                new Gson().fromJson(data.get("itemData").toString(), Item.class) :
                new Item();

        if (item.isNull())
            ((Button)findViewById(R.id.saveButton)).setText("Добавить");

        emojiPickerView = findViewById(R.id.emojiPicker);
        textView = findViewById(R.id.emojiText);
        colorView = findViewById(R.id.color_view);
        titleText = findViewById(R.id.titleText);
        dateCheck = findViewById(R.id.dateCheck);
        dateText = findViewById(R.id.dateText);

        emojiPickerView.setVisibility(View.GONE);

        dateCheck.setChecked(!item.date.isBlank());
        dateText.setEnabled(!item.date.isBlank());

        SetData();

        textView.setOnClickListener(l -> emojiPickerView.setVisibility(View.VISIBLE));

        dateCheck.setOnCheckedChangeListener((l, s) -> dateText.setEnabled(l.isChecked()));

        emojiPickerView.setOnEmojiPickedListener(l2 -> {
            textView.setText(l2.getEmoji());
            item.emoji = l2.getEmoji().equals("❌") ? "" : textView.getText().toString();
            emojiPickerView.setVisibility(View.GONE);
        });
    }

    public void SetData() {
        titleText.setText(item.title);
        textView.setText(item.emoji == null || item.emoji.isBlank() ? "❌" : item.emoji);
        dateCheck.setActivated(item.date != null);

        if (item.date != null && !item.date.isBlank())
            dateText.setText(item.date);

        colorView.setBackgroundTintList(ColorStateList.valueOf(item.backColor == 0 || item.backColor == -1 ?
                Color.parseColor(
                "#FFFFFF") :
                item.backColor));
    }

    public void clearEmoji(View view) {
        textView.setText("❌");
        item.emoji = "";
        emojiPickerView.setVisibility(View.GONE);
    }

    public void clearColor(View view) {
        colorView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        item.backColor = 0;
    }

    public void selectColor(View view) {
        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(view.getContext())
            .setTitle("Цвет заметки")
            .setPreferenceName("color_picker_dialog")
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .setPositiveButton("Выбрать", (ColorEnvelopeListener) (envelope, fromUser) -> {
                item.backColor = envelope.getColor();
                colorView.setBackgroundTintList(ColorStateList.valueOf(envelope.getColor()));
            });

        builder.getColorPickerView().setInitialColor(item.backColor == 0 || item.backColor == -1 ? Color.parseColor("#FFFFFF") : item.backColor);

        builder.show();
    }

    @SuppressLint("NewApi")
    public void selectDate(View view) {
        DatePickerDialog dialog = new DatePickerDialog(view.getContext());

        if (!item.date.isBlank())
            dialog.getDatePicker().init(Integer.parseInt(item.date.split("\\.")[2]), Integer.parseInt(item.date.split("\\" +
                    ".")[1]) - 1, Integer.parseInt(item.date.split("\\.")[0]), null);

        dialog.setOnDateSetListener((a, year, month, day) -> {
            item.date = day + "." + (month + 1) + "." + year;
            dateText.setText(item.date);
        });

        dialog.show();
    }

    public void saveTask(View view) {
        if (!Validate())
            return;

        if (((Button) findViewById(R.id.saveButton)).getText().toString().equals("Сохранить"))
            UpdateTask();
        else
            AddTask();

        setResult(0);
        finish();
    }

    public void cancelEdit(View view) {
        setResult(0);
        finish();
    }

    boolean Validate() {
        if (titleText.getText().toString().isBlank()) {
            Toast.makeText(this, "Введите текст задачи!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dateCheck.isChecked() && item.date.isBlank()) {
            Toast.makeText(this, "Выберите дату дедлайна или отключите опцию", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    void UpdateTask() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        item.title = titleText.getText().toString();
        item.date = !dateCheck.isChecked() ? "" : item.date;

        cv.put("id", item.id);
        cv.put("task", new Gson().toJson(item));

        db.update("task_db", cv, "id = " + item.id, null);

        dbHelper.close();
    }

    void AddTask() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        item.title = titleText.getText().toString();
        item.date = !dateCheck.isChecked() ? "" : item.date;

        cv.put("task", new Gson().toJson(item));

        db.insert("task_db", null, cv);

        dbHelper.close();
    }
}