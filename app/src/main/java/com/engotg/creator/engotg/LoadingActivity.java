package com.engotg.creator.engotg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoadingActivity extends AppCompatActivity{

    static TextView loadingText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(), "fibra_one_regular.otf");
        loadingText = findViewById(R.id.loadingText);
        loadingText.setTypeface(typeface);
        if(isConnectedToInternet()){
            checkWithServer();
        } else {
            loadingText.setText("Validation failed: No network connection\nPlease try again");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchModes();
                }
            } , 2500);
        }
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
                    loadingText.setText("Success.");
                    launchModes();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Database Error", databaseError.getDetails());
                launchModes();
            }
        });
    }

    public void validate(String serverVer){
        if(isConnectedToInternet()){
            new DownloadTask(LoadingActivity.this, loadingText, serverVer, getIntentForNext());
        } else {
            loadingText.setText("Update failed: No network connection\nPlease try again");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchModes();
                }
            } , 2500);
        }
    }
    public Intent getIntentForNext(){
        Intent intent = getIntent();
        String title = intent.getExtras().getString("title");
        int topicVal = intent.getExtras().getInt("topic");
        intent = new Intent(this, LearnTestActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("topic", topicVal);
        return intent;
    }

    public void launchModes(){
        startActivity(getIntentForNext());
        finish();
    }

    private boolean isConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null && info.isConnected()){
            return true;
        }
        return false;
    }
}
