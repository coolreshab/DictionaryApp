package com.example.dictionary.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WordsDao {

    @Query("SELECT * FROM words ORDER BY wordName")
    List<WordsEntity> loadAllWords();

    @Query("SELECT * FROM words where wordName=:name")
    WordsEntity loadWordByName(String name);

    @Insert
    void insertWords(WordsEntity wordEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWords(WordsEntity wordEntry);

    @Delete
    void deleteWords(WordsEntity wordEntry);
}
