package com.example.dictionary.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;


@Database(entities = {WordsEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class WordsDb extends RoomDatabase {

    private static final String LOG_TAG = WordsDb.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "wordsDb";
    private static WordsDb sInstance;

    public static WordsDb getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        WordsDb.class, WordsDb.DATABASE_NAME)
                        // Queries should be done in a separate thread to avoid locking the UI
                        // We will allow this ONLY TEMPORALLY to see that our DB is working
                        .allowMainThreadQueries()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract WordsDao wordsDao();
}
