package com.engotg.creator.engotg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView topic_1, topic_2, topic_3;
    private TextView topicText_1, topicText_2, topicText_3;
    private static TextView[] topicTime, topicQuiz;
    private static ImageView[] audioSymbol, bookSymbol;
    private ImageView greenMicro, blueMicro, purpMicro;
    private static final String topics[] = {"Internal External Forces", "Forces Moments", "Internal Forces Stresses"};
    private Typeface typeface;
    private boolean validated;
    private Context context;
    private ArrayList<int[]> colorComb;

    protected void onCreate(Bundle savedInstanceState) {
        Paper.init(this);
        setTitle("Select a topic");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validated = false;
        context = this;
        typeface = Typeface.createFromAsset(getResources().getAssets(), "fibra_one_regular.otf");
        greenMicro = findViewById(R.id.green_micro);
        blueMicro = findViewById(R.id.blue_micro);
        purpMicro = findViewById(R.id.purple_micro);
        Glide.with(this).load(R.drawable.scope_micro).into(greenMicro);
        Glide.with(this).load(R.drawable.gears_micro).into(blueMicro);
        Glide.with(this).load(R.drawable.chem_micro).into(purpMicro);
        topicText_1 = findViewById(R.id.topic_1_text);
        topicText_2 = findViewById(R.id.topic_2_text);
        topicText_3 = findViewById(R.id.topic_3_text);
        topicTime = new TextView[3];
        audioSymbol = new ImageView[3];
        topicQuiz = new TextView[3];
        bookSymbol = new ImageView[3];
        topicTime[0] = findViewById(R.id.topic_1_audio_length);
        topicTime[1] = findViewById(R.id.topic_2_audio_length);
        topicTime[2] = findViewById(R.id.topic_3_audio_length);
        audioSymbol[0] = findViewById(R.id.audio_1);
        audioSymbol[1] = findViewById(R.id.audio_2);
        audioSymbol[2] = findViewById(R.id.audio_3);
        topicQuiz[0] = findViewById(R.id.topic_1_quiz_length);
        topicQuiz[1] = findViewById(R.id.topic_2_quiz_length);
        topicQuiz[2] = findViewById(R.id.topic_3_quiz_length);
        bookSymbol[0] = findViewById(R.id.quiz_1);
        bookSymbol[1] = findViewById(R.id.quiz_2);
        bookSymbol[2] = findViewById(R.id.quiz_3);
        topicTime[0].setTypeface(typeface);
        topicTime[1].setTypeface(typeface);
        topicTime[2].setTypeface(typeface);
        topicQuiz[0].setTypeface(typeface);
        topicQuiz[1].setTypeface(typeface);
        topicQuiz[2].setTypeface(typeface);
        topicText_1.setTypeface(typeface);
        topicText_2.setTypeface(typeface);
        topicText_3.setTypeface(typeface);
        topic_1 = findViewById(R.id.topic_1);
        topic_2 = findViewById(R.id.topic_2);
        topic_3 = findViewById(R.id.topic_3);
        setMetaText();
        initColors();
        topic_1.setOnClickListener(this);
        topic_2.setOnClickListener(this);
        topic_3.setOnClickListener(this);
    }

    protected void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent().putExtra("validated", true));
        setMetaText();
    }

    public static void setMetaText(){
        for (int i = 0; i < topics.length; i++) {
            if(Paper.book().read(topics[i] + " min") != null &&
                    Paper.book().read(topics[i] + " sec") != null){
                double mins = (Integer) Paper.book().read(topics[i] + " min");
                int secMins = (Integer) Paper.book().read(topics[i] + " sec") / 60;
                double otherMins = Double.valueOf(Paper.book().read(topics[i] + " sec").toString()) / 60;
                double dec = otherMins - Math.floor(otherMins);
                double finalMins = (double) Math.round(dec * 10) / 10;

                double totalMins = mins + secMins + finalMins;
                topicTime[i].setText(totalMins + " Mins");
            } else {
                topicTime[i].setVisibility(View.GONE);
                audioSymbol[i].setVisibility(View.GONE);
            }
            if(Paper.book().read(topics[i] + " qtn") != null){
                topicQuiz[i].setText(Paper.book().read(topics[i] + " qtn") + " Qtns");
            } else {
                topicQuiz[i].setVisibility(View.GONE);
                bookSymbol[i].setVisibility(View.GONE);
            }
        }
    }

    public void initColors(){
        colorComb = new ArrayList<>();
        colorComb.add(new int[]{0xffCADBC0, 0xffC94277, 0xff2F0A28});
        colorComb.add(new int[]{0xffBFDBF7, 0xff022B3A, 0xff1F7A8C});
        colorComb.add(new int[]{0xff373F51, 0xff008DD5, 0xffF56476});
        colorComb.add(new int[]{0xffB7ADCF, 0xff22181C, 0xff84DCCF});
        colorComb.add(new int[]{0xff7F95D1, 0xff77625C, 0xff84DCCF});
        colorComb.add(new int[]{0xff32292F, 0xff4F646F, 0xff84DCCF});
        colorComb.add(new int[]{0xff3C3744, 0xff3D52D5, 0xff84DCCF});
        colorComb.add(new int[]{0xff33658A, 0xff86BBD8, 0xff3D52D5});
        colorComb.add(new int[]{0xffD5DCF9, 0xff8EDCE6, 0xff3D52D5});
        colorComb.add(new int[]{0xffD5DCF9, 0xff3D52D5, 0xff84DCCF});
        colorComb.add(new int[]{0xff85BDBF, 0xff57737A, 0xff040F0F});
        colorComb.add(new int[]{0xff93B5C6, 0xffDDEDAA, 0xffBD4F6C});
        colorComb.add(new int[]{0xff011638, 0xff2E294E, 0xff9055A2});
        colorComb.add(new int[]{0xffACACDE, 0xffABDAFC, 0xff545E75});
        colorComb.add(new int[]{0xff1e96ff, 0xff32b964, 0xff9553f1}); // first run
        colorComb.add(new int[]{0xffE9D758, 0xff297373, 0xff39393A});
        colorComb.add(new int[]{0xff52154E, 0xff111344, 0xffE6E6E6});
        colorComb.add(new int[]{0xff1F2041, 0xff4B3F72, 0xffE6E6E6});
        colorComb.add(new int[]{0xffA3D9FF, 0xff96E6B3, 0xff7E6B8F});

        if(Paper.book().read("firstRun") == null){
            Paper.book().write("firstRun", false);
            greenMicro.setBackgroundColor(0xff1e96ff);
            blueMicro.setBackgroundColor(0xff32b964);
            purpMicro.setBackgroundColor(0xff9553f1);
        } else {
            int palette = ThreadLocalRandom.current().nextInt(0, colorComb.size());
//        System.out.println(Integer.toHexString(colorComb.get(palette)[0]));
//        System.out.println(Integer.toHexString(colorComb.get(palette)[1]));
//        System.out.println(Integer.toHexString(colorComb.get(palette)[2]));
            greenMicro.setBackgroundColor(colorComb.get(palette)[0]);
            blueMicro.setBackgroundColor(colorComb.get(palette)[1]);
            purpMicro.setBackgroundColor(colorComb.get(palette)[2]);
        }
    }

    public void onClick(View v){
        Intent intent = getIntent();
        validated = intent.getBooleanExtra("validated", false);
        String title;
        int topicVal;
        if(v.getId() == R.id.topic_1){
            topicVal = 1;
            title = topicText_1.getText().toString();
        } else if(v.getId() == R.id.topic_2){
            topicVal = 2;
            title = topicText_2.getText().toString();
        } else {
            topicVal = 3;
            title = topicText_3.getText().toString();
        }
        if(!validated){
            intent = new Intent(this, LoadingActivity.class);
            validated = true;
        } else {
            intent = new Intent(this, LearnTestActivity.class);
        }
        intent.putExtra("title", title);
        intent.putExtra("topic", topicVal);
        startActivity(intent);
    }
}
