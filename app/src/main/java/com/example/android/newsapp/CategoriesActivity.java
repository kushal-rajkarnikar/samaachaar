package com.example.android.newsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;

public class CategoriesActivity extends AppCompatActivity{

    Button politics, business, environment, technology, music, all, science, sports;

    private Context context = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        CategoriesActivity.this.setTitle("Categories");

        politics = (Button) findViewById(R.id.Politics);
        business = (Button) findViewById(R.id.Business);
        environment = (Button) findViewById(R.id.Environment);
        technology = (Button) findViewById(R.id.Technology);
        music = (Button) findViewById(R.id.Music);
        science = (Button) findViewById(R.id.Science);
        all = (Button) findViewById(R.id.All);
        sports = (Button) findViewById(R.id.Sports);

        final Intent i = new Intent(CategoriesActivity.this, NewsActivity.class);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("selectCategry","world");
                startActivity(i);
            }
        });

        politics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("selectCategry","politics");
                startActivity(i);
            }
        });

        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("selectCategry","business");
                startActivity(i);
            }
        });

        environment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("selectCategry","environment");
                startActivity(i);
            }
        });

        technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("selectCategry","technology");
                startActivity(i);
            }
        });

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("selectCategry","music");
                startActivity(i);
            }
        });

        science.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("selectCategry","science");
                startActivity(i);
            }
        });

        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("selectCategry","sport");
                startActivity(i);
            }
        });

    }

}
