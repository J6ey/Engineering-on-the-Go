package com.engotg.creator.engotg;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import io.paperdb.Paper;

public class TopicActivity extends AppCompatActivity implements View.OnClickListener{

    private Button topic_1, topic_2, topic_3;
    private ProgressBar bar;
    private TextView loadingText;

    protected void onCreate(Bundle savedInstanceState) {
        Paper.init(this);
        setTitle("Select a topic");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_selection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        topic_1 = findViewById(R.id.topic_1);
        topic_2 = findViewById(R.id.topic_2);
        topic_3 = findViewById(R.id.topic_3);
        bar = findViewById(R.id.loadingBar);
        loadingText = findViewById(R.id.loadingText);
        checkWithServer();
        topic_1.setOnClickListener(this);
        topic_2.setOnClickListener(this);
        topic_3.setOnClickListener(this);
    }

    public void checkWithServer(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String serverVer= dataSnapshot.child("version").getValue().toString();
                if(Paper.book().read("version") == null ||
                        !Paper.book().read("version").equals(serverVer)){
                    validate(serverVer);
                } else {
                    updateUI();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Database Error", databaseError.getDetails());
                updateUI();
            }
        });
    }

    public void updateUI(){
        topic_1.setEnabled(true);
        topic_2.setEnabled(true);
        topic_3.setEnabled(true);
        topic_1.setTextColor(0xFF5E6762);
        topic_2.setTextColor(0xFF5E6762);
        topic_3.setTextColor(0xFF5E6762);
        bar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
    }

    public void validate(String serverVer){
        if(isConnectedToInternet()){
            new DownloadTask(TopicActivity.this, topic_1, topic_2, topic_3,
                    bar, loadingText, serverVer);
        } else {
            updateUI();
            loadingText.setText("Update failed. No network connection.");
        }
    }

    private boolean isConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null && info.isConnected()){
            return true;
        }
        return false;
    }

    public void onClick(View v){
        Button btn = (Button) v;
        Intent intent;
        int topicVal;
        if(v.getId() == R.id.topic_1){
            topicVal = 1;
        } else if(v.getId() == R.id.topic_2){
            topicVal = 2;
        } else {
            topicVal = 3;
        }
        intent = new Intent(this, LearnTestActivity.class);
        intent.putExtra("title", btn.getText());
        intent.putExtra("topic", topicVal);
        startActivity(intent);
    }
}
