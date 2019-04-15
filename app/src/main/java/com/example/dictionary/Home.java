package com.example.dictionary;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
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
    private GreenAdapterHome greenAdapter=null;
    private TextView searchHeading;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    String TAG=Home.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setQueryRefinementEnabled(true);

        searchHeading = (TextView) findViewById(R.id.searchHeading);
        recyclerView = findViewById(R.id.recyclerView);
        mySwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
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
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      mySwipeRefreshLayout.setRefreshing(true);
                                  }
                              });
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

        if (TextUtils.isEmpty(s))
            Toast.makeText(Home.this,"Network Error",Toast.LENGTH_SHORT).show();
        else {

            ArrayList<String>data=JsonParser.dataMuseParser(s);
            if (data.isEmpty()) {
                Toast.makeText(Home.this,"Oops nothing to show",Toast.LENGTH_SHORT).show();
            }
            else {
                if (greenAdapter==null) {
                    greenAdapter = new GreenAdapterHome(data, this);
                    recyclerView.setAdapter(greenAdapter);
                    recyclerView.setHasFixedSize(true);
                    DividerItemDecoration itemDecor = new DividerItemDecoration(this, 1);
                    recyclerView.addItemDecoration(itemDecor);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(layoutManager);
                } else {
                    greenAdapter.setWords(data);
                }
            }
        }
        mySwipeRefreshLayout.setRefreshing(false);
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
