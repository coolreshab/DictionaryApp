package com.example.dictionary;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
