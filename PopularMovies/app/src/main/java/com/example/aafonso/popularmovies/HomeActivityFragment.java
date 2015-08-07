package com.example.aafonso.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomeActivityFragment extends Fragment {

    //Array to hold all movie elements
    private ArrayList<MovieFlavor> movieCollection = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private final int RESULT_SETTINGS = 1;


    public HomeActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(getActivity(), SettingsActivity.class), RESULT_SETTINGS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        updateSorting();

        //Initialize Movie Adapter
        movieAdapter = new MovieAdapter(getActivity(), movieCollection);

        //Get a reference to the gridview, and attach the adpter;
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_movies);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieFlavor movie = movieAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra("com.aafonso.movie", movie);
                startActivity(intent);
            }
        });

        return rootView;

    }


    //Gets the user preferred sorting Preferences
    private void updateSorting() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = sharedPrefs.getString(getString(R.string.pref_sorting_key), getString(R.string.pref_sorting_popular));
        PopularMovieTask popularTask = new PopularMovieTask();
        popularTask.execute(sorting);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == RESULT_SETTINGS) {
            updateSorting();
        }
    }


    private class PopularMovieTask extends AsyncTask<String, Void, ArrayList<MovieFlavor>> {

        private final String LOG_TAG = PopularMovieTask.class.getSimpleName();

        /**
         * @param movieJsonStr JSON string containing Movie Data
         * @throws JSONException
         */
        private ArrayList<MovieFlavor> createMovieObject(String movieJsonStr) throws JSONException {

            movieCollection = new ArrayList<>();

            String mTitle;
            String mImage;
            String mOverview;
            double mRating;
            String mReleaseDate;

            JSONObject jObj = new JSONObject(movieJsonStr);
            JSONArray jArr = jObj.getJSONArray("results");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject jsonObject = jArr.getJSONObject(i);
                mTitle = jsonObject.getString("title");
                mImage = jsonObject.getString("poster_path");
                mOverview = jsonObject.getString("overview");
                mRating = jsonObject.getDouble("vote_average");
                mReleaseDate = jsonObject.getString("release_date");

                MovieFlavor movieFlavor = new MovieFlavor(mTitle, mImage, mOverview, mRating, mReleaseDate);
                movieCollection.add(movieFlavor);
            }

            return movieCollection;
        }

        @Override
        protected ArrayList<MovieFlavor> doInBackground(String... params) {


            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Will contain the raw JSON response as a string
            String movieJsonStr = null;

            try {

                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String API_KEY_QUERY = "api_key";
                final String API_KEY = "9dc4bca87508f043d71ca734d8ebaa80";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0] + ".desc")
                        .appendQueryParameter(API_KEY_QUERY, API_KEY)
                        .build();


                //Create the request to MovieDatabase, and open the connection
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the inputStream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = buffer.toString();


            } catch (IOException e) {

                // If the code didn't successfully get the Movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {

                    }
                }
            }

            try {
                return createMovieObject(movieJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //This will only happen if there was an error getting or parsing the forecast
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieFlavor> result) {
            if (result != null) {
                movieAdapter.clear();
                movieAdapter.addAll(result);
            }
        }

    }
}
