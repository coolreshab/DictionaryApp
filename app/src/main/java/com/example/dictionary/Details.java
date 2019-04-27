package com.example.dictionary;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.dictionary.database.WordsDb;
import com.example.dictionary.database.WordsEntity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Date;

public class Details extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private String TAG = Details.class.getSimpleName();
    private TextView wordName;
    private ToggleButton pronunciation;
    private ToggleButton favourite;
    private TextView phonetic;
    private final static int LoaderId = 97;
    private RecyclerView recyclerView;
    private GreenAdapterDetails greenAdapter;
    private String query;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private GoogleFetchInfoFull results;
    private ImageView wordNotFound;
    private ImageView networkError;
    private WordsDb wordsDb;
    private final static long THRESHOLD = 10;
    private MediaPlayer mPlayer;
    private final static String FOLDER_NAME="pronunciation";
    private ProgressBar circularProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        init();
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            query = intent.getDataString();
        } else {
            query = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        query = query.trim();
        query = GoogleFetchInfoFull.convert(query);
        //Log.d(TAG,query);
        pronunciation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAudio();
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        WordsEntity savedResult = wordsDb.wordsDao().loadWordByName(GoogleFetchInfoFull.convert(results.wordName));
                        savedResult.setIsStarred(!savedResult.getIsStarred());
                        wordsDb.wordsDao().updateWords(savedResult);
                    }
                });
            }
        });
        wordsDb = WordsDb.getInstance(getApplicationContext());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final WordsEntity savedResult = wordsDb.wordsDao().loadWordByName(query);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (savedResult != null && getDiff(new Date(), savedResult.getTicks()) < THRESHOLD) {
                            progressBar.setVisibility(View.INVISIBLE);
                            linearLayout.setVisibility(View.VISIBLE);
                            displayDetails(results = JsonParser.googleDictionaryJsonParser(savedResult.getWordDetails()));
                            if (savedResult.getIsStarred()) {
                                favourite.setChecked(true);
                                //set the toggled state
                            }
                        } else {
                            if (savedResult != null) {
                                results = JsonParser.googleDictionaryJsonParser(savedResult.getWordDetails());
                                if (savedResult.getIsStarred()) {
                                    favourite.setChecked(true);
                                    //set the toggled state
                                }
                            }
                            getSupportLoaderManager().initLoader(LoaderId, null, Details.this);
                        }
                    }
                });
            }
        });
        ;
    }

    private void downloadAudio() {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                startDownload(results.pronunciation);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        circularProgress.setVisibility(View.GONE);
                        pronunciation.setVisibility(View.VISIBLE);
                        playPronunciation();
                    }
                });
            }
        });
    }
    private void startDownload(String link){

        int count;
        try {
            File subFolder = new File(getAudioFolderUrl());
            if (!subFolder.exists()) {
                subFolder.mkdirs();
            }
            File target=new File(subFolder, getFileName(link));
            if(!target.exists()){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        circularProgress.setVisibility(View.VISIBLE);
                        pronunciation.setVisibility(View.GONE);
                    }
                });
                URL url=new URL(link);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                Log.d(TAG,target.toString());
                OutputStream output = new FileOutputStream(target);

                byte data[] = new byte[1024];

                int total = 0;

                while ((count = input.read(data)) != -1) {

                    total += count;
                    publishProgress(total*100/lenghtOfFile);
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getFileName(String link){
        String FILE_NAME= Uri.parse(link).getLastPathSegment();
        return FILE_NAME;
    }
    public String getAudioFolderUrl(){

        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + File.separator + FOLDER_NAME;
        return folder;
    }
    private void publishProgress(final int score){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //update UI progress bar percentage
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    circularProgress.setProgress(score,true);
                }
                else{
                    circularProgress.setProgress(score);
                }
            }
        });
    }
    private void init() {
        wordName = (TextView) findViewById(R.id.wordName);
        pronunciation = (ToggleButton) findViewById(R.id.pronunciation);
        phonetic = (TextView) findViewById(R.id.phonetic);
        favourite = (ToggleButton) findViewById(R.id.favourite);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        mPlayer=new MediaPlayer();
        circularProgress=(ProgressBar)findViewById(R.id.progressBarCircular);
        wordNotFound=(ImageView)findViewById(R.id.wordNotFound);
        networkError=(ImageView)findViewById(R.id.networkError);
    }

    public double getDiff(Date a, Date b) {
        double x = a.getTime();
        double y = b.getTime();
        return (x - y) / (1000 * 60 * 60 * 24);//days
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle bundle) {

        return new AsyncTaskLoader<String>(this) {

            String googleJson;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (googleJson != null) {
                    deliverResult(googleJson);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String url = NetworkUtils.getGoogleDictionaryUrl(query);
                try {
                    String response = NetworkUtils.getResponse(new URL(url));
                    return response;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return "1";
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return "2";
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void deliverResult(@Nullable String data) {
                super.deliverResult(data);
                googleJson = data;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, final String s) {
        progressBar.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        if (s.equals("2")) {
            if (results == null) {
                //wordName.setText("Network Error");
                networkError.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            }
            else {
                //Toast.makeText(Details.this,"Network Error",Toast.LENGTH_SHORT).show();
                displayDetails(results);
            }
        } else if (s.equals("1")){
            //wordName.setText("Word not found");
            wordNotFound.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }
        else {
            results = JsonParser.googleDictionaryJsonParser(s);
            if (results == null || results.meaning.isEmpty()) {
                //wordName.setText("Word not found");
                wordNotFound.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            } else {
                displayDetails(results);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        saveDetails(s);
                    }
                });
            }
        }
    }

    private void saveDetails(String s) {
        //add s in to database
        //Log.d(TAG,"HERE");
        String word = GoogleFetchInfoFull.convert(results.wordName);

        WordsEntity obj = wordsDb.wordsDao().loadWordByName(word);
        if (obj == null)
            wordsDb.wordsDao().insertWords(new WordsEntity(word, s, false, new Date(),results.pronunciation));
        else if (getDiff(new Date(), obj.getTicks()) >= THRESHOLD) {
            //Log.d(TAG,"HERE");
            wordsDb.wordsDao().updateTicks_Json(new Date(), s, word);
        } else {
            //Log.d(TAG,"WILL NOT COME HERE");
        }
    }

    private void displayDetails(GoogleFetchInfoFull results) {

        wordName.setText(GoogleFetchInfoFull.convertFirstToUpper(results.wordName));
        if (!TextUtils.isEmpty(results.pronunciation))
            pronunciation.setVisibility(View.VISIBLE);
        favourite.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(results.phonetic))
            phonetic.setText("Phonetic: " + results.phonetic);
        greenAdapter = new GreenAdapterDetails(results.meaning);
        recyclerView.setAdapter(greenAdapter);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, 1);
        recyclerView.addItemDecoration(itemDecor);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void playPronunciation() {
        //optimize it by caching it :D
        File target=new File(getAudioFolderUrl(),getFileName(results.pronunciation));
        if (target.exists()) {
            String audioUrl = target.toString();
            pronunciation.setEnabled(false);
            mPlayer.reset();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayer.setDataSource(audioUrl);
                mPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    player.start();
                }
            });

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    pronunciation.setEnabled(true);
                    pronunciation.setChecked(false);
                }

            });
        }
        else{
            pronunciation.setChecked(false);
            Log.d(TAG,"ERROR file not found");
            Toast.makeText(Details.this,"Network Error",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
        mPlayer.release();
    }
}

