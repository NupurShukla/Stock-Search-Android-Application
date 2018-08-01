package com.example.nupur.sample;

/**
 * Created by nupur on 11/20/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Current extends Fragment
{
    private ImageView favoriteButton, facebookShare;
    private WebView IndicatorChart;
    private Spinner indicator;
    private ProgressBar curStockViewProgress;
    private TextView change, errorMessage, errorMessage2;

    private RequestQueue reqQueue;
    ArrayList<CurrentStockResults> curStockResults;
    private SharedPreferences allFavorites;
    private CurrentStockViewAdapter currentStockViewAdapter;
    private static String lastPrice, changeVal;
    public static String symbol, close, prevClose, timestamp, open, low, high, vol;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private String currentChart;
    public static String url;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.current, container, false);
        symbol=getActivity().getIntent().getStringExtra("symbol");

        final ListView currentStockView = rootView.findViewById(R.id.currentStockView);
        allFavorites = getContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        favoriteButton = rootView.findViewById(R.id.favorite);
        facebookShare = rootView.findViewById(R.id.facebook);
        IndicatorChart = rootView.findViewById(R.id.indicatorChart);
        indicator = rootView.findViewById(R.id.indicatorVal);
        curStockViewProgress = rootView.findViewById(R.id.currentStockViewProgress);
        change = rootView.findViewById(R.id.change);
        errorMessage=rootView.findViewById(R.id.errorMessage);
        errorMessage2=rootView.findViewById(R.id.errorMessage2);

        errorMessage.setVisibility(View.GONE);
        errorMessage2.setVisibility(View.GONE);
        curStockViewProgress.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);
        curStockViewProgress.setVisibility(View.VISIBLE);
        currentStockView.setVisibility(View.GONE);

        if (isSymbolFavorite(symbol) == true)
        {
            favoriteButton.setImageResource(R.drawable.filled);
        }
        else
        {
            favoriteButton.setImageResource(R.drawable.empty);
        }

        FacebookSdk.sdkInitialize(this.getContext());

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, shareCallBack);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(currentStockView.getVisibility() == View.GONE)
                {

                }
                else if (isSymbolFavorite(symbol) == true)
                {
                    // Remove from favorite
                    allFavorites.edit().remove(symbol).commit();
                    favoriteButton.setImageResource(R.drawable.empty);
                }
                else
                {
                    // Add to favorite
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    String favInfo=lastPrice +"," + changeVal + "," + date;
                    allFavorites.edit().putString(symbol, favInfo).commit();
                    favoriteButton.setImageResource(R.drawable.filled);
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                facebookShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse(url))
                                    .build();

                            shareDialog.show(linkContent);
                        }
                    }
                });
            }
        }, 8000);

        currentChart="price";
        change.setClickable(false);
        change.setTextColor(Color.GRAY);

        indicator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = String.valueOf(indicator.getSelectedItem());

                if (selectedItem.equalsIgnoreCase(currentChart)) {
                    change.setClickable(false);
                    change.setTextColor(Color.GRAY);
                }
                else {
                    change.setClickable(true);
                    change.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(MainActivity.IsInvalidSymbol == true){

            errorMessage.setVisibility(View.VISIBLE);
            errorMessage2.setVisibility(View.VISIBLE);
            curStockViewProgress.setVisibility(View.GONE);
            currentStockView.setVisibility(View.GONE);
            IndicatorChart.setVisibility(View.GONE);
            return rootView;
        }

        reqQueue = Volley.newRequestQueue(this.getContext());
        String url="http://myapplication-env.us-west-1.elasticbeanstalk.com?symbolVal="+symbol;

        JsonObjectRequest jsonArrayRequest=new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            close = response.getString("Close");
                            prevClose = response.getString("Previous Close");
                            timestamp = response.getString("Timestamp");
                            open = response.getString("Open");
                            low = response.getString("Low");
                            high = response.getString("High");
                            vol = response.getString("Volume");
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                        curStockViewProgress.setVisibility(View.GONE);
                        currentStockView.setVisibility(View.VISIBLE);

                        curStockResults = CurrentStockResults();
                        currentStockViewAdapter=new CurrentStockViewAdapter(getContext(), curStockResults);
                        currentStockView.setAdapter(currentStockViewAdapter);
                        currentStockViewAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener()
                {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        // TODO Auto-generated method stub
                    }
                }
        );

        reqQueue.add(jsonArrayRequest);

        IndicatorChart.getSettings().setAllowUniversalAccessFromFileURLs(true);
        IndicatorChart.getSettings().setAllowFileAccessFromFileURLs(true);
        IndicatorChart.getSettings().setJavaScriptEnabled(true);
        IndicatorChart.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        IndicatorChart.getSettings().setDomStorageEnabled(true);

        IndicatorChart.addJavascriptInterface(new WebAppInterface(getContext()), "Android");

        DrawPriceChart();
        IndicatorCharts();
        return rootView;
    }

    private void IndicatorCharts()
    {
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String spinnerVal=String.valueOf(indicator.getSelectedItem());
                if(!spinnerVal.equalsIgnoreCase(currentChart))
                {
                    if(spinnerVal.equalsIgnoreCase("Price"))
                    {
                        DrawPriceChart();
                    }
                    else
                    {
                        currentChart=spinnerVal.toLowerCase();
                        try
                        {
                            IndicatorChart.loadUrl("file:///android_asset/" + currentChart + ".html?s=" + symbol);
                            change.setClickable(false);
                            change.setTextColor(Color.GRAY);
                        }
                        catch(Exception ex)
                        {
                        }
                    }
                }
            }
        });
    }

    private void DrawPriceChart()
    {
        currentChart="price";
        try
        {
            IndicatorChart.loadUrl("file:///android_asset/pricechart.html?s=" + symbol);
            change.setClickable(false);
            change.setTextColor(Color.GRAY);
        }
        catch(Exception ex)
        {
        }
    }

    public FacebookCallback<Sharer.Result> shareCallBack = new FacebookCallback<Sharer.Result>() {

        @Override
        public void onSuccess(Sharer.Result result) {
            Toast.makeText(getContext(), "Facebook post successful", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancel() {
            Toast.makeText(getContext(), "Post cancelled", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onError(FacebookException error) {
            Toast.makeText(getContext(), "Failed to post", Toast.LENGTH_SHORT).show();
        }
    };

    private boolean isSymbolFavorite(String symbolVal)
    {
        Map<String, ?> allEntries = allFavorites.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet())
        {
            if (entry.getKey().toString().equalsIgnoreCase(symbolVal))
            {
                return true;
            }
        }
        return  false;
    }

    private ArrayList<CurrentStockResults> CurrentStockResults()
    {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        ArrayList<CurrentStockResults> results = new ArrayList<CurrentStockResults>();

        CurrentStockResults resultsObj = new CurrentStockResults();
        resultsObj.setHeading("Stock Symbol");
        resultsObj.setStockSymbol(symbol);
        results.add(resultsObj);

        resultsObj = new CurrentStockResults();
        resultsObj.setHeading("Last Price");
        double closeDouble=Double.parseDouble(close);
        lastPrice=decimalFormat.format(closeDouble);
        resultsObj.setLastPrice(lastPrice);
        results.add(resultsObj);

        resultsObj = new CurrentStockResults();
        resultsObj.setHeading("Change");
        double prevCloseDouble=Double.parseDouble(prevClose);
        double change = closeDouble-prevCloseDouble;
        double changePercentage=(change/prevCloseDouble)*100;
        changeVal=decimalFormat.format(change) + " ("+ decimalFormat.format(changePercentage) + "%) ";
        resultsObj.setChange(changeVal);
        results.add(resultsObj);

        resultsObj = new CurrentStockResults();
        resultsObj.setHeading("Timestamp");
        resultsObj.setTimestamp(timestamp);
        results.add(resultsObj);

        resultsObj = new CurrentStockResults();
        resultsObj.setHeading("Open");
        double openDouble=Double.parseDouble(open);
        resultsObj.setOpen(decimalFormat.format(openDouble));
        results.add(resultsObj);

        resultsObj = new CurrentStockResults();
        resultsObj.setHeading("Close");
        if(timestamp.indexOf(':') > 0)
        {
            resultsObj.setClose(decimalFormat.format(prevCloseDouble));
        }
        else
        {
            resultsObj.setClose(decimalFormat.format(closeDouble));
        }

        resultsObj.setClose(decimalFormat.format(prevCloseDouble));
        results.add(resultsObj);

        resultsObj = new CurrentStockResults();
        resultsObj.setHeading("Day's Range");
        double lowDouble=Double.parseDouble(low);
        double highDouble=Double.parseDouble(high);
        String dayRange=decimalFormat.format(lowDouble) + " - "+ decimalFormat.format(highDouble);
        resultsObj.setDayRange(dayRange);
        results.add(resultsObj);

        resultsObj = new CurrentStockResults();
        resultsObj.setHeading("Volume");
        resultsObj.setVolume(vol);
        results.add(resultsObj);

        return results;
    }
}

class WebAppInterface {
    Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void ImageUrl(String imageUrl) {
        Current.url = imageUrl;
    }
}
