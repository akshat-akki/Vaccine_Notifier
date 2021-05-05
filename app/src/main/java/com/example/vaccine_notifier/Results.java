package com.example.vaccine_notifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

public class Results extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        MyListData[] myListData = new MyListData[] {
                new MyListData("abcd", "23","09/10/20"),
                new MyListData("abcd", "23","09/10/20"),
                new MyListData("abcd", "23","09/10/20"),
                new MyListData("abcd", "23","09/10/20"),
                new MyListData("abcd", "23","09/10/20"),
                new MyListData("abcd", "23","09/10/20"),
                new MyListData("abcd", "23","09/10/20"),
                new MyListData("abcd", "23","09/10/20"),
                new MyListData("abcd", "23","09/10/20"),
                new MyListData("abcd", "23","09/10/20")
        };

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MyListAdapter adapter = new MyListAdapter(myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}