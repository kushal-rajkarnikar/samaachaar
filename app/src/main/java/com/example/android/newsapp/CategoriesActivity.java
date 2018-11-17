package com.example.android.newsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CategoriesActivity extends AppCompatActivity {

    TextView politics, business, sports, entertainment, art, other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);


        politics = (TextView) findViewById(R.id.Politics);
        business = (TextView) findViewById(R.id.Business);
        sports = (TextView) findViewById(R.id.Sports);
        entertainment = (TextView) findViewById(R.id.Entertainment);
        art = (TextView) findViewById(R.id.Art);
        other = (TextView) findViewById(R.id.Other);

    }
}
