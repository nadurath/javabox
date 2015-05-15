package com.snam.jukebox;

import android.graphics.Bitmap;

public class Track {
    String songTitle;
    String artist;
    String album;
    String duration;
    String queuePos;
    Bitmap albumArt;


    public Track()
    {
        songTitle = "null";
        artist = "null";
        album = "null";
        duration = "null";
        queuePos = "null";
        albumArt = null;
    }

}
