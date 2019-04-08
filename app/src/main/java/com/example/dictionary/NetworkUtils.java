package com.example.dictionary;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    public static String getDataMuseUrl(String query){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.datamuse.com")
                .appendPath("words")
                .appendQueryParameter("sp",query)
                .appendQueryParameter("max","1000");
        String dataMuseUrl = builder.build().toString();
        return  dataMuseUrl;
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
