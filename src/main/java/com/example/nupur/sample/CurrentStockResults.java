package com.example.nupur.sample;

/**
 * Created by nupur on 11/21/17.
 */

public class CurrentStockResults
{
    private String stockSymbol = "";
    private String lastPrice = "";
    private String change = "";
    private String timestamp = "";
    private String open = "";
    private String close = "";
    private String dayRange = "";
    private String volume = "";
    private String heading ="";
    private String data;

    public void setData(String data1)
    {
        data = data1;
    }

    public String getData()
    {
        return data;
    }

    public void setHeading(String heading1)
    {
        heading = heading1;
    }

    public String getHeading() {

        return heading;
    }

    public void setStockSymbol(String stockSymbol1)
    {
        stockSymbol = stockSymbol1;
    }

    public String getStockSymbol()
    {
        return stockSymbol;
    }

    public void setLastPrice(String lastPrice1) {
        lastPrice = lastPrice1;
    }

    public String getLastPrice()
    {
        return lastPrice;
    }

    public void setChange(String change1)
    {
        change = change1;
    }

    public String getChange()
    {
        return change;
    }

    public void setTimestamp(String timestamp1)
    {
        if(timestamp1.indexOf(':') > 0)
        {
            timestamp = timestamp1 + " EDT";
        }

        else
        {
            timestamp = timestamp1 + " 16:00:00 EDT";
        }
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setOpen(String open1)
    {
        open = open1;
    }

    public String getOpen()
    {
        return open;
    }

    public void setClose(String close1)
    {
        close = close1;
    }

    public String getClose()
    {
        return close;
    }

    public void setDayRange(String dayRange1)
    {
        dayRange = dayRange1;
    }

    public String getDayRange()
    {
        return dayRange;
    }

    public void setVolume(String volume1)
    {
        volume = volume1;
    }

    public String getVolume()
    {
        return volume;
    }
}
