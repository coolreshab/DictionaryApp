package com.example.dictionary;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.SearchView;


import com.example.dictionary.database.WordsDb;

public class SavedWords extends AppCompatActivity implements GreenAdapterSavedWords.ButtonListener{

    private SearchView searchView;
    private RecyclerView recyclerView;
    private GreenAdapterSavedWords greenAdapter;
    private String TAG=Home.class.getSimpleName();
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                //viewHolder.getItemId()
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onCLickButton(int id) {



    }
}
