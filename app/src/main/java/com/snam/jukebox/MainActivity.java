package com.snam.jukebox;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private boolean auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = true;

        TextView textView = (TextView)findViewById(R.id.welcomeTV);
        TextView textView1 = (TextView)findViewById(R.id.serverKey);
        EditText editText = (EditText)findViewById(R.id.serverKeyEntry);


        Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mLoadAnimation.setDuration(3000);
        textView.startAnimation(mLoadAnimation);
        textView1.startAnimation(mLoadAnimation);
        editText.startAnimation(mLoadAnimation);


    }

    public void nextActivity(View view)
    {
        if(auth == true)
        {
            Intent i = new Intent(this, home.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
