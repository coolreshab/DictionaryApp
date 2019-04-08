package com.example.dictionary;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Details extends AppCompatActivity {

    private String TAG=Details.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        String query=null;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }
        else{
            query=intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        doMySearch(query);
    }
    private void doMySearch(String query){
        Log.d(TAG,"INSIDE DO MY SEARCH "+query);
    }
}

