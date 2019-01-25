package com.example.ferguson.connectivityassessment.Activities;

import android.annotation.SuppressLint;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ferguson.connectivityassessment.Adapters.EntryAdapter;
import com.example.ferguson.connectivityassessment.Models.Entry;
import com.example.ferguson.connectivityassessment.Models.School;
import com.example.ferguson.connectivityassessment.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.ferguson.connectivityassessment.Activities.LoginActivity.appkey;

public class MainActivity extends AppCompatActivity {
    public static String uid;
    private RequestQueue requestQueue;
    private Gson gson;
    private List<Entry> entryList;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;
    private EntryAdapter entryAdapter;
    public FloatingActionButton floatingActionButton;
    public static List<School> schoolsList;
    public static List<String> regionsList;
    public static int regID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Entry List");
        initialize();
        startBackgroundFetch();
        updateTimeStamp();
        setupGSON();
        startGetThread();
        setupRefreshMode();
        setupFab();
    }

    private void startBackgroundFetch() {
        Thread schools = new Thread() {
            @Override
            public void run() {
                String url = LoginActivity.baseUrl + "schools/" + regID;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            schoolsList = Arrays.asList(gson.fromJson(jsonArray.toString(), School[].class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("x-api-key", appkey);
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        };
        schools.start();
    }

    private void setupFab() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRefreshMode() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                entryList.clear();
                //   requestQueue = Volley.newRequestQueue(getApplicationContext());
                startGetThread();
            }
        });
    }

    private void setupGSON() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
    }

    private void initialize() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        floatingActionButton = findViewById(R.id.floating_action);
        setSupportActionBar(myToolbar);
        requestQueue = Volley.newRequestQueue(this);
        uid = getIntent().getStringExtra("uid");
        regID = getIntent().getIntExtra("regID", 0);
        entryList = new ArrayList<>();
    }

    private void startGetThread() {
        final Handler handler = new Handler();
        Thread thread = new Thread() {
            @Override
            public void run() {
                String url = LoginActivity.baseUrl + "entries/" + uid;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            entryList = Arrays.asList(gson.fromJson(jsonArray.toString(), Entry[].class));
                            makeRecycler();
                            entryAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("x-api-key", appkey);
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        };

        thread.start();
    }

    private void makeRecycler() {
        recyclerView = findViewById(R.id.entry_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        entryAdapter = new EntryAdapter(getLayoutInflater(), entryList);
        recyclerView.setAdapter(entryAdapter);
    }

    @SuppressLint("NewApi")
    private void updateTimeStamp() {
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date c = Calendar.getInstance().getTime();
        String url = LoginActivity.baseUrl + "user/update/date/" + uid + "?&date=" + simpleDateFormat.format(c);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("x-api-key", appkey);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

}

