package com.engotg.creator.engotg;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.engotg.creator.engotg.AudioPlayer;
import com.engotg.creator.engotg.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.engotg.creator.engotg.AudioPlayer.createTimeLabel;
import static com.engotg.creator.engotg.AudioPlayer.totalTime;

public class PlaceholderFragment extends Fragment {

    private static class Pair {
        String text;
        boolean flag;

        Pair(String text, boolean flag){
            this.text = text;
            this.flag = flag;
        }
    }

    public class LeftAudioAdapter extends BaseAdapter {

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            return leftAudio.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.audio_list_layout, null);
            TextView audioText = view.findViewById(R.id.audio_text);
            TextView duration = view.findViewById(R.id.duration);
            audioText.setText(leftAudio.get(i).text);
            String pathStr = apkStorageLeft.getAbsolutePath() + "/" + leftAudio.get(i).text;
            Uri uri = Uri.parse(pathStr);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(viewGroup.getContext(), uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int millisecond = Integer.parseInt(durationStr);
            duration.setText(createTimeLabel(millisecond));
            duration.setTypeface(typeface);
            if(leftAudio.get(i).flag){
                audioText.setTextColor(getResources().getColor(R.color.orange));
                audioText.setTypeface(typeface, Typeface.BOLD);
            } else {
                audioText.setTextColor(getResources().getColor(R.color.mimoWhite));
                audioText.setTypeface(typeface);
            }
            return view;
        }
    }

    public class RightAudioAdapter extends BaseAdapter {

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            return rightAudio.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.audio_list_layout, null);
            TextView audioText = view.findViewById(R.id.audio_text);
            TextView duration = view.findViewById(R.id.duration);
            audioText.setText(rightAudio.get(i).text);
            String pathStr = apkStorageRight.getAbsolutePath() + "/" + rightAudio.get(i).text;
            Uri uri = Uri.parse(pathStr);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(viewGroup.getContext(), uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int millisecond = Integer.parseInt(durationStr);
            duration.setText(createTimeLabel(millisecond));
            duration.setTypeface(typeface);
            if(rightAudio.get(i).flag){
                audioText.setTextColor(getResources().getColor(R.color.orange));
                audioText.setTypeface(typeface, Typeface.BOLD);
            } else {
                audioText.setTextColor(getResources().getColor(R.color.mimoWhite));
                audioText.setTypeface(typeface);
            }
            return view;
        }
    }

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static ListView leftAudioList, rightAudioList;
    private static List<Pair> leftAudio, rightAudio; // Existing audios
    static MediaPlayer mediaPlayer;
    private final String downloadDirectory = "EngOTG_data";
    private File apkStorageLeft, apkStorageRight;
    private LeftAudioAdapter leftAudioAdapter;
    private RightAudioAdapter rightAudioAdapter;
    private Typeface typeface;
    private int clickedPos = 0;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        String topic = "", leftSubTopic = "", rightSubTopic ="";
        typeface = Typeface.createFromAsset(getResources().getAssets(), "fibra_one_regular.otf");
        Intent intent = getActivity().getIntent();
        int topicVal = intent.getExtras().getInt("topic");
        switch (topicVal){
            case 1:
                topic = "Internal External Forces";
                leftSubTopic = "Internal Forces";
                rightSubTopic = "External Forces";
                break;
            case 2:
                topic = "Forces Moments";
                leftSubTopic = "Forces";
                rightSubTopic = "Moments";
                break;
            case 3:
                topic = "Internal Forces Stresses";
                leftSubTopic = "Internal Forces";
                rightSubTopic = "Internal Stresses";
                break;
        }
        apkStorageLeft = new File(getContext().getFilesDir() + "/"
                + downloadDirectory + "/" + topic + "/" + leftSubTopic + "/");
        apkStorageRight = new File(getContext().getFilesDir() + "/"
                + downloadDirectory + "/" + topic + "/" + rightSubTopic + "/");
        leftAudio = new ArrayList<>();
        rightAudio = new ArrayList<>();

        for (int i = 0; i < Arrays.asList(apkStorageLeft.list()).size(); i++) {
            leftAudio.add(new Pair(Arrays.asList(apkStorageLeft.list()).get(i), false));
        }
        for (int i = 0; i < Arrays.asList(apkStorageRight.list()).size(); i++) {
            rightAudio.add(new Pair(Arrays.asList(apkStorageRight.list()).get(i), false));
        }

        leftAudioAdapter = new LeftAudioAdapter();
        rightAudioAdapter = new RightAudioAdapter();


        switch(getArguments().getInt(ARG_SECTION_NUMBER)){
            case 1:
                rootView = inflater.inflate(R.layout.fragment_left, container, false);
                leftAudioList = rootView.findViewById(R.id.leftAudioList);
                leftAudioList.setAdapter(leftAudioAdapter);
                leftAudioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        AudioPlayer.onStart = false;
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.stop();
                        }
                        try {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(apkStorageLeft.getAbsolutePath() + "/" + leftAudio.get(position).text);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                        AudioPlayer.button_pause_play.setImageResource(R.drawable.ic_pause);
                        mediaPlayer.seekTo(0);
                        totalTime = mediaPlayer.getDuration();
                        AudioPlayer.seekbar.setMax(totalTime);
                        leftAudio.get(clickedPos).flag = false;
                        leftAudio.get(position).flag = true;
                        clickedPos = position;
                        leftAudioAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_right, container, false);
                rightAudioList = rootView.findViewById(R.id.rightAudioList);
                rightAudioList.setAdapter(rightAudioAdapter);
                rightAudioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        AudioPlayer.onStart = false;
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        try {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(apkStorageRight.getAbsolutePath() + "/" + rightAudio.get(position).text);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                        AudioPlayer.button_pause_play.setImageResource(R.drawable.ic_pause);
                        mediaPlayer.seekTo(0);
                        totalTime = mediaPlayer.getDuration();
                        AudioPlayer.seekbar.setMax(totalTime);
                        rightAudio.get(clickedPos).flag = false;
                        rightAudio.get(position).flag = true;
                        clickedPos = position;
                        rightAudioAdapter.notifyDataSetChanged();
                    }
                });
                break;
        }
        return rootView;
    }

    public void setItemSelected(View view){
        TextView tv = view.findViewById(R.id.audio_text);
        tv.setTextColor(getResources().getColor(R.color.orange));
        tv.setTypeface(typeface, Typeface.BOLD);
    }

    public void setItemNormal(){
        for (int i = 0; i < PlaceholderFragment.rightAudioList.getChildCount(); i++) {
            View v = PlaceholderFragment.rightAudioList.getChildAt(i);
            TextView tv = v.findViewById(R.id.audio_text);
            tv.setTextColor(getResources().getColor(R.color.mimoWhite));
            tv.setTypeface(typeface);
        }
        for (int i = 0; i < PlaceholderFragment.leftAudioList.getChildCount(); i++) {
            View v = PlaceholderFragment.leftAudioList.getChildAt(i);
            TextView tv = v.findViewById(R.id.audio_text);
            tv.setTextColor(getResources().getColor(R.color.mimoWhite));
            tv.setTypeface(typeface);
        }
    }
}