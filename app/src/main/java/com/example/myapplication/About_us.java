package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class About_us extends BaseActivity  {
    Button btn1;
    private CardView cv1;
    private ImageView Image1;
    private CardView cv2;
    private ImageView Image2;
    private CardView cv3;
    private ImageView Image3;
    private CardView cv4;
    private ImageView Image4;
    private CardView cv5;
    private ImageView Image5;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        btn1 = (Button) findViewById(R.id.btn1);
        cv1 = (CardView) findViewById(R.id.cv1);
        Image1 = (ImageView) findViewById(R.id.Image1);
        cv2 = (CardView) findViewById(R.id.cv2);
        Image2 = (ImageView) findViewById(R.id.Image2);
        cv3 = (CardView) findViewById(R.id.cv3);
        Image3 = (ImageView) findViewById(R.id.Image3);
        cv4 = (CardView) findViewById(R.id.cv4);
        Image4 = (ImageView) findViewById(R.id.Image4);
        cv5 = (CardView) findViewById(R.id.cv5);
        Image5 = (ImageView) findViewById(R.id.Image5);
        btn1 = (Button) findViewById(R.id.btn1);

        cv1.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_in));
        cv2.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_in));
        cv3.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_in));
        cv4.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_in));
        cv5.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_in));
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(About_us.this,Register.class));
            }
        });
    }
}