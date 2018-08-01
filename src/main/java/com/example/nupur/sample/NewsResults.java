package com.example.nupur.sample;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by nupur on 11/21/17.
 */

public class NewsResults
{
    private String title = "";
    private String link = "";
    private String author = "";
    private String pubDate = "";


    public void setTitle(String title1) throws JSONException {
        JSONObject obj=new JSONObject(title1);
        title = obj.getString("0");
    }

    public String getTitle()
    {
        return title;
    }

    public void setLink(String link1) throws JSONException {
        JSONObject obj=new JSONObject(link1);
        link = obj.getString("0");
    }

    public String getLink()
    {
        return link;
    }

    public void setAuthor(String author1) throws JSONException {
        JSONObject obj=new JSONObject(author1);
        author = obj.getString("0");
    }

    public String getAuthor()
    {
        return author;
    }

    public void setDate(String date1) throws JSONException, ParseException {
        JSONObject obj=new JSONObject(date1);
        String date = obj.getString("0");

        //EST to PDT
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        df.setTimeZone(TimeZone.getTimeZone("GMT-05:00"));
        Date timestamp = null;
        timestamp = df.parse(date);
        df.setTimeZone(TimeZone.getTimeZone("GMT-08:00"));
        date = df.format(timestamp);

        Integer s = date.indexOf('-');
        date = date.substring(0, s-1);
        pubDate=date + " PDT";
    }

    public String getDate()
    {
        return pubDate;
    }
}
