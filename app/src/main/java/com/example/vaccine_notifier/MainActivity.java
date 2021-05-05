package com.example.vaccine_notifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner spinnerState;
    Spinner spinnerDistrict;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        Intent i=new Intent(this,Myservice.class);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            startForegroundService(i);
        }
        else
        {
            startService(i);
        }
        spinnerState=(Spinner)findViewById(R.id.spinnerState);
        spinnerDistrict=(Spinner)findViewById(R.id.spinnerDistrict);
        addItemsOnSpinnerState();
        addItemsOnSpinnerDistrict();
    }
    
    public void addItemsOnSpinnerState() {
        List<String> listState = new ArrayList<String>();
        listState.add("Choose one State");
        listState.add("State 1");
        listState.add("State 2");
        listState.add("State 3");
        listState.add("State 4");
        listState.add("State 5");
        listState.add("State 6");
        listState.add("State 7");
        listState.add("State 8");
        listState.add("State 9");
        listState.add("State 10");
        ArrayAdapter<String> dataAdapterState = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listState);
        dataAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(dataAdapterState);
    }
    public void addItemsOnSpinnerDistrict() {
        List<String> listDistrict = new ArrayList<String>();
        listDistrict.add("Choose one District");
        listDistrict.add("District 1");
        listDistrict.add("District 2");
        listDistrict.add("District 3");
        listDistrict.add("District 4");
        listDistrict.add("District 5");
        listDistrict.add("District 6");
        listDistrict.add("District 7");
        listDistrict.add("District 8");
        listDistrict.add("District 9");
        listDistrict.add("District 10");
        ArrayAdapter<String> dataAdapterDistrict = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listDistrict);
        dataAdapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(dataAdapterDistrict);
    }
    public void checkStatus(View view)
    {
        //checkStatus
        Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
    }
    public void checkBoxClicked(View view)
    {
        //checkStatus
        Toast.makeText(this, "CheckBox clicked", Toast.LENGTH_SHORT).show();
    }
}