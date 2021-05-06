package com.example.vaccine_notifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Spinner spinnerState;
    Spinner spinnerDistrict;
    List<String> listState = new ArrayList<String>();
    List<String> listStateid = new ArrayList<String>();
    List<String> listDistrict = new ArrayList<String>();
    List<String> listDistrictid = new ArrayList<String>();
    String districtId;
    int Min_age=45;
    int notif=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinnerState=(Spinner)findViewById(R.id.spinnerState);
        spinnerDistrict=(Spinner)findViewById(R.id.spinnerDistrict);
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

        loadstates();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.FOREGROUND_SERVICE,Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.BROADCAST_WAP_PUSH},
                1);
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            }

            List<ResolveInfo> list = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                getApplicationContext().startActivity(intent);
            }
        } catch (Exception e) {

        }
        Switch s=(Switch)findViewById(R.id.switch2);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Min_age=18;
                    Toast.makeText(MainActivity.this,Integer.toString(Min_age), Toast.LENGTH_SHORT).show();

                } else {
                    Min_age=45;
                }
            }
        });
        CheckBox c=findViewById(R.id.checkBox);
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    notif=1;
                   // Toast.makeText(MainActivity.this,Integer.toString(notif), Toast.LENGTH_SHORT).show();

                }
                else
                {
                    notif=0;
                }
            }
        });
        addItemsOnSpinnerState();
        addItemsOnSpinnerDistrict();

    }

    public void addListenerOnSpinnerItemSelection() {
    spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           // Toast.makeText(parent.getContext(),
               //     "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
             //       Toast.LENGTH_SHORT).show();
           // Toast.makeText(MainActivity.this, listStateid.get(position), Toast.LENGTH_SHORT).show();
            if(position!=0)
            load_districts(listStateid.get(position));
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(18);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });
      spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          //  Toast.makeText(MainActivity.this, listDistrict.get(position), Toast.LENGTH_SHORT).show();
            if(position!=0)
            districtId=listDistrictid.get(position);
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(18);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });
    }

    public void load_districts(String id)
    {
        RequestQueue queue2= Volley.newRequestQueue(this);
        String url ="https://cdn-api.co-vin.in/api/v2/admin/location/districts/"+id;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("dist","called");
                try {
                    JSONObject obj = response;
                    JSONArray centresArray = obj.getJSONArray("districts");
                    //now looping through all the elements of the json array
                    for (int i = 0; i < centresArray.length(); i++)
                    {
                        //getting the json object of the particular index inside the array
                        JSONObject centresObject = centresArray.getJSONObject(i);
                        String district_name=centresObject.getString("district_name");
                        String district_id=centresObject.getString("district_id");
                        listDistrict.add(district_name);
                        listDistrictid.add(district_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //displaying the error in toast if occur
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
                headers.put("accept","application/json");
                return headers;
            }
        };
      //  queue2.add(objectRequest);
        MySingleton.getInstance(this).addToRequestQueue(objectRequest);
    }

    public void loadstates()
    {
        listState.add("Choose one State");
        listStateid.add("0000");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://cdn-api.co-vin.in/api/v2/admin/location/states";
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("state","called");
                try {
                    JSONObject obj = response;
                    JSONArray centresArray = obj.getJSONArray("states");
                    //now looping through all the elements of the json array
                    for (int i = 0; i < centresArray.length(); i++)
                    {
                        //getting the json object of the particular index inside the array
                        JSONObject centresObject = centresArray.getJSONObject(i);
                        String state_name=centresObject.getString("state_name");
                        String state_id=centresObject.getString("state_id");
                        listState.add(state_name);
                        listStateid.add(state_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occur
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
                return headers;
            }
        };
      //  queue.add(stringRequest);
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        addListenerOnSpinnerItemSelection();
    }

    public void addItemsOnSpinnerState() {

         ArrayAdapter<String> dataAdapterState = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listState);
        dataAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(dataAdapterState);
    }


    public void addItemsOnSpinnerDistrict() {
        listDistrict.add("Choose one District");
        listDistrictid.add("00000");
        ArrayAdapter<String> dataAdapterDistrict = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listDistrict);
        dataAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(dataAdapterDistrict);
    }
    public void checkStatus(View view)
    {

        Intent in=new Intent(getApplicationContext(),Myservice.class);
        in.putExtra("disid",districtId);
        in.putExtra("age",Min_age);
        in.putExtra("notif",notif);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            startForegroundService(in);
        }
        else
        {
            startService(in);
        }
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        // Do your work here

        //Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        final int delay = 2000;
        handler.postDelayed(new Runnable() {
            public void run() {
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), Results.class);
                startActivity(intent);
                finish();
            }
        }, delay);

    }


    public void checkBoxClicked(View view)
    {
        //checkStatus
       // Toast.makeText(this, "CheckBox clicked", Toast.LENGTH_SHORT).show();
    }
    public void ageclick(View view)
    {
        //AGE

    }
}