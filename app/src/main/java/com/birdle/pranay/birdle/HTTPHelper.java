package com.birdle.pranay.birdle;

import android.util.Log;

import org.apache.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Victor on 9/3/2015.
 */
public class HTTPHelper {

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        String Failiure_message = "search attempt failed";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
                throw new Exception(Failiure_message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String GET(String url, String title, String value){
        InputStream inputStream = null;
        String result = "";
        String Failiure_message = "search attempt failed";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpGetter = new HttpGet(url);
            httpGetter.addHeader(title, value);

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpGetter);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
                throw new Exception(Failiure_message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String POST(String url, String title, String value, String body){
        InputStream inputStream = null;
        String result = "";
        String Failiure_message = "search attempt failed";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

//            HttpGet httpGetter = new HttpGet(url);
            HttpPost httpPoster = new HttpPost(url);

            httpPoster.addHeader(title, value);
            httpPoster.addHeader("Content-Type", "application/json");

            StringEntity bodyEntity = new StringEntity(body);
            httpPoster.setEntity(bodyEntity);

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPoster);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
                throw new Exception(Failiure_message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


}
