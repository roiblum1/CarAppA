package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private Animation anim1;
    /* access modifiers changed from: private */
    public Animation anim2;
    /* access modifiers changed from: private */
    public Animation anim3;
    /* access modifiers changed from: private */
    public ImageView chmaraTech;
    /* access modifiers changed from: private */
    public ImageView logoSplash;
    /* access modifiers changed from: private */
    public ImageView logoWhite;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_splash_screen);
        this.logoSplash = (ImageView) findViewById(R.id.ivLogoSplash);
        this.logoWhite = (ImageView) findViewById(R.id.ivLogoWhite);
        this.chmaraTech = (ImageView) findViewById(R.id.ivCHTtext);
        this.anim1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        this.anim2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadeout);
        this.anim3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadein);
        this.logoSplash.startAnimation(this.anim1);
        this.anim1.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                SplashScreen.this.logoSplash.startAnimation(SplashScreen.this.anim2);
                SplashScreen.this.logoSplash.setVisibility(View.GONE);
                SplashScreen.this.logoWhite.startAnimation(SplashScreen.this.anim3);
                SplashScreen.this.chmaraTech.startAnimation(SplashScreen.this.anim3);
                SplashScreen.this.anim3.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        SplashScreen.this.logoWhite.setVisibility(View.VISIBLE);
                        SplashScreen.this.chmaraTech.setVisibility(View.VISIBLE);
                        SplashScreen.this.finish();
                        SplashScreen.this.startActivity(new Intent(SplashScreen.this, PersonalPage.class));
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}