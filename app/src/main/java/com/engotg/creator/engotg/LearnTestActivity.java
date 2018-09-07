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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class LearnTestActivity extends AppCompatActivity implements View.OnClickListener{

    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 999;
    private CardView leftButton, rightButton, bottomCard;
    private LinearLayout topSet, bottomSet;
    private Button set1, set2, set3, set4, set5;
    private ImageView chemical, wave;
    private ImageButton settings;
    private Intent intent;
    private int topicVal;
    private String topicTitle;
    private Typeface typeface;
    private ArrayList<int[]> colorComb;
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
        settings = findViewById(R.id.settingsBtn);
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
        initColors();
        settings.setOnClickListener(this);
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
        } else if(v.getId() == R.id.rightButton){
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
        } else {
            startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    public void initColors(){
        colorComb = new ArrayList<>();
        colorComb.add(new int[]{0xffCADBC0, 0xffC94277, 0xff2F0A28});
        colorComb.add(new int[]{0xff34454d, 0xff61e97a, 0xff8a8a8a});
        colorComb.add(new int[]{0xff022B3A, 0xffBFDBF7, 0xff1F7A8C});
        colorComb.add(new int[]{0xff4F6D7A, 0xffC0D6DF, 0xff04A777});
        colorComb.add(new int[]{0xffD81E5B, 0xff23395B, 0xffB9E3C6});
        colorComb.add(new int[]{0xff373F51, 0xff008DD5, 0xffF56476});
        colorComb.add(new int[]{0xffB7ADCF, 0xff22181C, 0xff84DCCF});
        colorComb.add(new int[]{0xff7F95D1, 0xff77625C, 0xff84DCCF});
        colorComb.add(new int[]{0xff32292F, 0xff4F646F, 0xff84DCCF});
        colorComb.add(new int[]{0xff3C3744, 0xff3D52D5, 0xff84DCCF});
        colorComb.add(new int[]{0xff33658A, 0xff86BBD8, 0xff3D52D5});
        colorComb.add(new int[]{0xff8EDCE6, 0xffD5DCF9, 0xff3D52D5});
        colorComb.add(new int[]{0xffD5DCF9, 0xff3D52D5, 0xff84DCCF});
        colorComb.add(new int[]{0xff85BDBF, 0xff57737A, 0xff040F0F});
        colorComb.add(new int[]{0xff93B5C6, 0xffDDEDAA, 0xffBD4F6C});
        colorComb.add(new int[]{0xff011638, 0xff2E294E, 0xff9055A2});
        colorComb.add(new int[]{0xff706C61, 0xffBCD979, 0xff99C5B5});
        colorComb.add(new int[]{0xffACACDE, 0xffABDAFC, 0xff545E75});

        int palette = ThreadLocalRandom.current().nextInt(0, colorComb.size());
//        System.out.println(Integer.toHexString(colorComb.get(palette)[0]));
//        System.out.println(Integer.toHexString(colorComb.get(palette)[1]));
//        System.out.println(Integer.toHexString(colorComb.get(palette)[2]));

        leftButton.setCardBackgroundColor(colorComb.get(palette)[0]);
        rightButton.setCardBackgroundColor(colorComb.get(palette)[1]);
        bottomCard.setCardBackgroundColor(colorComb.get(palette)[2]);
        settings.setBackgroundColor(colorComb.get(palette)[2]);
    }
}
