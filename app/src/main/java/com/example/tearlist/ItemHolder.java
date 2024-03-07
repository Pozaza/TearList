package com.example.tearlist;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemHolder extends RecyclerView.ViewHolder {
    public int id;
    public TextView title, date, emoji;
    public TextView editIcon, deleteIcon;

    public ItemHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.task_title);
        date = itemView.findViewById(R.id.task_date);
        emoji = itemView.findViewById(R.id.item_icon);
        editIcon = itemView.findViewById(R.id.edit_icon);
        deleteIcon = itemView.findViewById(R.id.delete_icon);
    }
}