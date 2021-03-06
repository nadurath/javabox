package com.snam.jukebox;

        import android.app.Activity;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import com.spotify.sdk.android.player.Player;

        import java.io.IOException;
        import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.ArrayList;

        import kaaes.spotify.webapi.android.models.Track;

public class SongAdapter extends ArrayAdapter<Track>
{
    Context context;
    int layoutResourceId;
    ArrayList<Track> songs = null;

    public SongAdapter(Context context, int layoutResourceId, ArrayList<Track> data)
    {
        super(context,layoutResourceId,data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.songs = data;
        Log.d("data",""+songs.toString());
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View row = convertView;
        SongClass song = null;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            song = new SongClass();
            song.artist = (TextView)row.findViewById(R.id.artist);
            song.album = (TextView)row.findViewById(R.id.album);
            song.title = (TextView)row.findViewById(R.id.title);
            song.length = (TextView)row.findViewById(R.id.length);
            //song.queuePosition = (TextView)row.findViewById(R.id.idk);
            song.rel = (RelativeLayout)row.findViewById(R.id.cell_layout);
            //song.image = (ImageView)row.findViewById(R.id.artwork);
            row.setTag(song);

        }
        else
        {
            song = (SongClass)row.getTag();
        }
        String title,artist,album,length,queuePosition = "Data not found";
        Track songInfo = songs.get(position);
        title = songInfo.name;
        artist = songInfo.artists.get(0).name;
        album = songInfo.album.name;

        long secs = songInfo.duration_ms/1000;
        long secs2 = (songInfo.duration_ms/1000)%60;
        long min = (songInfo.duration_ms/1000)/60;
        String min1 = min + "";
        String sec = secs2 + "";
        if(sec.length() < 2)
            sec = "0" + sec;

        length = min1+":"+sec;
        //queuePosition = songInfo.queuePos;
        artist = artist==null?"Field was null":artist;
        length = length==null?"Field was null":length;
        album = album==null?"Field was null":album;
        title = title==null?"Field was null":title;
        //queuePosition = queuePosition==null?"Field was null":queuePosition;
        song.artist.setText(artist);
        song.album.setText(album);
        song.length.setText(length);
        song.title.setText(title);
        //song.queuePosition.setText(queuePosition);//Sam commented this out


        return row;

    }
    static  class SongClass
    {
        TextView title;
        TextView artist;
        TextView album;
        TextView length;
        //TextView queuePosition;
        RelativeLayout rel;
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }



}