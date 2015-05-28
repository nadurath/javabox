package com.snam.jukebox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.util.Map;

/**
 * Created by britt296756 on 4/21/2015.
 */
public class Vinyl {
    private View view;
    private Context context;
    private Bitmap oRecord;
    private Bitmap record;
    private Bitmap art;
    private Animation currentAnimation;
    public Vinyl(Context c,View v) {
        Bitmap unMutableBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.record3);
        oRecord = unMutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
        context = c;
        view = v;
        changeArt(BitmapFactory.decodeResource(c.getResources(), R.drawable.record3));

    }

    private void setArt()
    {
        ((ImageView)view).setImageBitmap(art);
    }
    private void animate()
    {

        if(currentAnimation!=null) {
                        currentAnimation.cancel();
                        currentAnimation = new TranslateAnimation(0, 0, 0, 800);
                        currentAnimation.setDuration(1000);
                        currentAnimation.setRepeatCount(0);
                        view.startAnimation(currentAnimation);
                        currentAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation arg0) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation arg0) {
                            }

                            @Override
                            public void onAnimationEnd(Animation arg0) {
                                setArt();
                                currentAnimation = new TranslateAnimation(0, 0, 800, 0);
                                currentAnimation.setDuration(1000);
                                currentAnimation.setFillAfter(true);
                                currentAnimation.setFillBefore(true);
                                currentAnimation.setRepeatCount(0);
                                view.startAnimation(currentAnimation);
                                currentAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation arg0) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation arg0) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation arg0) {
                                        currentAnimation = new RotateAnimation(view.getRotation(), 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                        currentAnimation.setDuration(10000);
                                        currentAnimation.setRepeatCount(-1);
                                        currentAnimation.setInterpolator(new LinearInterpolator());
                                        currentAnimation.setFillAfter(true);
                                        view.startAnimation(currentAnimation);
                                    }
                                });
                            }
                        });
        }
        else {
            currentAnimation = new RotateAnimation(view.getRotation(), 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            currentAnimation.setDuration(5000);
            currentAnimation.setRepeatCount(-1);
            currentAnimation.setFillAfter(true);
            currentAnimation.setFillBefore(true);
            currentAnimation.setInterpolator(new LinearInterpolator());
            view.startAnimation(currentAnimation);
        }
    }
    public void changeArt(Bitmap x)
    {
        Bitmap unArt;
        record = oRecord.copy(oRecord.getConfig(),true);
        unArt = x;
        Bitmap art = unArt.copy(Bitmap.Config.ARGB_8888, true);
        art = Bitmap.createScaledBitmap(art,record.getWidth(), record.getHeight(),false);
        decorate(art);
    }

    public void stop()
    {
        view.clearAnimation();
    }
    private void decorate(Bitmap b)
    {

        int[] pixels = new int[record.getHeight() * record.getWidth()];
        record.getPixels(pixels, 0, record.getWidth(), 0, 0, record.getWidth(), record.getHeight());
        int[] artPixels = new int[b.getHeight() * b.getWidth()];
        b.getPixels(artPixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
        for (int i = 0; i < pixels.length; i++)
            if (Color.green(pixels[i]) > 5 && Color.blue(pixels[i]) < 90 && Color.red(pixels[i]) < 90)
                pixels[i] = Color.argb(Color.alpha(pixels[i]), Color.red(artPixels[i]), Color.green(artPixels[i]), Color.blue(artPixels[i]));
        record.setPixels(pixels, 0, record.getWidth(), 0, 0, record.getWidth(), record.getHeight());
        art = record;
        animate();
    }

    private class Decorate extends AsyncTask<Bitmap, Void,Bitmap> {//auth

        protected Bitmap doInBackground(Bitmap... s) {
            Bitmap art = s[0];
            int[] pixels = new int[record.getHeight() * record.getWidth()];
            record.getPixels(pixels, 0, record.getWidth(), 0, 0, record.getWidth(), record.getHeight());
            int[] artPixels = new int[art.getHeight() * art.getWidth()];
            art.getPixels(artPixels, 0, art.getWidth(), 0, 0, art.getWidth(), art.getHeight());
            for (int i = 0; i < pixels.length; i++)
                if (Color.green(pixels[i]) > 5 && Color.blue(pixels[i]) < 90 && Color.red(pixels[i]) < 90)
                    pixels[i] = Color.argb(Color.alpha(pixels[i]), Color.red(artPixels[i]), Color.green(artPixels[i]), Color.blue(artPixels[i]));
            record.setPixels(pixels, 0, record.getWidth(), 0, 0, record.getWidth(), record.getHeight());
            return record;
        }

        protected void onPostExecute(Bitmap out) {
            Log.d("art", "done");
            art = out;
            animate();
        }
    }

}
