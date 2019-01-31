package com.scenekey.model;

import java.io.Serializable;

/**
 * Created by mindiii on 24/4/17.
 */

public class Artists implements Serializable {
    private String artist_name;
    private String artist_id;
    private String rating;

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Artists{" +
                "artist_name='" + artist_name + '\'' +
                ", artist_id='" + artist_id + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }

}
