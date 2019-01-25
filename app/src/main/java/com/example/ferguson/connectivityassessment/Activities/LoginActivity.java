package com.example.ferguson.connectivityassessment.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ferguson.connectivityassessment.Helpers.BCrypt;
import com.example.ferguson.connectivityassessment.Models.Region;
import com.example.ferguson.connectivityassessment.Models.School;
import com.example.ferguson.connectivityassessment.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private EditText username;
    private EditText password;
    private RequestQueue queue;
    public static String baseUrl;
    private ProgressBar progressBar;
    private TextView wait_text;
    public static String appkey;
    private Spinner regionSpinner;
    public static List<Region> regionsList;
    private List<String> regionsListString;
    private Gson gson;


    public LoginActivity() {
        appkey = "xZami093765eioiksFSq";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        setupGSON();
        regionsPopulate();
        attachLoginListener();
    }

    private void regionsPopulate() {
        Thread regions = new Thread() {
            @Override
            public void run() {
                String url = LoginActivity.baseUrl + "regions";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            regionsList = Arrays.asList(gson.fromJson(jsonArray.toString(), Region[].class));
                            for (Region r : regionsList) {
                                regionsListString.add(r.getRegion_name());
                            }
                            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                    getApplication().getBaseContext(), R.layout.spinner_item, regionsListString);
                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            regionSpinner.setAdapter(spinnerArrayAdapter);
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
                queue.add(request);
            }
        };
        regions.start();

    }

    private void attachLoginListener() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authUser(String.valueOf(username.getText()), String.valueOf(password.getText()));
            }
        });
    }


    private void authUser(final String uname, final String pword) {
        if (!uname.isEmpty() && !pword.isEmpty()) {
            freezeAndLogon();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, baseUrl + "user/" + uname, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (BCrypt.checkpw(pword, response.getString("password"))) {
                            Intent main = new Intent(LoginActivity.this, MainActivity.class);
                            main.putExtra("uid", uname);
                            main.putExtra("regID", regionSpinner.getSelectedItemPosition() + 1);
                            startActivity(main);
                            finish();
                        } else {
                            wait_text.setText(R.string.login_error);
                            unfreezeAndEdit();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    unfreezeAndEdit();
                    wait_text.setText(R.string.something_wicked);
                    Log.e("Response", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("x-api-key", appkey);
                    return hashMap;
                }
            };

            queue.add(request);
        } else
            Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show();
    }


    private void unfreezeAndEdit() {
        username.setActivated(true);
        password.setActivated(true);
        loginBtn.setActivated(true);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void freezeAndLogon() {
        username.setEnabled(false);
        password.setEnabled(false);
        loginBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        wait_text.setText(R.string.wait);
        wait_text.setVisibility(View.VISIBLE);
    }

    private void initialize() {
        loginBtn = findViewById(R.id.sign_in);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        wait_text = findViewById(R.id.wait);
        regionSpinner = findViewById(R.id.region_list);
        wait_text.setVisibility(View.INVISIBLE);
        queue = Volley.newRequestQueue(this);
        baseUrl = "http://192.168.1.8/data_collection/";
        regionsList = new ArrayList<>();
        regionsListString = new ArrayList<>();

    }

    private void setupGSON() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
    }


}
