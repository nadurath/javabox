package com.snam.jukebox;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Sam on 5/20/2015.
 */
public class Server extends AsyncTask<String,Void , String>{

    private Context context;
    Activity activity;
    public Server(Activity mActivity) {
        activity = mActivity;
    }
    @Override
    protected String doInBackground(String[] s) {
        try {
            Log.d("server","Server started");
            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(4206);
            Socket client = serverSocket.accept();

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */

            InputStream inputStream = client.getInputStream();
            String command = convertStreamToString(inputStream);
            serverSocket.close();
            return command;
        } catch (IOException e) {
            e.printStackTrace();
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
            //((homeServer)activity).toastString(result);
            Log.d("DATA", result);
            ((homeServer)activity).searchAndPlay(result);
        }
        ((homeServer)activity).restartServer();
        Log.d("server","Server ended");
    }


}
