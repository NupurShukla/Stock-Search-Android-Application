package com.example.nupur.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    public String data;
    public List<String> suggestions;
    public ArrayAdapter<String> arrayAdapter;
    private FavoriteViewAdapter favViewAdapter;
    public AutoCompleteTextView autoCompleteText;
    private TextView getQuote, clear, errorMessage;
    private SharedPreferences allFavorites;
    private Spinner sortSpinner, orderSpinner;
    Activity context = this;
    private RequestQueue reqQueue;
    private ArrayList<String> currentOrdering = new ArrayList<>();
    private ListView favView;
    public static boolean IsInvalidSymbol=false;
    private ImageView manualrefresh;
    private ProgressBar favProgress;
    private Switch autorefresh;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        suggestions = new ArrayList<>();
        autoCompleteText= findViewById(R.id.autoCompleteTextView1);
        getQuote = findViewById(R.id.getQuote);
        clear = findViewById(R.id.clear);
        sortSpinner=findViewById(R.id.sortby);
        orderSpinner=findViewById(R.id.order);
        manualrefresh=findViewById(R.id.manualrefresh);
        autorefresh=findViewById(R.id.autorefresh);
        favProgress = findViewById(R.id.favProgress);

        favProgress.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);
        favProgress.setVisibility(View.GONE);

        autoCompleteText.addTextChangedListener(new TextWatcher()
        {

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String newText = s.toString();

                reqQueue = Volley.newRequestQueue(context);
                String url="http://myapplication-env.us-west-1.elasticbeanstalk.com?inputVal="+newText;

                JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>()
                        {
                            @Override
                            public void onResponse(JSONArray response)
                            {
                                try
                                {
                                    // Restricting auto-complete suggestions to 5
                                    int length=response.length();
                                    if(length==0)
                                    {
                                        IsInvalidSymbol=true;
                                        Toast.makeText(context, "Please enter a valid symbol", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    IsInvalidSymbol=false;
                                    if(length>5)
                                    {
                                        length=5;
                                    }

                                    for(int i=0;i<length;i++)
                                    {
                                        JSONObject jsonobject = response.getJSONObject(i);
                                        suggestions.add(i, jsonobject.getString("Symbol")+"\n"+jsonobject.getString("Name")+" ("+jsonobject.getString("Exchange")+")") ;
                                    }
                                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.suggestion, suggestions);
                                    autoCompleteText.setAdapter(arrayAdapter);
                                    arrayAdapter.notifyDataSetChanged();
                                }
                                catch (JSONException e)
                                {
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
                            }
                        });
                reqQueue.add(jsonArrayRequest);

            }

            public void afterTextChanged(Editable editable)
            {
                // TODO Auto-generated method stub
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub

            }
        });

        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String)parent.getItemAtPosition(position);
                autoCompleteText.setText(selection);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteText.setText("");
            }
        });

        getQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = autoCompleteText.getText().toString();
                if(input.trim().length() == 0)
                {
                    Toast.makeText(context, "Please enter a stock name or symbol", Toast.LENGTH_LONG).show();
                }
                else
                {
                    String symbolVal  = input.split("\n")[0];
                    Intent intent = new Intent(MainActivity.this, StockDetailsActivity.class);
                    intent.putExtra("symbol", symbolVal);
                    startActivity(intent);
                }
            }
        });

        final ArrayList<String> favouritesAsStrings = new ArrayList<>();
        allFavorites = getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = allFavorites.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet())
        {
            favouritesAsStrings.add(entry.getKey().toString() + ", " + entry.getValue().toString());
        }

        favView = findViewById(R.id.favListView);
        favView.setAdapter(new FavoriteViewAdapter(this, favouritesAsStrings));
        currentOrdering=favouritesAsStrings;
        registerForContextMenu(favView);

        favView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                String favInfo[]= currentOrdering.get(position).split(",");
                String symbolVal = favInfo[0];
                Intent intent = new Intent(MainActivity.this, StockDetailsActivity.class);
                intent.putExtra("symbol", symbolVal);
                startActivity(intent);
            }
        });

        ManualRefresh(favView);
        AutoRefresh(favView);

        SortFavorites(favView, favouritesAsStrings);
        OrderFavorites(favView, favouritesAsStrings);
    }

    private void AutoRefresh(final ListView favView) {
        autorefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (buttonView.isChecked())
                {
                    favView.setVisibility(View.GONE);
                    favProgress.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            favProgress.setVisibility(View.GONE);
                            favView.setVisibility(View.VISIBLE);
                        }
                    }, 2000);
                }
            }
        });

    }

    private void ManualRefresh(final ListView favView)
    {
        manualrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favView.setVisibility(View.GONE);
                favProgress.setVisibility(View.VISIBLE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        favProgress.setVisibility(View.GONE);
                        favView.setVisibility(View.VISIBLE);
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.favListView)
        {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.favorite_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.no:
                Toast.makeText(context, "Selected No", Toast.LENGTH_LONG).show();
                return true;
            case R.id.yes:
                String favInfo[]= currentOrdering.get(info.position).split(",");
                String symbolVal = favInfo[0];
                allFavorites.edit().remove(symbolVal).commit();

                currentOrdering.remove(info.position);

                favViewAdapter=new FavoriteViewAdapter(this, currentOrdering);
                favView.setAdapter(favViewAdapter);
                favViewAdapter.notifyDataSetChanged();

                Toast.makeText(context, "Selected Yes", Toast.LENGTH_LONG).show();
                return true;
            case R.id.question:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void SortFavorites(final ListView favView, final ArrayList<String> favouritesAsStrings)
    {
        sortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String order=String.valueOf(orderSpinner.getSelectedItem());

                switch (position)
                {
                    case 0:
                    {
                        orderSpinner.setEnabled(false);
                        break;
                    }
                    case 1:
                    {
                        // Default
                        orderSpinner.setEnabled(false);

                        ArrayList<String> defaultSort=favouritesAsStrings;
                        Collections.sort(defaultSort, new DefaultComparator());
                        favViewAdapter = new FavoriteViewAdapter(context, defaultSort);
                        favView.setAdapter(favViewAdapter);
                        favViewAdapter.notifyDataSetChanged();
                        currentOrdering=defaultSort;
                        break;
                    }

                    case 2:
                    {
                        // Symbol
                        orderSpinner.setEnabled(true);
                        ArrayList<String> symbolSort=favouritesAsStrings;
                        Collections.sort(symbolSort);

                        if(order.equalsIgnoreCase("Descending"))
                        {
                            Collections.reverse(symbolSort);
                        }

                        favViewAdapter = new FavoriteViewAdapter(context, symbolSort);
                        favView.setAdapter(favViewAdapter);
                        favViewAdapter.notifyDataSetChanged();
                        currentOrdering=symbolSort;
                        break;
                    }

                    case 3:
                    {
                        //Price
                        orderSpinner.setEnabled(true);
                        ArrayList<String> priceSort=favouritesAsStrings;
                        Collections.sort(priceSort, new PriceComparator());
                        if(order.equalsIgnoreCase("Descending"))
                        {
                            Collections.reverse(priceSort);
                        }

                        favViewAdapter = new FavoriteViewAdapter(context, priceSort);
                        favView.setAdapter(favViewAdapter);
                        favViewAdapter.notifyDataSetChanged();
                        currentOrdering=priceSort;
                        break;
                    }

                    case 4:
                    {
                        //Change
                        orderSpinner.setEnabled(true);
                        ArrayList<String> changeSort=favouritesAsStrings;
                        Collections.sort(changeSort, new ChangeComparator());
                        if(order.equalsIgnoreCase("Descending"))
                        {
                            Collections.reverse(changeSort);
                        }

                        favViewAdapter = new FavoriteViewAdapter(context, changeSort);
                        favView.setAdapter(favViewAdapter);
                        favViewAdapter.notifyDataSetChanged();
                        currentOrdering=changeSort;
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void OrderFavorites(final ListView favView, final ArrayList<String> favouritesAsStrings)
    {
        orderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String sort=String.valueOf(sortSpinner.getSelectedItem());

                switch (position)
                {
                    case 0:
                    {
                        break;
                    }
                    case 1:
                    {
                        if(sort.equalsIgnoreCase("Symbol"))
                        {
                            ArrayList<String> symbolSort=favouritesAsStrings;
                            Collections.sort(symbolSort);

                            favViewAdapter = new FavoriteViewAdapter(context, symbolSort);
                            favView.setAdapter(favViewAdapter);
                            favViewAdapter.notifyDataSetChanged();
                            currentOrdering=symbolSort;
                        }

                        else if(sort.equalsIgnoreCase("Price"))
                        {
                            ArrayList<String> priceSort=favouritesAsStrings;
                            Collections.sort(priceSort, new PriceComparator());

                            favViewAdapter = new FavoriteViewAdapter(context, priceSort);
                            favView.setAdapter(favViewAdapter);
                            favViewAdapter.notifyDataSetChanged();
                            currentOrdering=priceSort;
                        }

                        else if(sort.equalsIgnoreCase("Change"))
                        {
                            ArrayList<String> changeSort=favouritesAsStrings;
                            Collections.sort(changeSort, new ChangeComparator());

                            favViewAdapter = new FavoriteViewAdapter(context, changeSort);
                            favView.setAdapter(favViewAdapter);
                            favViewAdapter.notifyDataSetChanged();
                            currentOrdering=changeSort;
                        }

                        break;
                    }

                    case 2:
                    {
                        // Descending
                        if(sort.equalsIgnoreCase("Default") || sort.equalsIgnoreCase("Sort by"))
                        {
                            ArrayList<String> defaultSort=new ArrayList();

                            SharedPreferences s = getSharedPreferences("Favorites", Context.MODE_PRIVATE);
                            Map<String, ?> allEntries = s.getAll();
                            for (Map.Entry<String, ?> entry : allEntries.entrySet())
                            {
                                defaultSort.add(entry.getKey().toString() + ", " + entry.getValue().toString());
                            }
                            Collections.reverse(defaultSort);

                            favViewAdapter = new FavoriteViewAdapter(context, defaultSort);
                            favView.setAdapter(favViewAdapter);
                            favViewAdapter.notifyDataSetChanged();
                            currentOrdering=defaultSort;
                        }

                        else if(sort.equalsIgnoreCase("Symbol"))
                        {
                            ArrayList<String> symbolSort=favouritesAsStrings;
                            Collections.sort(symbolSort);
                            Collections.reverse(symbolSort);

                            favViewAdapter = new FavoriteViewAdapter(context, symbolSort);
                            favView.setAdapter(favViewAdapter);
                            favViewAdapter.notifyDataSetChanged();
                            currentOrdering=symbolSort;
                        }

                        else if(sort.equalsIgnoreCase("Price"))
                        {
                            ArrayList<String> priceSort=favouritesAsStrings;
                            Collections.sort(priceSort, new PriceComparator());
                            Collections.reverse(priceSort);

                            favViewAdapter = new FavoriteViewAdapter(context, priceSort);
                            favView.setAdapter(favViewAdapter);
                            favViewAdapter.notifyDataSetChanged();
                            currentOrdering=priceSort;
                        }

                        else if(sort.equalsIgnoreCase("Change"))
                        {
                            ArrayList<String> changeSort=favouritesAsStrings;
                            Collections.sort(changeSort, new ChangeComparator());
                            Collections.reverse(changeSort);

                            favViewAdapter = new FavoriteViewAdapter(context, changeSort);
                            favView.setAdapter(favViewAdapter);
                            favViewAdapter.notifyDataSetChanged();
                            currentOrdering=changeSort;
                        }

                        break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}

class PriceComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {

        String price1String= o1.split(",")[1];
        String price2String=o2.split(",")[1];

        double price1Double=Double.parseDouble(price1String);
        double price2Double=Double.parseDouble(price2String);

        if(price1Double<price2Double)
            return -1;
        else
            return 1;
    }
}

class ChangeComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {

        String change1String= o1.split(",")[2].split(" ")[0];
        String change2String=o2.split(",")[2].split(" ")[0];

        double change1Double=Double.parseDouble(change1String);
        double change2Double=Double.parseDouble(change2String);

        if(change1Double<change2Double)
            return -1;
        else
            return 1;
    }
}

class DefaultComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {

        String date1String= o1.split(",")[3];
        String date2String=o2.split(",")[3];

        Date date1= new Date(), date2=new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            date1 = date.parse(date1String);
            date2 = date.parse(date2String);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        if(date1.before(date2))
            return -1;
        else
            return 1;
    }
}
