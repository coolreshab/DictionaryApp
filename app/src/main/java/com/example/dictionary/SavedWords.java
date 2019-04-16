package com.example.dictionary;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.ToggleButton;


import com.example.dictionary.database.WordsDb;
import com.example.dictionary.database.WordsEntity;

import java.util.List;

public class SavedWords extends AppCompatActivity implements GreenAdapterSavedWords.ButtonListener{

    private SearchView searchView;
    private RecyclerView recyclerView;
    private GreenAdapterSavedWords greenAdapter;
    private String TAG=SavedWords.class.getSimpleName();
    private WordsDb wordsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_words);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.searchSavedWords);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setQueryRefinementEnabled(true);
        wordsDb = WordsDb.getInstance(getApplicationContext());
        recyclerView=(RecyclerView)findViewById(R.id.recyclerViewSavedWords);
        greenAdapter = new GreenAdapterSavedWords(wordsDb.wordsDao().loadAllWords(), this);
        recyclerView.setAdapter(greenAdapter);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, 1);
        recyclerView.addItemDecoration(itemDecor);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        wordsDb=WordsDb.getInstance(getApplicationContext());
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //Log.d(TAG,Integer.toString((int)viewHolder.getAdapterPosition()));
                final WordsEntity obj=greenAdapter.getItem((int) viewHolder.getAdapterPosition());
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        wordsDb.wordsDao().deleteWords(obj);
                        final List<WordsEntity>savedData=wordsDb.wordsDao().loadAllWords();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                greenAdapter.setWords(savedData);
                            }
                        });
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onClickButton(int id) {

        String word=greenAdapter.getItemWord(id);
        Intent intent=new Intent(SavedWords.this,Details.class);
        intent.putExtra(Intent.EXTRA_TEXT,word);
        startActivity(intent);
    }

    @Override
    public void onClickFavourite(final int id) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                WordsEntity savedResult=wordsDb.wordsDao().loadWordByName(GoogleFetchInfoFull.convert(greenAdapter.getItemWord(id)));
                savedResult.setIsStarred(!savedResult.getIsStarred());
                wordsDb.wordsDao().updateWords(savedResult);
            }
        });
    }
}
