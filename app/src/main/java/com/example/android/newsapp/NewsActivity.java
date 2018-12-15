package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.view.Gravity;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    public static final String LOG_TAG = NewsActivity.class.getName();
    // URL for article data from the Guardian News API
    private static final String GUARDIAN_API_URL = "https://content.guardianapis.com/search?";
    private static final String API_TEST_KEY = "ca0f7726-275e-4609-bb9e-0f31ff819dd6";
    // Adapter for the list of articles
    private ArticleAdapter mAdapter;
    // Constant value for the book loader ID.
    private static final int ARTICLE_LOADER_ID = 11;

    private TextView mEmptyTextView;
    private ProgressBar mProgressView;
    private ArrayList<Article> articleArrayList = new ArrayList<>();
    LoaderManager loaderManager;
    ConnectivityManager cm;
    NetworkInfo activeNetwork;
    private SwipeRefreshLayout mSwipeRefresh;
//    Uri baseUri = Uri.parse(GUARDIAN_API_URL);
//    Uri.Builder uriBuilder = baseUri.buildUpon();
    String mselectCategory = "";
    String query = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        String selectCategory = "world";

        // Find a reference to the {@link ListView} in the layout
        ListView articleListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of articles as input
        mAdapter = new ArticleAdapter(this, articleArrayList, selectCategory);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleListView.setAdapter(mAdapter);

        // Set a custom message when there are no list items
        mEmptyTextView = (TextView) findViewById(R.id.empty_view_text);
        View emptyLayoutView = findViewById(R.id.empty_layout_view);
        articleListView.setEmptyView(emptyLayoutView);

        mProgressView = (ProgressBar) findViewById(R.id.progress);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website where a user can view the article.
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current article that was clicked on
                Article currentArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        articleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // TODO Auto-generated method stub

                Toast.makeText(NewsActivity.this, "News Saved in Favourites.", Toast.LENGTH_LONG).show();
                return true;
            }

        });

        if (isConnected()) {

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
            Log.i(LOG_TAG, "Loader on init");
        } else {
            mProgressView.setVisibility(View.GONE);
            mEmptyTextView.setText(R.string.no_internet);
        }

        // Reload the API if the user refreshes the activity
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                        refreshView();
                    }
                }
        );
    }

    private boolean isConnected() {
        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void refreshView() {
        // Restart the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.restartLoader(ARTICLE_LOADER_ID, null, NewsActivity.this);
        Log.i(LOG_TAG, "Loader on refresh");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loaderManager.restartLoader(ARTICLE_LOADER_ID, null, NewsActivity.this);
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the given URL
        Log.i(LOG_TAG, "Loader on create");

        Bundle extras = getIntent().getExtras();
        String selectCategory = "";
        if (extras != null) {
            selectCategory = extras.getString("selectCategry");
        }
        else {
            selectCategory = "world";
        }

        mselectCategory = selectCategory;

//        Toast toast = Toast.makeText(NewsActivity.this, selectCategory, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String noOfArticles = sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));

        Uri baseUri = Uri.parse(GUARDIAN_API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //https://content.guardianapis.com/search?tag=politics/politics&api-key=test

//        uriBuilder.appendQueryParameter("q", selectCategory);
        if (extras != null && extras.containsKey("query")){
            query = extras.getString("query");
        }
        if (query.equals("none")) {
            uriBuilder.appendQueryParameter("section", selectCategory);

        }
        else {
            uriBuilder.appendQueryParameter("q", query);
            NewsActivity.this.setTitle(query);
        }
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("show-fields", "all");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", noOfArticles);
        uriBuilder.appendQueryParameter("api-key", API_TEST_KEY);
        Log.v(LOG_TAG, uriBuilder.toString());
//        Toast.makeText(NewsActivity.this, uriBuilder.toString(), Toast.LENGTH_LONG).show();

        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {

        // Finish refreshing
        mSwipeRefresh.setRefreshing(false);
        // Clear the adapter of previous article data
        mAdapter.clear();

        if (!isConnected()) {
            // Set empty state text to display "No connection."
            mProgressView.setVisibility(View.GONE);
            mEmptyTextView.setText(R.string.no_internet);
        } else {
            // If there is a valid list of {@link Articles}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (articles != null && !articles.isEmpty()) {
                mAdapter.addAll(articles);
            } else {
                // Set empty state text to display "No articles found."
                mProgressView.setVisibility(View.GONE);
                mEmptyTextView.setText(R.string.no_articles);
            }
        }
        Log.i(LOG_TAG, "Loader on finished");

    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "Loader on reset");
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Add SearchWidget.
        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE );
        SearchView searchView = (SearchView) menu.findItem( R.id.search ).getActionView();

        searchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );

//


        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
//                Toast.makeText(NewsActivity.this, query, Toast.LENGTH_LONG).show();
                Intent i= new Intent(NewsActivity.this,NewsActivity.class);
                i.putExtra("query",query);
                startActivity(i);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Check if user triggered a refresh:

//            case R.id.search:
//                Intent searchIntent = new Intent(this, SearchActivity.class);
//                startActivity(searchIntent);

            case R.id.menu_categories:
                Intent categoriesIntent = new Intent(this, CategoriesActivity.class);
                startActivity(categoriesIntent);
                //Log.i(LOG_TAG, "Categories menu item selected");
                // Signal SwipeRefreshLayout to start the progress indicator
                //mSwipeRefresh.setRefreshing(true);
                //refreshView();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
