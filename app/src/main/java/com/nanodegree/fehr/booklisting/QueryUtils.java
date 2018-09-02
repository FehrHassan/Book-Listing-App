package com.nanodegree.fehr.booklisting;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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


/**
 * Created by Fehr on 16-Aug-17.
 */

public class QueryUtils {

     public static final String LOG_TAG_QUERY = QueryUtils.class.getSimpleName();
     Activity mActivity;
    public QueryUtils(Activity activity)
    {
        mActivity = activity;
    }
    public URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG_QUERY, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    public String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG_QUERY, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG_QUERY, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
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

    public ArrayList<Book> extractBookInfoFromJson(String bookJSON) {
        if(TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        ArrayList<Book> books = new ArrayList<Book>();

        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            if(baseJsonResponse.getInt("totalItems") == 0) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mActivity, mActivity.getString(R.string.result_not_found), Toast.LENGTH_SHORT).show();
                    }
                });

                return null;
            }

            JSONArray itemArray = baseJsonResponse.getJSONArray("items");

            // If there are results in the items array
            for (int i = 0; i< itemArray.length(); i++) {
                // Extract out the cuurent item (which is a book)
                JSONObject cuurentItem = itemArray.getJSONObject(i);
                JSONObject bookInfo = cuurentItem.getJSONObject("volumeInfo");

                // Extract out the title, authors, and description
                String title = bookInfo.getString("title");

                String [] authors = new String[]{};
                JSONArray authorJsonArray = bookInfo.optJSONArray("authors");
                if(authorJsonArray!= null) {
                    ArrayList<String> authorList = new ArrayList<String>();
                    for (int j = 0; j < authorJsonArray.length(); j++) {
                        authorList.add(authorJsonArray.get(j).toString());
                    }
                    authors = authorList.toArray(new String[authorList.size()]);
                }


                String description = "";
                if(bookInfo.optString("description")!=null)
                    description = bookInfo.optString("description");

                String infoLink = "";
                if(bookInfo.optString("infoLink")!=null)
                    infoLink = bookInfo.optString("infoLink");

                books.add(new Book(title, authors, description, infoLink));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG_QUERY, "Problem parsing the book JSON results", e);
        }
        return books;
    }
}
