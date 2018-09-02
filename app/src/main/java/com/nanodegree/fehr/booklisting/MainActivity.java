package com.nanodegree.fehr.booklisting;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private Parcelable state;
    @BindView(R.id.list_view_items)
    ListView mListView;
    private BooKArrayAdapter mAdapter;
    ArrayList<Book> booksArrayList;
    @BindView(R.id.empty_view)
    TextView emptyTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_search)
    Button mSearchButton;
    @BindView(R.id.edit_text_search)
    EditText mSearchEditText;

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BOOK_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        emptyTextView.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);


        final ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.no_connection));
        }

        final LoaderManager loaderManager = getSupportLoaderManager();

        booksArrayList = new ArrayList<Book>();
        mAdapter = new BooKArrayAdapter(MainActivity.this, booksArrayList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String url = booksArrayList.get(position).getBookInfoLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                    mAdapter.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.GONE);
                    Loader loader = new Loader(MainActivity.this);
                    if (loader != null) {
                        Log.v(LOG_TAG, "loader will be destroyed");
                        loaderManager.destroyLoader(BOOK_LOADER_ID);
                    }
                    loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);
                } else {
                    emptyTextView.setText(getString(R.string.no_connection));
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        String mSearchText = mSearchEditText.getText().toString();
        mSearchEditText.setText("");
        return new BookAsyncTaskLoader(MainActivity.this, mSearchText);

    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> bookList) {
        progressBar.setVisibility(View.GONE);
        mAdapter.clear();
        if (bookList == null || bookList.isEmpty()) {
            emptyTextView.setText(getString(R.string.result_not_found));
            emptyTextView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            return;
        }
        if (!bookList.isEmpty()) {
            emptyTextView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mAdapter.addAll(bookList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

}
