package com.example.dictionary;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class Home extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, GreenAdapterHome.ButtonListener {

    private SearchView searchView;
    private static String randomQuery;
    private static int LoaderId=97;
    private RecyclerView recyclerView;
    private GreenAdapterHome greenAdapter;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    String TAG=Home.class.getSimpleName();
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.recyclerView);
        greenAdapter = new GreenAdapterHome(new ArrayList<String>(), this);
        recyclerView.setAdapter(greenAdapter);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, 1);
        recyclerView.addItemDecoration(itemDecor);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mySwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        sharedPref=Home.this.getPreferences(Context.MODE_PRIVATE);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getSupportLoaderManager().restartLoader(LoaderId, null,Home.this);
                    }
                }
        );
        getSupportLoaderManager().initLoader(LoaderId, null, this);
        //handle configuration changes
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.saved_words, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Lets Sprint");
        //searchView.setQueryRefinementEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=new Intent(Home.this,SavedWords.class);
        startActivity(intent);
        return true;
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle bundle) {

        return new AsyncTaskLoader<String>(this) {

            String datamuseJson;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                mySwipeRefreshLayout.setRefreshing(true);
                if(datamuseJson!=null){
                    deliverResult(datamuseJson);
                }
                else{
                    forceLoad();
                }
            }
            @Nullable
            @Override
            public String loadInBackground() {
                randomQuery=randomizer();
                String url = NetworkUtils.getDataMuseUrl(randomQuery,"words","sp","1000");
                try {
                    String response=NetworkUtils.getResponse(new URL(url));
                    return response;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void deliverResult(@Nullable String data) {
                super.deliverResult(data);
                datamuseJson=data;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {

        if (TextUtils.isEmpty(s)) {
            Toast.makeText(Home.this, "Network Error", Toast.LENGTH_SHORT).show();
            s=getRandomWordsPreference();
            if(s!=null)
                greenAdapter.setWords(JsonParser.dataMuseParser(s));
        }
        else {

            ArrayList<String>data=JsonParser.dataMuseParser(s);
            if (data.isEmpty()) {
                Toast.makeText(Home.this,"Oops nothing to show",Toast.LENGTH_SHORT).show();
            }
            else {
                greenAdapter.setWords(data);
                applyPersistence(s);
            }
        }
        mySwipeRefreshLayout.setRefreshing(false);
    }
    private String getRandomWordsPreference(){
        String randomWordsJson=sharedPref.getString(getString(R.string.golden_key),null);
        return randomWordsJson;
    }
    private void applyPersistence(String randomWordsJson){
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putString(getString(R.string.golden_key),randomWordsJson);
        editor.apply();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public void onCLickButton(int id) {
        String word=greenAdapter.getItem(id);
        Intent intent=new Intent(Home.this,Details.class);
        intent.putExtra(Intent.EXTRA_TEXT,word);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }
    public String randomizer(){

        String randomString="";
        Random rand=new Random();
        int option=rand.nextInt(4);
        if(option==0){

            int lenPrefix=1;
            while(lenPrefix>0){
                randomString+=(char) ('a'+rand.nextInt(26));
                lenPrefix--;
            }
            randomString+="*";
        }
        else if(option==1){

            randomString+="*";
            int lenSuffix=1;
            while(lenSuffix>0){
                randomString+=(char) ('a'+rand.nextInt(26));
                lenSuffix--;
            }
        }
        else if(option==2){

            int len=rand.nextInt(8)+3;
            while(len>0){
                randomString+="?";
                len--;
            }
        }
        else{

            int lenSubsequence=rand.nextInt(2)+1;
            while(lenSubsequence>0){
                randomString+="*";
                randomString+=(char) ('a'+rand.nextInt(26));
                lenSubsequence--;
            }
            randomString+="*";
        }
        return randomString;
    }
}
