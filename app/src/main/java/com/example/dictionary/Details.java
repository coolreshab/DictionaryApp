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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Details extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private String TAG=Details.class.getSimpleName();
    private TextView wordName;
    private ImageButton pronunciation;
    private ToggleButton favourite;
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
        else if(Intent.ACTION_VIEW.equals(intent.getAction())){
            query=intent.getDataString();
        }
        else{
            query=intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        query=query.trim();
        String[] splited = query.split("\\s+");
        query="";
        for(int  i=0;i<splited.length;++i){
            query+=splited[i];
            if(i!=splited.length-1)
                query+="-";
        }
        Log.d(TAG,query);
        pronunciation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPronunciation();
            }
        });
        getSupportLoaderManager().initLoader(LoaderId, null, this);
    }
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle bundle) {

        return new AsyncTaskLoader<String>(this) {

            String googleJson;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
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
                String url = NetworkUtils.getGoogleDictionaryUrl(query);
                try {
                    String response=NetworkUtils.getResponse(new URL(url));
                    return response;
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return "1";
                }
                catch(UnknownHostException e){
                    e.printStackTrace();
                    return "2";
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void deliverResult(@Nullable String data) {
                super.deliverResult(data);
                googleJson=data;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {

        progressBar.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        if(s.equals("2"))
            wordName.setText("Network Error");
        else if (s.equals("1"))
            wordName.setText("Word not found");
        else {

            results=JsonParser.googleDictionaryJsonParser(s);
            if(results==null || results.meaning.isEmpty()){
                wordName.setText("Word not found");
            }
            else{
                wordName.setText(GoogleFetchInfoFull.convertFirstToUpper(results.wordName));
                if(!TextUtils.isEmpty(results.pronunciation))
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

