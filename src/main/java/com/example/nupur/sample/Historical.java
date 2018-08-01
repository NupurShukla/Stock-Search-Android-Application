package com.example.nupur.sample;

/**
 * Created by nupur on 11/20/17.
 */

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.webkit.JavascriptInterface;

public class Historical extends Fragment
{
    WebView HistoricalChart;
    private String symbol;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        symbol = getActivity().getIntent().getStringExtra("symbol");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.historical, container, false);
        HistoricalChart = rootView.findViewById(R.id.historicalChart);

        try
        {
            HistoricalChart.getSettings().setAllowUniversalAccessFromFileURLs(true);
            HistoricalChart.getSettings().setAllowFileAccessFromFileURLs(true);
            HistoricalChart.getSettings().setJavaScriptEnabled(true);
            HistoricalChart.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

            HistoricalChart.loadUrl("file:///android_asset/hischart.html?s=" + symbol);
        }
        catch(Exception ex)
        {
        }

        return rootView;
    }
}