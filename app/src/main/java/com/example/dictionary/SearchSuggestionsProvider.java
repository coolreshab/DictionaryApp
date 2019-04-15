package com.example.dictionary;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SearchSuggestionsProvider extends ContentProvider{

    private String TAG=SearchSuggestionsProvider.class.getSimpleName();
    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,String selection,String[] selectionArgs,String sortOrder) {

        String query=uri.getLastPathSegment();
        //Log.d(TAG,query);
        if(query.equals("search_suggest_query"))
            return null;
        String url=NetworkUtils.getDataMuseUrl(query,"sug","s","15");
        String response="";
        try {
            response=NetworkUtils.getResponse(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ArrayList<String>suggestions=JsonParser.dataMuseParser(response);
        String[] columns = {
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA
          };

        MatrixCursor cursor = new MatrixCursor(columns);
        for (int i = 0; i < suggestions.size(); i++)
        {
            String[] tmp = {Integer.toString(i), suggestions.get(i),suggestions.get(i)};
            cursor.addRow(tmp);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri,ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
