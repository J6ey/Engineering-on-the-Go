package com.engotg.creator.engotg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.tooltip.OnClickListener;
import com.tooltip.OnDismissListener;
import com.tooltip.Tooltip;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import io.paperdb.Paper;

public class TestActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private TextView questionsLeft, questionText, scoreView;
    static HashSet<String> choices;
    private String answer, explanation, question, topic, speakQuestion;
    static CardView[] choiceArray;
    private Integer[] randomQuestions;
    static ImageButton answerInfo, micBtn, speakBtn, next, settings;
    private SpeechRecognizer sr;
    private TextToSpeech tts;
    private int questionLength, currentNum, setVal, topicVal, score, wrongCount, localeVal;
    private LinearLayoutCompat questionFrame, scoreFrame;
    static boolean onResults, autoRead, autoPrompt, enableTips, twoTries;
    static CardView tryAgain, menu;
    static Tooltip tipInfo, errorInfo;
    private static Typeface typeface;
    private char correctLetter;
    private SpannableStringBuilder sb;
    private Toolbar toolbar;
    private static Context mContext;
    private SharedPreferences sharedPrefs;
    private double pitch, speed;
    private List<Locale> locList;


    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
//        setTitle(title);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        typeface = Typeface.createFromAsset(getResources().getAssets(), "fibra_one_light.otf");
        Paper.init(this);
        mContext = this;
        next = findViewById(R.id.nextButton);
        questionsLeft = findViewById(R.id.questionsLeft);
        questionText = findViewById(R.id.question);
        questionFrame = findViewById(R.id.questionFrame);
        questionText.setTypeface(typeface);
        questionsLeft.setTypeface(typeface);
        scoreFrame = findViewById(R.id.scoreFrame);
        micBtn = findViewById(R.id.mic);
        speakBtn = findViewById(R.id.speaker);
        answerInfo = findViewById(R.id.expButton);
        toolbar = findViewById(R.id.bar);
        settings = findViewById(R.id.settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        onResults = false;
        currentNum = 1;
        score = 0;
        topicVal = intent.getExtras().getInt("topic");
        setVal = intent.getExtras().getInt("set");
        topic = topicVal == 1 ? "Internal External Forces" : topicVal == 2 ?
                "Forces Moments" : "Internal Forces Stresses";

        questionLength = 0; // Starts from 1
        while (Paper.book().read(topic + "|" +
                "set " + setVal + "|" + "Questions|" + questionLength++) != null) {
        }
        questionLength = questionLength - 1; // Get number of questions
        randomQuestions = new Integer[questionLength];
        for (int i = 0; i < questionLength; i++) {
            randomQuestions[i] = i;
        }
        Collections.shuffle(Arrays.asList(randomQuestions));

        // Gets the first question
        question = Paper.book().read(topic + "|" +
                "set " + setVal + "|Questions|" + randomQuestions[0]);
        speakQuestion = formatBlank(question);
        questionText.setText(sb);

        // Gets first answer
        answer = Paper.book().read(topic + "|" +
                "set " + setVal + "|Answer|" + randomQuestions[0]);

        // Gets list of choices on 1st question and shuffles them
        choices = Paper.book().read(topic + "|" +
                "set " + setVal + "|Choices|" + randomQuestions[0]);
        List<String> choicesList = new ArrayList<>(choices);
        Collections.shuffle(choicesList);
        choices = new HashSet<>(choicesList);

        // Gets first explanation
        explanation = Paper.book().read(topic + "|" +
                "set " + setVal + "|Explanations|" + randomQuestions[0]);

        questionsLeft.setText(currentNum + "/" + questionLength);
        initSettings();
        locList = new ArrayList<>();
        tts = new TextToSpeech(this, this);
        initTooltip();
        wrongCount = twoTries ? 0 : 1;
        // Must be declared for each question
        setChoiceAmount();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrongCount = twoTries ? 0 : 1;
                if (SpeechListener.isListening) {
                    micBtn.setImageResource(R.drawable.ico_mic);
                    sr.cancel();
                    SpeechListener.isListening = false;
                }
                if (tts.isSpeaking()) {
                    speakBtn.setImageResource(R.drawable.ico_speak);
                    tts.stop();
                }
                answerInfo.setVisibility(View.GONE);
                answerInfo.setEnabled(false);
                if (Paper.book().read(topic + "|" +
                        "set " + setVal + "|" + "Questions|" + currentNum) != null) {
                    question = Paper.book().read(topic + '|' +
                            "set " + setVal + "|Questions|" + randomQuestions[currentNum]);
                    speakQuestion = formatBlank(question);
                    questionText.setText(sb);
                    explanation = Paper.book().read(topic + '|' +
                            "set " + setVal + "|Explanations|" + randomQuestions[currentNum]);
                    initTooltip();
                    answer = Paper.book().read(topic + '|' +
                            "set " + setVal + "|Answer|" + randomQuestions[currentNum]);
                    choices = Paper.book().read(topic + '|' +
                            "set " + setVal + "|Choices|" + randomQuestions[currentNum]);
                    List<String> choicesList = new ArrayList<>(choices);
                    Collections.shuffle(choicesList);
                    choices = new HashSet<>(choicesList);
                    setChoiceAmount();
                    currentNum++;
                    if (autoRead) {
                        speakOut();
                    }
                    questionsLeft.setText(currentNum + "/" + questionLength);
                } else {
                    onResults = true;
                    next.setVisibility(View.GONE);
                    questionsLeft.setVisibility(View.INVISIBLE);
                    questionFrame.setVisibility(View.INVISIBLE);
                    if (choiceArray != null) {
                        for (int i = 0; i < choiceArray.length; i++) {
                            if (i != 0) {
                                choiceArray[i].setVisibility(View.GONE);
                            } else {
                                choiceArray[i].setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    String text = "<font color='#2f3152'>Overall Score: </font>" +
                            "<font color='#3F51B5'>  " + score + "/" + questionLength + "</font>";
                    scoreView = findViewById(R.id.score);
                    scoreView.setTypeface(typeface, Typeface.BOLD);
                    scoreView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                    scoreFrame.setVisibility(View.VISIBLE);
                    tryAgain = findViewById(R.id.choice2);
                    tryAgain.setBackgroundResource(R.drawable.button_frame);
                    ((TextView) tryAgain.getChildAt(0)).setTypeface(typeface, Typeface.BOLD);
                    tryAgain.setEnabled(true);
                    ((TextView) tryAgain.getChildAt(0)).setText("Try Again");
                    tryAgain.setVisibility(View.VISIBLE);
                    menu = findViewById(R.id.choice3);
                    menu.setBackgroundResource(R.drawable.button_frame);
                    ((TextView) menu.getChildAt(0)).setTypeface(typeface, Typeface.BOLD);
                    ((TextView) menu.getChildAt(0)).setText("Back to Menu");
                    menu.setEnabled(true);
                    menu.setVisibility(View.VISIBLE);
                    if (autoRead) {
                        speakResults();
                    }
                    tryAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int newSetVal;
                            while ((newSetVal = ThreadLocalRandom.current().nextInt(1, 6)) == setVal) {
                            }
                            finish();
                            startActivity(getIntent().putExtra("set", newSetVal));
                        }
                    });
                    menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.stop();
                speakBtn.setImageResource(R.drawable.ico_speak);
                if(SpeechListener.isListening) {
                    micBtn.setImageResource(R.drawable.ico_mic);
                    sr.cancel();
                    SpeechListener.isListening = false;
                }
                startActivityForResult(new Intent(v.getContext(), SettingsActivity.class), 1);
            }
        });

        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SpeechListener.isListening) {
                    micBtn.setImageResource(R.drawable.ico_mic);
                    sr.cancel();
                    SpeechListener.isListening = false;
                }
                if (tts.isSpeaking()) {
                    speakBtn.setImageResource(R.drawable.ico_speak);
                    tts.stop();
                } else {
                    speakBtn.setImageResource(R.drawable.ico_stop);
                    if (!onResults) {
                        speakOut();
                    } else {
                        speakResults();
                    }
                }
            }
        });

        micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts.isSpeaking()) {
                    speakBtn.setImageResource(R.drawable.ico_speak);
                    tts.stop();
                }
                if (SpeechListener.isListening) {
                    micBtn.setImageResource(R.drawable.ico_mic);
                    sr.cancel();
                    SpeechListener.isListening = false;
                } else {
                    listen();
                }
            }
        });

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new SpeechListener());

        answerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SpeechListener.isListening) {
                    micBtn.setImageResource(R.drawable.ico_mic);
                    sr.cancel();
                    SpeechListener.isListening = false;
                }
                if (tts.isSpeaking()) {
                    speakBtn.setImageResource(R.drawable.ico_speak);
                    tts.stop();
                }
                showTooltip(v, Gravity.TOP, v.getId());
            }
        });
    }

    public void onRestart() {
        super.onRestart();
        initSettings();
    }

    public void initSettings() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        autoRead = sharedPrefs.getBoolean("auto_speak", true);
        autoPrompt = sharedPrefs.getBoolean("auto_input", false);
        enableTips = sharedPrefs.getBoolean("enableTips", true);
        twoTries = sharedPrefs.getBoolean("twoTries", true);
        wrongCount = twoTries ? 0 : 1;
    }

    public void initPitchSpeed(){
        if (Paper.book().read("pitchKey") == null) {
            tts.setPitch(1);
        } else {
            double pitch = Paper.book().read("pitchKey");
            tts.setPitch((float) pitch);
        }
        if (Paper.book().read("speedKey") == null) {
            tts.setSpeechRate(1);
        } else {
            double speed = Paper.book().read("speedKey");
            tts.setSpeechRate((float) speed);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                pitch = data.getDoubleExtra("pitch", 1);
                speed = data.getDoubleExtra("speed", 1);
                localeVal = data.getIntExtra("locale", 0);
                Paper.book().write("pitchKey", pitch);
                Paper.book().write("speedKey", speed);
                Paper.book().write("localeKey", localeVal);
                tts.setPitch((float) pitch);
                tts.setSpeechRate((float) speed);
                tts.setLanguage(locList.get(localeVal));
            }
            if(resultCode == Activity.RESULT_CANCELED){
            }
        }
    }

    public String formatBlank(String q){
        sb = new SpannableStringBuilder(q);
        TypefaceSpan sans = new TypefaceSpan("monospace");
        if(q.contains("_")){
            int usEnd = q.lastIndexOf('_');
            int usStart = q.indexOf('_');
            q = q.replace(q.substring(usStart, usEnd+1)
                    , "blank");
            sb.setSpan(sans, usStart, usEnd+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return q;
    }

    public void listen(){
        micBtn.setImageResource(R.drawable.ico_stop);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        sr.startListening(intent);
        Log.i("111111","11111111");
    }

    public void onDestroy(){
        if(tts != null || tts.isSpeaking()){
            tts.stop();
            tts.shutdown();
        }
        sr.destroy();
        super.onDestroy();
    }

    public void onStop(){
        super.onStop();
    }

    public void onInit(int status){
        if(status == TextToSpeech.SUCCESS){
            if(Paper.book().read("locList") == null) {
                for (Locale loc : tts.getAvailableLanguages()) {
                    if (loc.toLanguageTag().contains("en")) {
                        locList.add(Locale.forLanguageTag(loc.toLanguageTag()));
                    }
                }
                Paper.book().write("locList", locList);
            } else {
                locList = Paper.book().read("locList");
            }
            int result;
            if(Paper.book().read("localeKey") == null) {
                result = tts.setLanguage(locList.get(0));
                Paper.book().write("localeKey", 0);
            } else {
                result = tts.setLanguage(locList.get(Integer.valueOf(Paper.book().read("localeKey").toString())));
            }

            if(result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","This language is not supported");
            } else {
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                    }
                    @Override
                    public void onDone(final String utteranceId) {
                        TestActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                speakBtn.setImageResource(R.drawable.ico_speak);
                                if(autoPrompt){
                                    listen();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.e("TTS", "Utterance Error!" );
                    }
                });
                initPitchSpeed();
                if(autoRead){
                    speakOut();
                }
            }
        }
    }

    public void speakOut(){
        speakBtn.setImageResource(R.drawable.ico_stop);
        tts.speak(speakQuestion, TextToSpeech.QUEUE_ADD,null,null);
        String[] arr = choices.toArray(new String[choices.size()]);
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "onStart");
        for (int i = 0; i < arr.length; i++) {
            if(i == arr.length-1){
                tts.speak((char) (65 + i) + "," + arr[i],
                        TextToSpeech.QUEUE_ADD, params, "onStart");
            } else {
                tts.speak((char) (65 + i) + "," + arr[i],
                        TextToSpeech.QUEUE_ADD, null, null);
            }
            if(answer.equals(arr[i])){
                correctLetter = (char) (65 + i);
            }
        }
    }

    public void speakResults(){
        speakBtn.setImageResource(R.drawable.ico_stop);
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "onResults");
        String results = "You score, " + score + "out of" + questionLength;
        if(enableTips) {
            tts.speak(results, TextToSpeech.QUEUE_ADD,null,null);
            String options = "Please say, try again to retry, or back to menu to return to menu";
            String repeat = "you may also say, repeat, to repeat this message";
            tts.speak(options, TextToSpeech.QUEUE_ADD, null, null);
            tts.speak(repeat, TextToSpeech.QUEUE_ADD, params, "onResults");
        } else {
            tts.speak(results, TextToSpeech.QUEUE_ADD,params,"onResults");
        }
    }

    public void explain(){
        speakBtn.setImageResource(R.drawable.ico_stop);
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "onExplain");
        if(enableTips) {
            tts.speak(explanation, TextToSpeech.QUEUE_ADD,null,  null);
            String afterExplain = "to repeat, say explain, or say, next, to"
                    + " move on.";
            tts.speak(afterExplain, TextToSpeech.QUEUE_ADD, params, "onExplain");
        } else {
            tts.speak(explanation, TextToSpeech.QUEUE_ADD,params, "onExplain");
        }
    }

    public void setChoiceAmount(){
        if(choiceArray != null){
            for (int i = 0; i < choiceArray.length; i++) {
                choiceArray[i].setVisibility(View.GONE);
            }
        }
        choiceArray = new CardView[choices.size()];
        for (int i = 0; i < choiceArray.length; i++) {
            String buttonID = "choice" + (i + 1);
            String[] arr = choices.toArray(new String[choices.size()]);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            choiceArray[i] = findViewById(resID);
            choiceArray[i].setBackgroundResource(R.drawable.button_frame);
            ((TextView)choiceArray[i].getChildAt(0)).setTypeface(typeface, Typeface.BOLD);
            choiceArray[i].setVisibility(View.VISIBLE);
            ((TextView)choiceArray[i].getChildAt(0)).setText(arr[i]);
            choiceArray[i].setEnabled(true);
            choiceArray[i].setOnClickListener(this);
        }
        for (int i = 0; i < choiceArray.length; i++) {
            if(((TextView)choiceArray[i].getChildAt(0)).getText().equals("")){
                choiceArray[i].setVisibility(View.GONE);
            }
        }
    }

    public void initTooltip(){
        tipInfo = new Tooltip.Builder(answerInfo).setText(explanation)
                .setTextColor(getResources().getColor(R.color.mimoWhite)).setGravity(Gravity.TOP).setCornerRadius(16f).setDismissOnClick(false)
                .setCancelable(true).setTypeface(typeface).setBackgroundColor(getResources().getColor(R.color.blueGrey)).build();
    }

    public static void initErrorTip(){
        String errorText = "Didn't catch that. Please speak again.";
        errorInfo = new Tooltip.Builder(micBtn).setText(errorText)
                .setTextColor(mContext.getResources().getColor(R.color.black)).setGravity(Gravity.TOP).setCornerRadius(16f).setDismissOnClick(true)
                .setCancelable(true).setTypeface(typeface).setBackgroundColor(mContext.getResources().getColor(R.color.red)).build();
    }

    public static void showErrorTip(){
        initErrorTip();
        errorInfo.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                initErrorTip();
            }
        });
        errorInfo.show();
    }

    public void showTooltip(View v, int gravity, int id){
        if (autoRead){
            explain();
        }
        tipInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(@NonNull Tooltip tooltip) {
                tts.stop();
                explain();
            }
        });
        tipInfo.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                speakBtn.setImageResource(R.drawable.ico_speak);
                tts.stop();
                initTooltip();
            }
        });
        tipInfo.show();

    }

    public void onClick(View v){
        if(SpeechListener.isListening){
            micBtn.setImageResource(R.drawable.ico_mic);
            sr.cancel();
            SpeechListener.isListening = false;
        }
        if(tts.isSpeaking()){
            speakBtn.setImageResource(R.drawable.ico_speak);
            tts.stop();
        }
        CardView btn = findViewById(v.getId());
        CardView[] btnArr = new CardView[5];
        for (int i = 0; i < btnArr.length; i++) {
            String buttonID = "choice" + (i + 1);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            btnArr[i] = findViewById(resID);
        }
        if(((TextView)btn.getChildAt(0)).getText().equals(answer)) {
            answerInfo.setVisibility(View.VISIBLE);
            answerInfo.setEnabled(true);
            score++;
            btn.setBackgroundResource(R.drawable.button_correct);
            if(autoRead){
                speakCorrect();
            }
            for (int i = 0; i < btnArr.length; i++) {
                btnArr[i].setEnabled(false);
            }
        } else if(wrongCount < 1){ // can try again
            wrongCount++;
            if(autoRead){
                speakWrong();
            }
            btn.setBackgroundResource(R.drawable.button_wrong);

        } else {
            answerInfo.setVisibility(View.VISIBLE);
            answerInfo.setEnabled(true);
            if(autoRead){
                speakSorry();
            }
            btn.setBackgroundResource(R.drawable.button_wrong);
            if(((TextView)btnArr[0].getChildAt(0)).getText().equals(answer)){
                btnArr[0].setBackgroundResource(R.drawable.button_correct);
            } else if (((TextView)btnArr[1].getChildAt(0)).getText().equals(answer)){
                btnArr[1].setBackgroundResource(R.drawable.button_correct);
            } else if (((TextView)btnArr[2].getChildAt(0)).getText().equals(answer)){
                btnArr[2].setBackgroundResource(R.drawable.button_correct);
            } else if (((TextView)btnArr[3].getChildAt(0)).getText().equals(answer)){
                btnArr[3].setBackgroundResource(R.drawable.button_correct);
            } else {
                btnArr[4].setBackgroundResource(R.drawable.button_correct);
            }
            for (int i = 0; i < btnArr.length; i++) {
                btnArr[i].setEnabled(false);
            }
        }
    }

    public void speakSorry(){
        speakBtn.setImageResource(R.drawable.ico_stop);
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "onSorry");
        String results = "sorry, the correct answer is, " + correctLetter + "," + answer;
        if(enableTips) {
            tts.speak(results, TextToSpeech.QUEUE_ADD, null, null);
            String cmdTips = "Say, explain, to hear an explanation, or say, next, to move on";
            tts.speak(cmdTips, TextToSpeech.QUEUE_ADD, params, "onSorry");
        } else {
            tts.speak(results, TextToSpeech.QUEUE_ADD,params, "onSorry");
        }
    }

    public void speakCorrect(){
        speakBtn.setImageResource(R.drawable.ico_stop);
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "onCorrect");
        String results = "correct!";

        if(enableTips) {
            tts.speak(results, TextToSpeech.QUEUE_ADD, null, null);
            String cmdTips = "say, explain, to hear an explanation, or say, next, to move on";
            tts.speak(cmdTips, TextToSpeech.QUEUE_ADD, params, "onCorrect");
        } else {
            tts.speak(results, TextToSpeech.QUEUE_ADD,params, "onCorrect");
        }
    }

    public void speakWrong(){
        speakBtn.setImageResource(R.drawable.ico_stop);
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "onWrong");
        String results = "please try again";
        tts.speak(results, TextToSpeech.QUEUE_ADD,params,"onWrong");
    }

}