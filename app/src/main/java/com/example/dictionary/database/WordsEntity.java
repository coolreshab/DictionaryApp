package com.example.dictionary.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "words")
public class WordsEntity {

    //columns
    @NonNull
    @PrimaryKey
    private String wordName;
    private String wordDetails;
    private boolean isStarred;
    private Date ticks;

    public WordsEntity(String wordName, String wordDetails, boolean isStarred, Date ticks) {
        this.wordName = wordName;
        this.wordDetails = wordDetails;
        this.isStarred = isStarred;
        this.ticks = ticks;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public String getWordDetails() {
        return wordDetails;
    }

    public void setWordDetails(String wordDetails) {
        this.wordDetails = wordDetails;
    }

    public boolean getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(boolean starred) {
        isStarred = starred;
    }

    public Date getTicks() {
        return ticks;
    }

    public void setTicks(Date ticks) {
        this.ticks = ticks;
    }

}
