package com.nanodegree.fehr.booklisting;

import android.app.Activity;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Fehr on 16-Aug-17.
 */
public class BookAsyncTaskLoader extends AsyncTaskLoader<List<Book>> {
    /**
     * Tag for log messages
     */
    private final String LOG_TAG_ASYNC_LOADER = BookAsyncTaskLoader.class.getName();
    private static final String BOOK_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";


    private String mSearchInput;
    Activity mActivity;

    public BookAsyncTaskLoader(Activity activity, String searchInput) {
        super(activity);
        mActivity = activity;
        mSearchInput = searchInput;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (mSearchInput.length() == 0) {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mActivity, mActivity.getString(R.string.enter_search_keyword), Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        mSearchInput = mSearchInput.replace(" ", "+");

        // Create URL object
        URL url = new QueryUtils(mActivity).createUrl(BOOK_REQUEST_URL + mSearchInput);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = new QueryUtils(mActivity).makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG_ASYNC_LOADER, "IOException", e);
        }

        // Extract relevant fields from the JSON response and create an ArrayList of Book
        ArrayList<Book> books = new QueryUtils(mActivity).extractBookInfoFromJson(jsonResponse);

        // Return the {@link Event} object as the result fo the {@link BookAsyncTask}
        return books;
    }

}
