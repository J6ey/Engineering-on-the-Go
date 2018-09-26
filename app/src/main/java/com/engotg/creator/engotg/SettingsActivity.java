package com.engotg.creator.engotg;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import gs.preference.SeekBarPreference;
import io.paperdb.Paper;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class SettingsActivity extends AppCompatPreferenceActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech tts;
    private SeekBarPreference pitchSlider;
    private SeekBarPreference rateSlider;
    private ListPreference accentList;
    private List<Locale> locList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.pref_main);
        Paper.init(this);
        accentList = (ListPreference) findPreference("accent_key");
        tts = new TextToSpeech(this, this);
        pitchSlider = (SeekBarPreference) findPreference("pitch_key");
        rateSlider = (SeekBarPreference) findPreference("speech_rate_key");
        pitchSlider.setMin(1);
        rateSlider.setMin(1);
        accentList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                tts.setLanguage(locList.get(Integer.valueOf((String) newValue)));
                return true;
            }
        });

        Preference voiceSelection = findPreference("voice_data");
        voiceSelection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.google.android.tts","com.google.android.tts.local.voicepack.ui.VoiceDataInstallActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }
        });
        Preference defaultPitch = findPreference("default_pitch");
        Preference defaultSpeed = findPreference("default_speed");
        defaultPitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                pitchSlider.setValue(10);
                tts.setPitch((float) 1.0);
                return true;
            }
        });
        defaultSpeed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                rateSlider.setValue(10);
                tts.setSpeechRate((float) 1.0);
                return true;
            }
        });

        Preference sample = findPreference("sample_key");
        sample.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                double pitch = pitchSlider.getValue();
                pitch = pitch / 10;
                tts.setPitch((float) pitch);
                double rate = rateSlider.getValue();
                rate = rate / 10;
                tts.setSpeechRate((float) rate);
                String sampleText = "This is an example of speech synthesis in English.";
                tts.speak(sampleText, TextToSpeech.QUEUE_FLUSH, null, "onSample");
                return false;
            }
        });
    }

    public void onBackPressed(){
        tts.stop();
        tts.shutdown();
        double pitch = pitchSlider.getValue();
        double speed = rateSlider.getValue();
        pitch = pitch / 10;
        speed = speed / 10;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("pitch", pitch);
        returnIntent.putExtra("speed", speed);
        returnIntent.putExtra("locale", Integer.valueOf(accentList.getValue()));
        Paper.book().write("localeKey", Integer.valueOf(accentList.getValue()));
        Paper.book().write("pitchKey", pitch);
        Paper.book().write("speedKey", speed);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void initLocale(){
        locList = new ArrayList<>();
        for (Locale loc : tts.getAvailableLanguages()) {
            if (loc.toLanguageTag().contains("en")) {
                locList.add(Locale.forLanguageTag(loc.toLanguageTag()));
            }
        }
        Paper.book().write("locList", locList);
    }

    public void onInit(int status){
        if(status == TextToSpeech.SUCCESS){
            if(accentList != null){
                if(Paper.book().read("locList") == null) {
                    initLocale();
                } else {
                    locList = Paper.book().read("locList");
                }
                CharSequence entries[] = new String[locList.size()];
                CharSequence entryValues[] = new String[locList.size()];
                int i = 0;
                for (Locale loc : locList) {
                    entries[i] = loc.getDisplayCountry();
                    entryValues[i] = Integer.toString(i);
                    i++;
                }
                accentList.setEntries(entries);
                accentList.setEntryValues(entryValues);
            }
            int result = tts.setLanguage(locList.get(Integer.valueOf(accentList.getValue())));


            if(result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","This language is not supported");
            } else {
                Log.e(TAG, "Error: Language NOT supposed.");
            }
        }
    }

}
