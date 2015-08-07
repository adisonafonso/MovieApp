package com.example.aafonso.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String MOVIE_POSTER_BASE = "http://image.tmdb.org/t/p/w185";

        Bundle b = getActivity().getIntent().getExtras();
        MovieFlavor movie = b.getParcelable("com.aafonso.movie");
        String url = MOVIE_POSTER_BASE + movie.getImage();
        //Get the Movie Data from previous Activity
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        TextView movieTitle = (TextView) rootView.findViewById(R.id.movieTitle);
        movieTitle.setText(movie.getTitle());

        ImageView moviePoster = (ImageView) rootView.findViewById(R.id.moviePoster);
        Picasso.with(getActivity()).load(url).resize(370,556).into(moviePoster);

        TextView releaseDate = (TextView) rootView.findViewById(R.id.releaseDate);
        releaseDate.setText(movie.getReleaseDate());

        TextView rating = (TextView) rootView.findViewById(R.id.rating);
        rating.setText(Double.toString(movie.getRating()) + "/10.0");

        TextView overViewText = (TextView) rootView.findViewById(R.id.overviewText);
        overViewText.setText(movie.getSummary());

        return rootView;
    }
}
