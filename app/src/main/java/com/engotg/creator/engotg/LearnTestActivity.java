package com.engotg.creator.engotg;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class LearnTestActivity extends AppCompatActivity implements View.OnClickListener{

    private Button leftButton, rightButton, topButton,
            set1, set2, set3, set4, set5;
    private PopupWindow popupWindow;
    private LinearLayout linearLayout;
    private Intent intent;
    private int topicVal;
    private String topicTitle;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        topicTitle = intent.getExtras().getString("title");
        setTitle(topicTitle);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_test_selection);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        topButton = findViewById(R.id.topButton);
        linearLayout = (LinearLayout) findViewById(R.id.chooseLeftRight);
        LayoutInflater layoutInflater = (LayoutInflater) LearnTestActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.test_set_popup, null);
        set1 = customView.findViewById(R.id.set1);
        set2 = customView.findViewById(R.id.set2);
        set3 = customView.findViewById(R.id.set3);
        set4 = customView.findViewById(R.id.set4);
        set5 = customView.findViewById(R.id.set5);
        popupWindow = new PopupWindow(customView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(null);

        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        topButton.setOnClickListener(this);

    }

    public void onClick(View v){
        intent = getIntent();
        topicVal = intent.getExtras().getInt("topic");
        if(v.getId() == R.id.leftButton){
            intent = new Intent(this, AudioPlayer.class);
            intent.putExtra("topic", topicVal);
            intent.putExtra("title", topicTitle);
            if(popupWindow.isShowing()){
                popupWindow.dismiss();
            }
            startActivity(intent);
        } else if(v.getId() == R.id.topButton) {
            if(popupWindow.isShowing()){
                popupWindow.dismiss();
            }
            finish();
        } else {
            if(popupWindow.isShowing()){
                popupWindow.dismiss();
            } else {
                showPopup();
            }
        }
    }

    public void showPopup(){
        popupWindow.showAtLocation(linearLayout, Gravity.BOTTOM, 0,0 );
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

    public void onSetClick(){
        intent.putExtra("topic", topicVal);
        intent.putExtra("title", topicTitle);
        startActivity(intent);
        popupWindow.dismiss();
    }
}
