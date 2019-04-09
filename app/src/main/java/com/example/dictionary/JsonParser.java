package com.example.dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonParser {

    public  static ArrayList<String>dataMuseParser(String dataMuseJson) {

        ArrayList<String>dataMuseResults=new ArrayList<String>();
        try {
            JSONArray dataMuseArray=new JSONArray(dataMuseJson);
            for(int i=0;i<dataMuseArray.length();++i){
                JSONObject temp=dataMuseArray.getJSONObject(i);
                if(temp.has("word")){
                    String word=temp.get("word").toString();
                    if(word.length()<=20)
                        dataMuseResults.add(word);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataMuseResults;
    }

    public static GoogleFetchInfoFull googleDictionaryJsonParser(String googleJson){
        GoogleFetchInfoFull googleResults=new GoogleFetchInfoFull();
        try{
            JSONArray googleArray=new JSONArray(googleJson);
            JSONObject googleObject =googleArray.getJSONObject(0);
            if(googleObject.has("word"))
                googleResults.wordName=googleObject.getString("word");
            if(googleObject.has("phonetic"))
                googleResults.phonetic=googleObject.getString("phonetic");
            if(googleObject.has("pronunciation"))
                googleResults.pronunciation=googleObject.getString("pronunciation");
            if(googleObject.has("meaning")){
                googleObject=googleObject.getJSONObject("meaning");
                Iterator<String> iter = googleObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    JSONArray detail=googleObject.getJSONArray(key);
                    for(int i=0;i<detail.length();++i){

                        JSONObject temp=detail.getJSONObject(i);
                        GoogleFetchInfo obj=new GoogleFetchInfo();
                        obj.partOfSpeech=key;
                        if(temp.has("definition"))
                            obj.definition=temp.getString("definition");
                        if(temp.has("example"))
                            obj.example=temp.getString("example");
                        if(temp.has("synonyms")){
                            JSONArray temp1=temp.getJSONArray("synonyms");
                            for(int j=0;j<temp1.length();++j){
                                obj.synonyms.add(temp1.getString(j));
                            }
                        }
                        googleResults.meaning.add(obj);
                    }
                }

            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return googleResults;
    }
}
