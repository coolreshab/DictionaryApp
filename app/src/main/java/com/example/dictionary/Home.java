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
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class Home extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, GreenAdapter.ButtonListener {

    private SearchView searchView;
    private static String BundleQuery="search";
    private static String randomQuery;
    private static int LoaderId=97;
    private RecyclerView recyclerView;
    private GreenAdapter greenAdapter;
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
        searchHeading = (TextView) findViewById(R.id.searchHeading);
        recyclerView = findViewById(R.id.recyclerView);
        mySwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        displayRandomWords();
                    }
                }
        );
        if(getSupportLoaderManager().getLoader(LoaderId)==null)
            displayRandomWords();
        else
            getSupportLoaderManager().initLoader(LoaderId, null, this);
        //handle configuration changes
    }

    public void displayRandomWords(){

        mySwipeRefreshLayout.setRefreshing(true);
        randomQuery=randomizer();
        Log.d(TAG,randomQuery);
        String url = NetworkUtils.getDataMuseUrl(randomQuery);
        Bundle bundle=new Bundle();
        bundle.putString(BundleQuery,url);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(LoaderId);
        if (loader == null) {
            loaderManager.initLoader(LoaderId, bundle, this);
        } else {
            loaderManager.restartLoader(LoaderId, bundle,this);
        }
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
                if(bundle==null)
                    return;
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
                String url=bundle.getString(BundleQuery);
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
                datamuseJson=data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {

        if (TextUtils.isEmpty(s))
            searchHeading.setText("Network Error");
        else {

            greenAdapter = new GreenAdapter(JsonParser.dataMuseParser(s), this);
            if (greenAdapter.getItemCount() == 0) {
                searchHeading.setText("Oops nothing to show");
            }
            else {
                searchHeading.setText("Random words matching "+randomQuery);
                recyclerView.setAdapter(greenAdapter);
                recyclerView.setHasFixedSize(true);
                DividerItemDecoration itemDecor = new DividerItemDecoration(this, 1);
                recyclerView.addItemDecoration(itemDecor);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);

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

            int lenPrefix=rand.nextInt(3)+1;
            while(lenPrefix>0){
                randomString+=(char) ('a'+rand.nextInt(26));
                lenPrefix--;
            }
            randomString+="*";
        }
        else if(option==1){

            randomString+="*";
            int lenSuffix=rand.nextInt(3)+1;
            while(lenSuffix>0){
                randomString+=(char) ('a'+rand.nextInt(26));
                lenSuffix--;
            }
        }
        else if(option==2){

            int len=rand.nextInt(4)+3;
            while(len>0){
                if(len%3==0)
                    randomString+=(char) ('a'+rand.nextInt(26));
                else
                    randomString+="?";
                len--;
            }
        }
        else{

            int lenSubsequence=rand.nextInt(3)+1;
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
