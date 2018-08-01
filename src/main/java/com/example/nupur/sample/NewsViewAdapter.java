package com.example.nupur.sample;

/**
 * Created by nupur on 11/21/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class NewsViewAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private static ArrayList<NewsResults> newsList;

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_view_adapter, null);
            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.title);
            //holder.txtDescription = (TextView) convertView.findViewById(R.id.Description);
            holder.author = (TextView) convertView.findViewById(R.id.author);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(newsList.get(position).getTitle());
        holder.author.setText("Author: " + newsList.get(position).getAuthor());
        holder.date.setText("Date: " + newsList.get(position).getDate());

        return convertView;
    }

    public NewsViewAdapter(Context context, ArrayList<NewsResults> results)
    {
        newsList = results;
        mInflater = LayoutInflater.from(context);
    }

    static class ViewHolder
    {
        TextView title;
        TextView author;
        TextView date;
    }
}
