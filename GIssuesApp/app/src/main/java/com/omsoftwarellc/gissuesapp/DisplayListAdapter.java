package com.omsoftwarellc.gissuesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DisplayListAdapter extends BaseAdapter {

    public static final class Item {
        public final String title, description;

        public Item(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    private List<Item> items;

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_item, parent, false);
        }

        Item item = getItem(position);
        TextView titleTV = (TextView) view.findViewById(R.id.rowTVTitle);
        titleTV.setText(item.title);
        TextView descriptionTV = (TextView) view.findViewById(R.id.rowTVDescription);
        descriptionTV.setText(item.description);

        return view;
    }

}
