package com.coolreshab.dictionary.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface WordsDao {

    @Query("SELECT * FROM words ORDER BY ticks desc")
    List<WordsEntity> loadAllWords();

    @Query("SELECT * FROM words where isStarred=:val ORDER BY ticks desc")
    List<WordsEntity> loadAllStarredWords(boolean val);

    @Query("SELECT * FROM words where wordName=:name")
    WordsEntity loadWordByName(String name);

    @Query("update words set ticks=:time , wordDetails=:json where wordName=:name")
    void updateTicks_Json(Date time, String json,String name);

    @Insert
    void insertWords(WordsEntity wordEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWords(WordsEntity wordEntry);

    @Delete
    void deleteWords(WordsEntity wordEntry);
}
