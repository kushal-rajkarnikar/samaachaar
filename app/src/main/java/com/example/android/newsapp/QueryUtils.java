package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsapp.NewsActivity.LOG_TAG;

public class QueryUtils {


    private QueryUtils() {
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Article> extractResponseFromJson(String articleJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject
            JSONObject baseJsonResponse = new JSONObject(articleJSON);

            // Extract the JSONObject associated with the key called "response"
            JSONObject response = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of articles.
            JSONArray resultsArray = response.getJSONArray("results");

            // For each article in the resultsArray, create an {@link Article} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject currentArticle = resultsArray.getJSONObject(i);

                // Extract the value for the key called "sectionName"
                String sectionName = "No Category";
                if (currentArticle.has("sectionName")) {
                    sectionName = currentArticle.getString("sectionName");
                }


                String webUrl = "https://theguardian.com";
                if (currentArticle.has("webUrl")) {
                    webUrl = currentArticle.getString("webUrl");
                }


                String webPublicationDate = "No Date";
                if (currentArticle.has("webPublicationDate")) {
                    webPublicationDate = currentArticle.getString("webPublicationDate");
                }

                // For a given article, extract the JSONObject associated with the
                // key called "fields", which represents a list of fields
                // for that article.
                JSONObject fields = currentArticle.getJSONObject("fields");

                // Extract the value for the key called "headline"
                String headline = "No Title";
                if (fields.has("headline")) {
                    headline = fields.getString("headline");
                }

                // Extract the value for the key called "trailText / subtitle"
                String trailText = "No Subtitle";
                if (fields.has("trailText")) {
                    trailText = fields.getString("trailText");
                }

                // Extract the value for the key called "byline"
                String byline = "Author Unknown";
                if (fields.has("byline")) {
                    byline = fields.getString("byline");
                }

                // Extract the value for the key called "thumbnail"
                String thumbnail = "";
                if (fields.has("thumbnail")) {
                    thumbnail = fields.getString("thumbnail");
                }

                // Create a new {@link Article} object with the title, subtitle, author, date, category, thumbnail
                // and url from the JSON response.
                Article article = new Article(sectionName, webUrl, webPublicationDate, headline, trailText, byline, thumbnail);

                // Add the new {@link Article} to the list of books.
                articles.add(article);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of articles
        return articles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Query the Guardian articles dataset and return a list of {@link Article} objects.
     */
    public static List<Article> fetchArticleData(String requestUrl) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "Fetch article data");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Article}s
        List<Article> articles = extractResponseFromJson(jsonResponse);

        // Return the list of {@link Articles}s
        return articles;
    }
}
