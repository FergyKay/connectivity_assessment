package com.example.ferguson.connectivityassessment.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ferguson.connectivityassessment.Models.School;
import com.example.ferguson.connectivityassessment.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.ferguson.connectivityassessment.Activities.LoginActivity.appkey;

public class NewEntryActivity extends AppCompatActivity {
    public android.support.v7.widget.Toolbar toolbar;
    public Spinner schoolSpinner;
    private List<String> schoolListString;
    public EditText lat, lng;
    public Button upload;
    public int cur_school_id;
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        initialize();
        getSchoolListing();
        makeCombo();
        setUploadTriggers();
    }

    private void setUploadTriggers() {
        final SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Date c = Calendar.getInstance().getTime();
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!lat.getText().toString().isEmpty() && !lng.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(NewEntryActivity.this)
                            .setTitle("Uploading Data")
                            .setMessage("Are you sure you want to upload entered data? Data Once uploaded cannot be edited!")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pushToServer(MainActivity.uid, simpleDateFormat.format(c), lng.getText().toString(), lat.getText().toString());
                                }
                            }).setNegativeButton("Recheck for correctness", null).show();
                } else
                    Toast.makeText(NewEntryActivity.this, "Fill All Fields", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void pushToServer(final String uid, final String date, final String lng, final String lat) {
        StringRequest putRequest = new StringRequest(Request.Method.GET,
                LoginActivity.baseUrl + "school/update/" + cur_school_id + "?latitude=" + lat + "&longitude=" + lng + "&pId=" + uid + "&date=" + date,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        new AlertDialog.Builder(NewEntryActivity.this)
                                .setTitle("Success").setIcon(R.drawable.ic_error_outline_black_24dp)
                                .setMessage("Data uploaded and recorded successfully!").setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        })
                                .show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("x-api-key", appkey);
                return hashMap;
            }
        };

        queue.add(putRequest);

    }

    private void makeCombo() {
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getApplication().getBaseContext(), R.layout.spinner_item, schoolListString);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        schoolSpinner.setAdapter(spinnerArrayAdapter);

        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (School e : MainActivity.schoolsList) {
                    if (e.getSchool_name().equals(schoolSpinner.getSelectedItem()))
                        cur_school_id = e.getSchool_id();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                upload.setEnabled(false);
            }
        });
    }

    private void getSchoolListing() {
        try {
            for (School e : MainActivity.schoolsList) {
                schoolListString.add(e.getSchool_name());
            }
        } catch (Exception e) {
            new AlertDialog.Builder(NewEntryActivity.this)
                    .setTitle("Alert")
                    .setMessage("All schools in the current region updated").setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    finish();
                }
            }).setIcon(R.drawable.ic_error_outline_black_24dp).show();
        }
    }

    private void initialize() {
        toolbar = findViewById(R.id.toolbar);
        lat = findViewById(R.id.lat_value);
        lng = findViewById(R.id.lng_value);
        setSupportActionBar(toolbar);
        upload = findViewById(R.id.upload);
        schoolSpinner = findViewById(R.id.school_combo);
        schoolListString = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
    }
}
