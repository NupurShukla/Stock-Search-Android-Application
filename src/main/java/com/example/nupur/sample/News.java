package com.example.nupur.sample;

/**
 * Created by nupur on 11/20/17.
 */

import android.graphics.Color;
import android.net.Uri;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class News extends Fragment
{
    private RequestQueue reqQueue;
    private String symbol;
    private ArrayList<NewsResults> newsResults = new ArrayList<NewsResults>();
    private NewsViewAdapter newsViewAdapter;
    private ProgressBar newsProgress;
    private TextView errorMessage3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        symbol = getActivity().getIntent().getStringExtra("symbol");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.news, container, false);
        final ListView newsView = rootView.findViewById(R.id.newsItems);
        newsProgress = rootView.findViewById(R.id.newsProgress);
        errorMessage3=rootView.findViewById(R.id.errorMessage3);

        errorMessage3.setVisibility(View.GONE);
        newsProgress.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);
        newsProgress.setVisibility(View.VISIBLE);
        newsView.setVisibility(View.GONE);

        if(MainActivity.IsInvalidSymbol == true){

            errorMessage3.setVisibility(View.VISIBLE);
            newsProgress.setVisibility(View.GONE);
            newsView.setVisibility(View.GONE);
            return rootView;
        }

        reqQueue = Volley.newRequestQueue(this.getContext());
        String url="http://myapplication-env.us-west-1.elasticbeanstalk.com?newsSymbol="+symbol;

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        NewsResults results;

                        try
                        {
                            for (int i = 0; i < response.length(); i++)
                            {
                                JSONObject item = response.getJSONObject(i);

                                results= new NewsResults();

                                String title =item.getString("Title");
                                String link =item.getString("Link");
                                String author = item.getString("Author");
                                String date = item.getString("Date");



                                results.setTitle(title);
                                results.setLink(link);
                                results.setAuthor(author);
                                results.setDate(date);
                                newsResults.add(results);
                            }
                            newsProgress.setVisibility(View.GONE);
                            newsView.setVisibility(View.VISIBLE);

                            newsViewAdapter=new NewsViewAdapter(getContext(), newsResults);
                            newsView.setAdapter(newsViewAdapter);
                            newsViewAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //TODO
                        Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        reqQueue.add(jsonArrayRequest);

        newsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id)
            {
                NewsResults selectedNews = (NewsResults) (newsView.getItemAtPosition(position));
                Uri newsUri = Uri.parse(selectedNews.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
