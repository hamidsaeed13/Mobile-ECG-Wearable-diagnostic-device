package com.mujahid.mobileecg;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by cryst on 10/19/2016.
 *
 */

public class SplashScreen extends Activity {
    ProgressBar progressbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

       ImageView im  = (ImageView) findViewById(R.id.imageView3);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.anim_down);
        im.setAnimation(anim);
        progressbar = (ProgressBar)findViewById(R.id.progressBar);
        ObjectAnimator anim1 = ObjectAnimator.ofInt(progressbar,"progress",0,100);
        anim1.setDuration(4000);
        anim1.setInterpolator(new DecelerateInterpolator());
        anim1.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();

            }
        },1500);
    }
}
