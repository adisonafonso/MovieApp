package com.example.aafonso.popularmovies;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by aafonso on 7/16/15.
 */
public class MovieAdapter extends ArrayAdapter<MovieFlavor> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    int screenWidth;



    /**
     *
     * @param context Current Context. Used to inflate the layout
     * @param movieFlavors A list of MovieFlavors to be displayed in the GridView
     */
    public MovieAdapter(Context context, ArrayList<MovieFlavor> movieFlavors) {
        super(context,0, movieFlavors);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    /**
     *
     * @param position AdapterView position that is requesting aview
     * @param convertView
     * @param parent
     * @return The view for the position in the AdapterView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieFlavor movieFlavor = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_home, parent, false);
        }

        ImageView moviePoster = (ImageView) convertView.findViewById(R.id.grid_item_poster);

        final String MOVIE_POSTER_BASE = "http://image.tmdb.org/t/p/w185";
        String url = MOVIE_POSTER_BASE + movieFlavor.getImage();

        //Keep the ratio of images when loading into the view
        Picasso.with(getContext()).load(url).resize(screenWidth/2,(int)((screenWidth/2)/.70)).into(moviePoster);
        return convertView;
    }

}
