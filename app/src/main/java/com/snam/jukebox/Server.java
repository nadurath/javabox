package com.snam.jukebox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Sam on 5/20/2015.
 */
public class Server extends AsyncTask{

    private Context context;
    private TextView statusText;

    public Server(Context context) {
        this.context = context;
        //this.statusText = (TextView) statusText;
    }
    @Override
    protected String doInBackground(Object[] params) {
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(420);
            Socket client = serverSocket.accept();

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */
//            final File f = new File(Environment.getExternalStorageDirectory() + "/"
//                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
//                    + ".jpg");
//
//            File dirs = new File(f.getParent());
//            if (!dirs.exists())
//                dirs.mkdirs();
//            f.createNewFile();

            InputStream inputStream = client.getInputStream();
            String command = convertStreamToString(inputStream);
            serverSocket.close();
            return command;
        } catch (IOException e) {
            Log.e("error", e.getMessage());
            return null;
        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    /**
     * Start activity that can handle the JPEG image
     */
    //@Override
    protected void onPostExecute(String result) {
        if (result != null) {
            //Intent intent = new Intent();
            //context.startActivity(intent);
            Log.d("DATA",result);
        }
    }


}
