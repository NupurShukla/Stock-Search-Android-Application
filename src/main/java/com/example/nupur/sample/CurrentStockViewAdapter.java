package com.example.nupur.sample;

/**
 * Created by nupur on 11/20/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CurrentStockViewAdapter extends BaseAdapter {

    private static ArrayList<CurrentStockResults> curStockResults;
    private LayoutInflater mInflater;

    @Override
    public int getCount() {
        return curStockResults.size();
    }

    @Override
    public Object getItem(int position) {
        return curStockResults.get(position);
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
            convertView = mInflater.inflate(R.layout.current_stock_view_adapter, null);
            holder = new ViewHolder();
            holder.heading = convertView.findViewById(R.id.heading);
            holder.value = convertView.findViewById(R.id.value);
            holder.indicator=convertView.findViewById(R.id.indicator);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        try
        {
            String heading = curStockResults.get(position).getHeading();
            holder.heading.setText(heading);
            if(heading=="Stock Symbol")
                holder.value.setText(curStockResults.get(position).getStockSymbol());
            else if(heading=="Last Price")
                holder.value.setText(curStockResults.get(position).getLastPrice());
            else if(heading=="Change")
            {
                String change=curStockResults.get(position).getChange();
                holder.value.setText(change);
                if (change.contains("-"))
                {
                    holder.indicator.setImageResource(R.drawable.down);
                }
                else
                {
                    holder.indicator.setImageResource(R.drawable.up);
                }
            }
            else if(heading=="Timestamp")
                holder.value.setText(curStockResults.get(position).getTimestamp());
            else if(heading=="Open")
                holder.value.setText(curStockResults.get(position).getOpen());
            else if(heading=="Close")
                holder.value.setText(curStockResults.get(position).getClose());
            else if(heading=="Day's Range")
                holder.value.setText(curStockResults.get(position).getDayRange());
            else if(heading=="Volume")
                holder.value.setText(curStockResults.get(position).getVolume());

            //holder.imgValue.setImageDrawable((searchArrayList.get(position).getFlag()));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


        return convertView;
    }

    static class ViewHolder {
        TextView heading;
        TextView value;
        ImageView indicator;
    }

    public CurrentStockViewAdapter(Context context, ArrayList<CurrentStockResults> curStockResults1) {
        curStockResults = curStockResults1;
        mInflater = LayoutInflater.from(context);
    }
}
