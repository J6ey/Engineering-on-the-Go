package com.engotg.creator.engotg;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import junit.framework.Test;

import java.util.ArrayList;

public class SpeechListener implements RecognitionListener{
    private String TAG = "STO";
    static boolean isListening;

    public void onReadyForSpeech(Bundle params)
    {
        isListening = true;
        Log.d(TAG, "onReadyForSpeech");
    }
    public void onBeginningOfSpeech()
    {
        Log.d(TAG, "onBeginningOfSpeech");
    }
    public void onRmsChanged(float rmsdB)
    {
        Log.d(TAG, "onRmsChanged");
    }
    public void onBufferReceived(byte[] buffer)
    {
        Log.d(TAG, "onBufferReceived");
    }
    public void onEndOfSpeech()
    {
        Log.d(TAG, "onEndofSpeech");
    }
    public void onError(int error)
    {
        TestActivity.micBtn.setImageResource(R.drawable.ico_mic);
        isListening = false;
        TestActivity.micBtn.performClick();
        Log.d(TAG,  "error " +  error);
    }
    public void onResults(Bundle results)
    {
        TestActivity.micBtn.setImageResource(R.drawable.ico_mic);
        isListening = false;
        Log.d(TAG, "onResults " + results);
        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(TAG, "onResults: " + data);
        if(!TestActivity.onResults){
            if(data.contains("repeat")){
                TestActivity.speakBtn.performClick();
            } else if(data.contains("next")){
                TestActivity.next.performClick();
            } else if(data.contains("explain")) {
                if(TestActivity.answerInfo.isShown()){
                    TestActivity.answerInfo.performClick();
                }
            } else {
                for (int i = 0; i < data.size(); i++) {
                    char letter = data.get(i).toUpperCase().charAt(0);
                    if(letter - 65 < TestActivity.choiceArray.length &&
                            letter >= 65){
                        if(TestActivity.choiceArray[letter - 65].isEnabled()){
                            TestActivity.choiceArray[letter - 65].performClick();
                            break;
                        }
                    }
                }
            }
        } else {
            if(data.contains("repeat")){
                TestActivity.speakBtn.performClick();
            } else if(data.contains("try again")){
                TestActivity.tryAgain.performClick();
            } else if(data.contains("back to menu")){
                TestActivity.menu.performClick();
            }
        }
    }
    public void onPartialResults(Bundle partialResults)
    {
        Log.d(TAG, "onPartialResults");
    }
    public void onEvent(int eventType, Bundle params)
    {
        Log.d(TAG, "onEvent " + eventType);
    }
}
