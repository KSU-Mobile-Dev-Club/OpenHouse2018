package com.example.jeremy.myapplication;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TwitterClient {

    private final static String TOKEN = "AAAAAAAAAAAAAAAAAAAAACJl4wAAAAAAHpkA5UaHDodjo01kteq9nB%2Fqjns%3DGxarAYeiInh2pTDK3WvhRwsmrPCVVchsT48sj6CcyUsfPdglvP";
    private final static String ACCOUNT_NAME = "KState";
    private final static int NUM_TWEETS = 20;

    private static String sTweetURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=%s&count=%d";
    private static String sUsersURL = "https://api.twitter.com/1.1/friends/list.json?screen_name=%s";

    public static ArrayList<Tweet> getTweets() {

        HttpsURLConnection connection = null;

        try {
            URL url = new URL(String.format(sTweetURL, ACCOUNT_NAME, NUM_TWEETS));


            JSONArray objs = (JSONArray) getJSONFromURL(url);

            if (objs != null) {
                ArrayList<Tweet> tweets = new ArrayList<Tweet>();

                // Loop through each JSONObject, get Tweet data from it, and create a Tweet to add to the list
                for (int i = 0; i < objs.size(); i++) {
                    JSONObject tweetObj = (JSONObject) objs.get(i);
                    String author = ((JSONObject) tweetObj.get("user")).get("name").toString();
                    String text = tweetObj.get("text").toString();

                    tweets.add(new Tweet(author, text));
                }

                return tweets;
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return null;
    }

    public static ArrayList<User> getUsers() {

        HttpsURLConnection connection = null;

        try {
            URL url = new URL(String.format(sUsersURL, ACCOUNT_NAME));

            JSONArray objs = (JSONArray)((JSONObject)getJSONFromURL(url)).get("users");

            if (objs != null) {
                ArrayList<User> users = new ArrayList<User>();

                // Loop through each JSONObject, get Tweet data from it, and create a Tweet to add to the list
                for (int i = 0; i < objs.size(); i++) {
                    String handle = ((JSONObject)objs.get(i)).get("screen_name").toString();
                    users.add(new User(handle));
                    //String handle = ((JSONArray) userObj.get("users")).get("screen_name").toString();

                    //users.add(new User(handle));
                }

                return users;
            }
        }
        catch (Exception e)
        {
            Log.e("ERROR", "Error retrieving users: " + e.getMessage());
        }
        return null;
    }


    private static Object getJSONFromURL(URL url) throws IOException {
        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + TOKEN);

            disableConnectionReuseIfNecessary();

            return JSONValue.parse(readResponse(connection));
        } catch (MalformedURLException e) {
            throw new IOException("Invalid endpoint URL specified." + e.getMessage());
        } catch (Exception e) {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    /*
    Ya, ripped this bad boy off the internet. It reads the HTTPS response with the JSON Tweet data
     */
    private static String readResponse(HttpsURLConnection connection) {

        try {
            StringBuilder str = new StringBuilder();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpStatus.SC_OK) {
                if (responseCode != -1) {
                    InputStream errorStream = connection.getErrorStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
                    BufferedReader br = new BufferedReader(inputStreamReader);
                    String line = "";
                    while((line = br.readLine()) != null) {
                        str.append(line + System.getProperty("line.separator"));
                    }
                } else {
                    String errorMsg = "Response code = -1, so can't get error stream";
                    str.append(errorMsg);
                }
                return str.toString();
            }
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = "";
            while((line = br.readLine()) != null) {
                str.append(line + System.getProperty("line.separator"));
            }
            return str.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Workaround to fix -1 responseCode on a second call via HttpsUrlConnection.
     * See: http://stackoverflow.com/questions/1440957/httpurlconnection-getresponsecode-returns-1-on-second-invocation
     */
    private static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        // When mindSdk >= 4 use: Build.VERSION.SDK_INT
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }
}
