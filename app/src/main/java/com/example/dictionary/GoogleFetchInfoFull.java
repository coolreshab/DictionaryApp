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
    public static String convert(String query){
        String[] splited = query.split("\\s+");
        query="";
        for(int  i=0;i<splited.length;++i){
            query+=splited[i].toLowerCase();
            if(i!=splited.length-1)
                query+="-";
        }
        return query;
    }
    public static String undoConvert(String query){
        String[] splited = query.split("-");
        query="";
        splited[0]=convertFirstToUpper(splited[0]);
        for(int  i=0;i<splited.length;++i){
            query+=splited[i];
            if(i!=splited.length-1)
                query+=" ";
        }
        return query;
    }
}
