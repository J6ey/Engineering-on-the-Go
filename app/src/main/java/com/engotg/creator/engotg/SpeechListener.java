package com.engotg.creator.engotg;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.TextView;

import junit.framework.Test;

import java.util.ArrayList;

public class SpeechListener implements RecognitionListener{
    private String TAG = "STO";
    static boolean isListening;

    public void onReadyForSpeech(Bundle params) {
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
    public void onError(int error) {
        if(TestActivity.errorInfo != null){
            TestActivity.errorInfo.dismiss();
        }
        TestActivity.showErrorTip();
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
            if(TestActivity.errorInfo != null){
                TestActivity.errorInfo.dismiss();
            }
            if(data.contains("repeat")){
                TestActivity.speakBtn.performClick();
            } else if(data.contains("next")){
                TestActivity.tipInfo.dismiss();
                TestActivity.next.performClick();
            } else if(data.contains("explain")) {
                if(TestActivity.answerInfo.isShown()){
                    TestActivity.answerInfo.performClick();
                }
            } else {
                if(data.contains("a") || data.contains("hey")){
                    performClick(0);
                } else if(data.contains("be") || data.contains("B") || data.contains("bee")){
                    performClick(1);
                } else if(data.contains("see") || data.contains("C") || data.contains("sea")){
                    performClick(2);
                } else if(data.contains("d") || data.contains("Dee")){
                    performClick(3);
                } else if(data.contains("e") || data.contains("e e")){
                    performClick(4);
                } else {
                    boolean isChoice = false;
                    for (String strData : data) {
                        for (int i = 0; i < TestActivity.choiceArray.length; i++) {
                            if(strData.equalsIgnoreCase(((TextView)TestActivity.choiceArray[i]
                                    .getChildAt(0)).getText().toString())
                                    && TestActivity.choiceArray[i].isEnabled()){
                                TestActivity.choiceArray[i].performClick();
                                isChoice = true;
                                break;
                            }
                        }
                        if(isChoice) break;
                    }
                    if(!isChoice){
                        TestActivity.showErrorTip();
                        TestActivity.micBtn.setImageResource(R.drawable.ico_mic);
                        isListening = false;
                        TestActivity.micBtn.performClick();
                    }
                }
            }
        } else {
            if(TestActivity.errorInfo != null){
                TestActivity.errorInfo.dismiss();
            }
            if(data.contains("repeat")){
                TestActivity.speakBtn.performClick();
            } else if(data.contains("try again") ||
                    data.contains("try") || data.contains("again")){
                TestActivity.tryAgain.performClick();
            } else if(data.contains("back to menu") ||
                    data.contains("go back to menu") || data.contains("menu")){
                TestActivity.menu.performClick();
            } else {
                TestActivity.showErrorTip();
                TestActivity.micBtn.setImageResource(R.drawable.ico_mic);
                isListening = false;
                TestActivity.micBtn.performClick();
            }
        }
    }

    public void performClick(int charVal){
        if(charVal < TestActivity.choiceArray.length){
            if(TestActivity.choiceArray[charVal].isEnabled()){
                TestActivity.choiceArray[charVal].performClick();
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
