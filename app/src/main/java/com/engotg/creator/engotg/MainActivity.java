package com.engotg.creator.engotg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button topicButton;
    private Button aboutButton;

    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Engineering on the Go");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topicButton = findViewById(R.id.topicButton);
        aboutButton = findViewById(R.id.aboutButton);
        topicButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
    }

    public void onClick(View v){
        Intent intent;
        if(v.getId() == R.id.topicButton){
            intent = new Intent(this, TopicActivity.class);
            startActivity(intent);
        } else {
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }
}
