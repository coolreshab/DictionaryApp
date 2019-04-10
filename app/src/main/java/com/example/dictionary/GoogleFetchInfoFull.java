package com.example.dictionary;

import android.text.Html;
import android.text.Spanned;

import java.util.ArrayList;

public class GoogleFetchInfoFull {

    public ArrayList<GoogleFetchInfo>meaning;
    public String wordName;
    public String pronunciation;
    public String phonetic;

    GoogleFetchInfoFull(){
        wordName="";
        pronunciation="";
        phonetic="";
        meaning=new ArrayList<GoogleFetchInfo>();
    }
    public static String convertFirstToUpper(String query){
        return Character.toUpperCase(query.charAt(0))+query.substring(1);
    }
    public static Spanned addEffect(String query,String L,String R){
        String sourceString = L + query + R;
        return Html.fromHtml(sourceString);
    }
}
