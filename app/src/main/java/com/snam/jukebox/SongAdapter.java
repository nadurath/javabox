package com.snam.jukebox;

        import android.app.Activity;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Color;
        import android.location.Address;
        import android.location.Geocoder;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.snam.jukebox.R;

        import java.io.IOException;
        import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Map;

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
            song.queuePosition = (TextView)row.findViewById(R.id.idk);
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
        title = songInfo.songTitle;
        artist = songInfo.artist;
        album = songInfo.album;
        length = songInfo.duration;
        queuePosition = songInfo.queuePos;
        artist = artist==null?"Field was null":artist;
        length = length==null?"Field was null":length;
        album = album==null?"Field was null":album;
        title = title==null?"Field was null":title;
        queuePosition = queuePosition==null?"Field was null":queuePosition;
        song.artist.setText(artist);
        song.album.setText(album);
        song.length.setText(length);
        song.title.setText(title);
        song.queuePosition.setText(queuePosition);
        /*new Thread(new Runnable() {
            public void run() {
                Bitmap b = loadImageFromNetwork("http://example.com/image.png");
                mImageView.setImageBitmap(b);
                song.image.setImageBitmap(getBitmapFromURL("http://pngimg.com/upload/banana_PNG814.png"));
            }
        }).start();*/

        return row;

    }
    static  class SongClass
    {
        TextView title;
        TextView artist;
        TextView album;
        TextView length;
        TextView queuePosition;
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