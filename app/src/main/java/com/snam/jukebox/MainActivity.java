package com.snam.jukebox;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

    private boolean auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    @Override
    public void onResume()
    {
        super.onResume();
        TextView textView = (TextView)findViewById(R.id.welcomeTV);
        TextView textView1 = (TextView)findViewById(R.id.serverKey);
        View b1 = findViewById(R.id.nextAct);
        View b2 = findViewById(R.id.serverButton);
        Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mLoadAnimation.setDuration(1000);
        Animation mLoadAnimation2 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mLoadAnimation2.setDuration(2000);
        Animation mLoadAnimation3 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mLoadAnimation3.setDuration(3000);
        Animation mLoadAnimation4 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mLoadAnimation4.setDuration(4000);
        mLoadAnimation.setInterpolator(new LinearInterpolator());
        mLoadAnimation2.setInterpolator(new LinearInterpolator());
        mLoadAnimation3.setInterpolator(new LinearInterpolator());
        mLoadAnimation4.setInterpolator(new LinearInterpolator());
        textView.startAnimation(mLoadAnimation);
        textView1.startAnimation(mLoadAnimation2);
        b1.startAnimation(mLoadAnimation3);
        b2.startAnimation(mLoadAnimation4);
    }

    public void nextActivity(View view)
    {
        Animation exit = AnimationUtils.loadAnimation(this,android.R.anim.fade_out);
        exit.setDuration(1000);
        exit.setFillAfter(true);
        exit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Intent i = new Intent(getApplicationContext(), homeClient.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        TextView textView = (TextView)findViewById(R.id.welcomeTV);
        TextView textView1 = (TextView)findViewById(R.id.serverKey);
        View b1 = findViewById(R.id.nextAct);
        View b2 = findViewById(R.id.serverButton);

        textView.startAnimation(exit);
        textView1.startAnimation(exit);
        b1.startAnimation(exit);
        b2.startAnimation(exit);

    }

    public void hostServer(View view)
    {
        Intent i = new Intent(this, homeServer.class);
        startActivity(i);
    }


}
