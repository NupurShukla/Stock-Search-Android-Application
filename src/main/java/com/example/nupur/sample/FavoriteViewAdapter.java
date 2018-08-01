package com.example.nupur.sample;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nupur on 11/22/17.
 */

public class FavoriteViewAdapter extends BaseAdapter
{
    static class ViewHolder
    {
        TextView symbol;
        TextView lastPrice;
        TextView change;
    }

    private ArrayList<String> favouritesAsStrings;
    private LayoutInflater inflater;

    public FavoriteViewAdapter(Context context, ArrayList<String> favouritesAsStrings1)
    {
        inflater = LayoutInflater.from(context);
        favouritesAsStrings=favouritesAsStrings1;
    }
    @Override
    public int getCount()
    {
        return favouritesAsStrings.size();
    }

    @Override
    public Object getItem(int position) {
        return favouritesAsStrings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.favorite_view_adapter, null);
            holder = new ViewHolder();
            holder.symbol = convertView.findViewById(R.id.symbol);
            holder.lastPrice = convertView.findViewById(R.id.lastprice);
            holder.change = convertView.findViewById(R.id.change);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        String favInfo[]= favouritesAsStrings.get(position).split(",");
        holder.symbol.setText(favInfo[0]);
        holder.lastPrice.setText(favInfo[1]);
        holder.change.setText(favInfo[2]);
        if(holder.change.getText().toString().contains("-"))
        {
            holder.change.setTextColor(Color.rgb(255, 0, 0));
        }
        else
        {
            holder.change.setTextColor(Color.rgb(0,255,0));
        }

        return convertView;
    }
}
