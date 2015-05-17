package com.snam.jukebox;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Sam on 5/16/2015.
 */
public class Singleton {
    private static Singleton mInstance = null;

    private ArrayList<Track> songs;
    private Track song;

    private Singleton(){
        songs = null;
        song =null;
    }

    public static Singleton getInstance(){//so basically calling this method will get this one class, and we can get and set objects using it.
        if(mInstance == null)
        {
            mInstance = new Singleton();
        }
        return mInstance;
    }

    public ArrayList<Track> getSongs(){
        return this.songs;
    }

    public void setSongs(ArrayList<Track> value){
        songs = value;
    }
    public Track getSong(){
        return this.song;
    }

    public void setSong(Track value){
        song = value;
    }

}
