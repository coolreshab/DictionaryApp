package com.coolreshab.dictionary;

import java.util.ArrayList;

public class GoogleFetchInfo {
    public String partOfSpeech;
    public String definition;
    public String example;
    public ArrayList<String>synonyms;

    public GoogleFetchInfo() {
        this.partOfSpeech = "";
        this.definition = "";
        this.example = "";
        this.synonyms = new ArrayList<String>();
    }
}
