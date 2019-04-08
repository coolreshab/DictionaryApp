package com.example.dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParser {

    public  static ArrayList<String>dataMuseParser(String dataMuseJson) {

        ArrayList<String>dataMuseResults=new ArrayList<String>();
        try {
            JSONArray dataMuseArray=new JSONArray(dataMuseJson);
            for(int i=0;i<dataMuseArray.length();++i){
                JSONObject temp=dataMuseArray.getJSONObject(i);
                if(temp.has("word"))
                    dataMuseResults.add(temp.get("word").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataMuseResults;
    }
}
