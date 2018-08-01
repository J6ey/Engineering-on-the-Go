package com.engotg.creator.engotg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioPlayer extends AppCompatActivity implements View.OnClickListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Button topicsButton, menuButton;
    static ImageButton button_pause_play;
    static SeekBar seekbar;
    private TextView timer;
    static int totalTime;
    static boolean onStart;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(intent.getExtras().getString("title"));
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        TabLayout.Tab leftTab = tabLayout.newTab();
        TabLayout.Tab rightTab = tabLayout.newTab();
        onStart = true;
        String topic = "", leftSubTopic = "", rightSubTopic ="";
        int topicVal = intent.getExtras().getInt("topic");
        if (topicVal == 1) {
            leftTab.setText("Internal forces");
            rightTab.setText("External forces");
            topic = "Internal External Forces";
            leftSubTopic = "Internal Forces";
            rightSubTopic = "External Forces";
        } else if (topicVal == 2) {
            leftTab.setText("Forces");
            rightTab.setText("Moments");
            topic = "Forces Moments";
            leftSubTopic = "Forces";
            rightSubTopic = "Moments";
        } else {
            leftTab.setText("Internal forces");
            rightTab.setText("Internal stresses");
            topic = "Internal Forces Stresses";
            leftSubTopic = "Internal Forces";
            rightSubTopic = "Internal Stresses";
        }

        // Left & Right Tabs
        tabLayout.addTab(leftTab);
        tabLayout.addTab(rightTab);

        // Timer Label
        timer = findViewById(R.id.textTimer);

        // Bottom Buttons
        menuButton = findViewById(R.id.menuButton);
        topicsButton = findViewById(R.id.topicButton);

        // Buttons
        button_pause_play = findViewById(R.id.play_pause_button);

        // Media Player
        File apkStorage = new File(this.getFilesDir() + "/"
                + "EngOTG_data" + "/" + topic + "/" + leftSubTopic + "/");
        ArrayList<String> audioList = new ArrayList<>(Arrays.asList(apkStorage.list()));
        try {
            PlaceholderFragment.mediaPlayer = new MediaPlayer();
            PlaceholderFragment.mediaPlayer.setDataSource(apkStorage.getAbsolutePath() + "/" + audioList.get(0));
            PlaceholderFragment.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            PlaceholderFragment.mediaPlayer.prepare();
        } catch (Exception e){
            e.printStackTrace();
        }

        PlaceholderFragment.mediaPlayer.seekTo(0);
        totalTime = PlaceholderFragment.mediaPlayer.getDuration();

        // Seek Bar
        seekbar = findViewById(R.id.seekbar);
        seekbar.setMax(totalTime);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    PlaceholderFragment.mediaPlayer.seekTo(progress);
                    seekbar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Thread (update seekBar & timeLabel)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(PlaceholderFragment.mediaPlayer != null){
                    try{
                        Message msg = new Message();
                        msg.what = PlaceholderFragment.mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e){

                    }
                }
            }
        }).start();
        button_pause_play.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        topicsButton.setOnClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update SeekBar
            seekbar.setProgress(currentPosition);
            // update Labels
            String elapsedTime = createTimeLabel(currentPosition);
            timer.setText(elapsedTime);

            if(elapsedTime.equals(createTimeLabel(totalTime))){
                button_pause_play.setImageResource(R.drawable.ic_play);
            }
        }
    };

    public static String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if(sec < 10) timeLabel += "0";
        timeLabel += sec;

        return  timeLabel;
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.play_pause_button:
                if(!PlaceholderFragment.mediaPlayer.isPlaying()){
                    // Plays
                    PlaceholderFragment.mediaPlayer.start();
                    if(onStart){
                        setItemSelected(PlaceholderFragment.leftAudioList.getRootView());
                        onStart = false;
                    }
                    button_pause_play.setImageResource(R.drawable.ic_pause);
                } else {
                    // Pauses
                    PlaceholderFragment.mediaPlayer.pause();
                    button_pause_play.setImageResource(R.drawable.ic_play);
                }
                break;
            case R.id.topicButton:
                Intent topicIntent = new Intent(this, TopicActivity.class);
                startActivity(topicIntent);
                finish();
                break;
            case R.id.menuButton:
                finish();
        }
    }

    public void setItemSelected(View view){
        TextView tv = view.findViewById(R.id.audio_text);
        tv.setTextColor(getResources().getColor(R.color.greenLogo));
        tv.setTypeface(null, Typeface.BOLD);
    }

    protected void onDestroy(){
        super.onDestroy();
        PlaceholderFragment.mediaPlayer.stop();
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}
