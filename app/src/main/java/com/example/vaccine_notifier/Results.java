package com.example.vaccine_notifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class Results extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
         ArrayList<String> centre=Myservice.centre_name;
         ArrayList<String> date=Myservice.date;
         ArrayList<String> capacity_array=Myservice.capacity_array;
        MyListData[] myListData = new MyListData[centre.size()];
        if(centre.size()==0)
        {
            Toast.makeText(this, "NO SLOT AVAILABLE!!", Toast.LENGTH_LONG).show();
        }
        for(int i=0;i<centre.size();i++)
        {
            MyListData m=new MyListData();
            m.setCentre(centre.get(i));
            m.setAvailability("Availability:"+capacity_array.get(i));
            m.setDate(date.get(i));
            myListData[i]=m;
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MyListAdapter adapter = new MyListAdapter(myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}