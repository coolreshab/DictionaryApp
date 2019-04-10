package com.example.dictionary;

import android.app.SearchManager;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Details extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private String TAG=Details.class.getSimpleName();
    private TextView wordName;
    private ImageButton pronunciation;
    private ToggleButton favourite;
    private static String BundleQuery="search";
    private TextView phonetic;
    private static int LoaderId=97;
    private RecyclerView recyclerView;
    private GreenAdapterDetails greenAdapter;
    private static String query;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private GoogleFetchInfoFull results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        wordName=(TextView)findViewById(R.id.wordName);
        pronunciation=(ImageButton)findViewById(R.id.pronunciation);
        phonetic=(TextView)findViewById(R.id.phonetic);
        favourite=(ToggleButton)findViewById(R.id.favourite);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView1);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }
        else{
            query=intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        query=query.trim();
        pronunciation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Details.this,"Pronunciation",Toast.LENGTH_LONG).show();
                playPronunciation();
            }
        });
        if(getSupportLoaderManager().getLoader(LoaderId)==null)
            doMySearch(query);
        else
            getSupportLoaderManager().initLoader(LoaderId, null, this);
    }
    private void doMySearch(String query){
        //Log.d(TAG,"INSIDE DO MY SEARCH "+query);
        String url = NetworkUtils.getGoogleDictionaryUrl(query);
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

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle bundle) {

        return new AsyncTaskLoader<String>(this) {

            String googleJson;

            @Override
            protected void onStartLoading() {
                if(bundle==null)
                    return;
                if(googleJson!=null){
                    deliverResult(googleJson);
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
                googleJson=data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {

        progressBar.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(s))
            wordName.setText("Oops Something Went Wrong");
        else {

            results=JsonParser.googleDictionaryJsonParser(s);
            if(results==null){
                wordName.setText("Oops Something Went Wrong");
            }
            else{
                wordName.setText(GoogleFetchInfoFull.convertFirstToUpper(query));
                pronunciation.setVisibility(View.VISIBLE);
                favourite.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(results.phonetic))
                    phonetic.setText("Phonetic: "+results.phonetic);
                greenAdapter = new GreenAdapterDetails(results.meaning);
                recyclerView.setAdapter(greenAdapter);
                recyclerView.setHasFixedSize(true);
                DividerItemDecoration itemDecor = new DividerItemDecoration(this, 1);
                recyclerView.addItemDecoration(itemDecor);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
            }
        }
    }

    void playPronunciation(){
        pronunciation.setEnabled(false);
        String audioUrl = results.pronunciation;
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try{
            mPlayer.setDataSource(audioUrl);
            mPlayer.prepareAsync();
        }catch (IOException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer player) {
                player.start();
            }

        });
        pronunciation.setEnabled(true);
    }
    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }


}

