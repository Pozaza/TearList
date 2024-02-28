package com.example.tearlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.zip.Inflater;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
    final LayoutInflater inflater;
    List<Item> items;

    public ItemAdapter(Context context, List<Item> items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        Item item = items.get(position);

        holder.idView.setText(item.id);
        holder.nameView.setText(item.name);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView idView, nameView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idView = itemView.findViewById(R.id.idText);
            nameView = itemView.findViewById(R.id.nameText);
        }
    }
}