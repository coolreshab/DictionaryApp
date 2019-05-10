package com.coolreshab.dictionary;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    public static String getDataMuseUrl(String query,String type,String param,String max){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.datamuse.com")
                .appendPath(type)
                .appendQueryParameter(param,query)
                .appendQueryParameter("max",max);
        String dataMuseUrl = builder.build().toString();
        return  dataMuseUrl;
    }
    public static String getGoogleDictionaryUrl(String query){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("googledictionaryapi.eu-gb.mybluemix.net")
                .appendQueryParameter("define",query)
                .appendQueryParameter("lang","en");
        String googleDictionaryUrl = builder.build().toString();
        return  googleDictionaryUrl;
    }
    public static String getResponse(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String urlString = "", current;
        while ((current = in.readLine()) != null) {
            urlString += current;
        }
        return urlString;
    }
}
