package com.coolreshab.dictionary;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.ImageView;


import com.coolreshab.dictionary.database.WordsDb;
import com.coolreshab.dictionary.database.WordsEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedWords extends AppCompatActivity implements GreenAdapterSavedWords.ButtonListener {

    private RecyclerView recyclerView;
    private GreenAdapterSavedWords greenAdapter;
    private String TAG = SavedWords.class.getSimpleName();
    private WordsDb wordsDb;
    private SearchView searchView;
    private SharedPreferences sharedPref;
    private final static String FOLDER_NAME = "pronunciation";
    private ImageView historyError;
    private ImageView historyErrorFavourite;
    private ImageView noResultsFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_words);
        wordsDb = WordsDb.getInstance(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSavedWords);
        greenAdapter = new GreenAdapterSavedWords(new ArrayList<WordsEntity>(), this);
        recyclerView.setAdapter(greenAdapter);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, 1);
        recyclerView.addItemDecoration(itemDecor);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        historyError = (ImageView) findViewById(R.id.historyError);
        historyErrorFavourite = (ImageView) findViewById(R.id.historyErrorFavourite);
        noResultsFound = (ImageView) findViewById(R.id.noResultsFound);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                final WordsEntity obj = greenAdapter.getItem((int) viewHolder.getAdapterPosition());
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        wordsDb.wordsDao().deleteWords(obj);
                    }
                });
                greenAdapter.deleteWord(obj);
                toggle();
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        deleteFile(obj);
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void toggle() {
        if (greenAdapter.getItemCount() == 0) {
            if (!getCheckedPreference()) {
                showEmptyStateAll();
            } else {
                showEmptyStateFavorite();
            }
        } else {
            showRecyclerView();
        }
    }

    private void showEmptyStateFavorite() {

        historyError.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noResultsFound.setVisibility(View.GONE);
        historyErrorFavourite.setVisibility(View.VISIBLE);
    }

    private void showEmptyStateAll() {

        historyError.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        historyErrorFavourite.setVisibility(View.GONE);
        noResultsFound.setVisibility(View.GONE);

    }

    private void showRecyclerView() {
        historyError.setVisibility(View.GONE);
        noResultsFound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        historyErrorFavourite.setVisibility(View.GONE);
    }

    private void showEmptyStateNoResultsFound() {

        historyError.setVisibility(View.GONE);
        noResultsFound.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        historyErrorFavourite.setVisibility(View.GONE);
    }

    private void deleteFile(WordsEntity obj) {
        String url = obj.getPronunciationUrl();
        if (!TextUtils.isEmpty(url)) {
            File fileToDelete = new File(getAudioFolderUrl(), getFileName(url));
            if (fileToDelete.exists())
                fileToDelete.delete();
        }
    }

    private void updateUI(final List<WordsEntity> savedData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                greenAdapter.setWords(savedData);
                toggle();
            }
        });
    }

    @Override
    public void onClickButton(int id) {

        String word = greenAdapter.getItemWord(id);
        Intent intent = new Intent(SavedWords.this, Details.class);
        intent.putExtra(Intent.EXTRA_TEXT, word);
        startActivity(intent);
    }

    @Override
    public void onClickFavourite(final int id) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                WordsEntity savedResult = wordsDb.wordsDao().loadWordByName(GoogleFetchInfoFull.convert(greenAdapter.getItemWord(id)));
                savedResult.setIsStarred(!savedResult.getIsStarred());
                wordsDb.wordsDao().updateWords(savedResult);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_history)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search a historic word");
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                //Log.d(TAG, "query text changed");
                greenAdapter.getFilter().filter(query, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if (greenAdapter.getItemCount() == 0) {
                            //Log.d(TAG, "triggered");
                            showEmptyStateNoResultsFound();
                        }
                        else{
                            showRecyclerView();
                        }
                    }
                });

                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                toggle();
                return false;
            }
        });

        sharedPref = SavedWords.this.getPreferences(Context.MODE_PRIVATE);
        boolean checked = getCheckedPreference();
        if (checked) {
            showStarred();
        } else {
            showAll();
        }
        menu.findItem(R.id.favourite_history).setChecked(checked);
        return true;
    }

    private boolean getCheckedPreference() {
        boolean checked = sharedPref.getBoolean(getString(R.string.golden_key), false);
        return checked;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_history) {
            return true;
        } else if (id == R.id.favourite_history) {
            if (!item.isChecked()) {
                item.setChecked(true);
                showStarred();
                applyPersistence(true);
            } else {
                item.setChecked(false);
                showAll();
                applyPersistence(false);
            }
        }
        else if(id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyPersistence(boolean flag) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.golden_key), flag);
        editor.apply();
    }

    private void showStarred() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<WordsEntity> savedData = wordsDb.wordsDao().loadAllStarredWords(true);
                updateUI(savedData);
            }
        });
    }

    private void showAll() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<WordsEntity> savedData = wordsDb.wordsDao().loadAllWords();
                updateUI(savedData);
            }
        });
    }

    public String getFileName(String link) {
        String FILE_NAME = Uri.parse(link).getLastPathSegment();
        return FILE_NAME;
    }

    public String getAudioFolderUrl() {

        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + File.separator + FOLDER_NAME;
        return folder;
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


}
