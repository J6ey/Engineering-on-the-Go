package com.engotg.creator.engotg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class LearnTestActivity extends AppCompatActivity implements View.OnClickListener{

    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 999;
    private CardView leftButton, rightButton, bottomCard;
    private LinearLayout topSet, bottomSet;
    private Button set1, set2, set3, set4, set5;
    private ImageView chemical, wave;
    private Intent intent;
    private int topicVal;
    private String topicTitle;
    private Typeface typeface;
    static Context context;
    private TextView leftText, rightText, bottomText;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        topicTitle = intent.getExtras().getString("title");
        setTitle(topicTitle);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_test_selection);
        context = this;
        typeface = Typeface.createFromAsset(getResources().getAssets(), "fibra_one_light.otf");
        chemical = findViewById(R.id.chemicals);
        wave = findViewById(R.id.waves);
        Glide.with(this).load(R.drawable.chemicals).into(chemical);
        Glide.with(this).load(R.drawable.waves).into(wave);
        chemical.setTag(1);
        wave.setTag(1);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        bottomCard = findViewById(R.id.bottomCard);
        topSet = findViewById(R.id.topSet);
        bottomSet = findViewById(R.id.bottomSet);
        leftText = findViewById(R.id.leftText);
        rightText = findViewById(R.id.rightText);
        bottomText = findViewById(R.id.bottomText);
        leftText.setTypeface(typeface, Typeface.BOLD);
        rightText.setTypeface(typeface, Typeface.BOLD);
        bottomText.setTypeface(typeface, Typeface.BOLD);
        set1 = findViewById(R.id.set1);
        set2 = findViewById(R.id.set2);
        set3 = findViewById(R.id.set3);
        set4 = findViewById(R.id.set4);
        set5 = findViewById(R.id.set5);

        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        bottomCard.setOnClickListener(this);
    }

    public void onSetClick(){
        intent.putExtra("topic", topicVal);
        intent.putExtra("title", topicTitle);
        startActivity(intent);
        rightText.setText("Test Your\nKnowledge");
        topSet.setVisibility(View.INVISIBLE);
        bottomSet.setVisibility(View.INVISIBLE);
    }

    public void onClick(View v){
        intent = getIntent();
        topicVal = intent.getExtras().getInt("topic");
        if(v.getId() == R.id.leftButton){
            if(wave.getTag().equals(1)){
                Glide.with(this).load(R.drawable.waves_yellow).into(wave);
                wave.setTag(2);
            } else {
                Glide.with(this).load(R.drawable.waves).into(wave);

                wave.setTag(1);
            }
            intent = new Intent(this, AudioPlayer.class);
            intent.putExtra("topic", topicVal);
            intent.putExtra("title", topicTitle);
            if(topSet.isShown()){
                rightText.setText("Test Your\nKnowledge");
                topSet.setVisibility(View.INVISIBLE);
                bottomSet.setVisibility(View.INVISIBLE);
            }
            startActivity(intent);
        } else if(v.getId() == R.id.bottomCard) {
            if(topSet.isShown()){
                topSet.setVisibility(View.INVISIBLE);
                bottomSet.setVisibility(View.INVISIBLE);
            }
            finish();
        } else {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
            if(chemical.getTag().equals(1)){
                Glide.with(this).load(R.drawable.chemicals_invert).into(chemical);
                chemical.setTag(2);
            } else {
                Glide.with(this).load(R.drawable.chemicals).into(chemical);
                chemical.setTag(1);
            }

            if(topSet.isShown()){
                rightText.setText("Test Your\nKnowledge");
                topSet.setVisibility(View.INVISIBLE);
                bottomSet.setVisibility(View.INVISIBLE);
            } else {
                rightText.setText("Select a\nTest Set");
                topSet.setVisibility(View.VISIBLE);
                bottomSet.setVisibility(View.VISIBLE);
            }

            intent = new Intent(LearnTestActivity.this, TestActivity.class);
            set1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("set", 1);
                    onSetClick();
                }
            });
            set2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("set", 2);
                    onSetClick();
                }
            });
            set3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("set", 3);
                    onSetClick();
                }
            });
            set4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("set", 4);
                    onSetClick();
                }
            });
            set5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("set", 5);
                    onSetClick();
                }
            });
        }
    }
}
