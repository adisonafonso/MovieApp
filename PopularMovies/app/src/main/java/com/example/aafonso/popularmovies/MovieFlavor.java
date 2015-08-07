package com.example.aafonso.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aafonso on 7/20/15.
 * Represents a Movie Object
 */
public class MovieFlavor implements Parcelable {

    private String title;
    private String image;
    private String summary;
    private double rating;
    private String releaseDate;


    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getSummary() {
        return summary;
    }

    public double getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }


    public MovieFlavor(String mName, String mImage, String mSummary, double mRating, String mReleaseDate){
        this.title = mName;
        this.image = mImage;
        this.summary = mSummary;
        this.rating = mRating;
        this.releaseDate = mReleaseDate;
    }

    private MovieFlavor(Parcel in) {
        this.title = in.readString();
        this.image = in.readString();
        this.summary = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(summary);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
    }

    public static final Parcelable.Creator<MovieFlavor> CREATOR = new Parcelable.Creator<MovieFlavor>() {

        @Override
        public MovieFlavor createFromParcel(Parcel source) {
            return new MovieFlavor(source);
        }

        @Override
        public MovieFlavor[] newArray(int size) {
            return new MovieFlavor[size];
        }
    };
}
