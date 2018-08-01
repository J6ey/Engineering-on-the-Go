package com.engotg.creator.engotg;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tooltip.OnDismissListener;
import com.tooltip.Tooltip;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import io.paperdb.Paper;

public class TestActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private TextView questionsLeft, questionText;
    static HashSet<String> choices;
    private String answer, explanation, title, question, topic;
    static Button[] choiceArray;
    private Integer[] randomQuestions;
    static ImageButton answerInfo, micBtn, speakBtn, next;
    private SpeechRecognizer sr;
    private TextToSpeech tts;
    private int questionLength, currentNum, setVal, topicVal, score;
    private LinearLayoutCompat questionFrame, scoreFrame;
    static boolean onResults;
    static Button tryAgain, menu;

    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        title = intent.getExtras().getString("title");
        setTitle(title);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Paper.init(this);
        next = findViewById(R.id.nextButton);
        questionsLeft = findViewById(R.id.questionsLeft);
        questionText = findViewById(R.id.question);
        questionFrame = findViewById(R.id.questionFrame);
        scoreFrame = findViewById(R.id.scoreFrame);
        micBtn = findViewById(R.id.mic);
        speakBtn = findViewById(R.id.speaker);
        answerInfo = findViewById(R.id.expButton);
        onResults = false;
        currentNum = 1;
        score = 0;
        topicVal = intent.getExtras().getInt("topic");
        setVal = intent.getExtras().getInt("set");
        topic = topicVal == 1 ? "Internal External Forces" : topicVal == 2 ?
                "Forces Moments" : "Internal Forces Stresses";

        questionLength = 0; // Starts from 1
        while(Paper.book().read(topic + "|" +
                "set " + setVal + "|" + "Questions|" + questionLength++) != null){}
        questionLength = questionLength - 1; // Get number of questions
        randomQuestions = new Integer[questionLength];
        for (int i = 0; i < questionLength; i++) {
            randomQuestions[i] = i;
        }
        Collections.shuffle(Arrays.asList(randomQuestions));

        // Gets the first question
        question = Paper.book().read(topic + "|" +
                "set " + setVal + "|Questions|" + randomQuestions[0]);
        questionText.setText(question);

        // Gets first answer
        answer = Paper.book().read(topic + "|" +
                "set " + setVal + "|Answer|" + randomQuestions[0]);

        // Gets list of choices on 1st question
        choices = Paper.book().read(topic + "|" +
                "set " + setVal + "|Choices|" + randomQuestions[0]);

        // Gets first explanation
        explanation = Paper.book().read(topic + "|" +
                "set " + setVal + "|Explanations|" + randomQuestions[0]);

        questionsLeft.setText(currentNum + "/" + questionLength);

        tts = new TextToSpeech(this, this);

        // Must be declared for each question
        setChoiceAmount();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tts.isSpeaking()){
                    tts.stop();
                }
                answerInfo.setVisibility(View.GONE);
                answerInfo.setEnabled(false);
                if(Paper.book().read(topic + "|" +
                        "set " + setVal + "|" + "Questions|" + currentNum) != null){
                    question = Paper.book().read(topic + '|' +
                            "set " + setVal + "|Questions|" + randomQuestions[currentNum]);
                    questionText.setText(question);
                    explanation = Paper.book().read(topic + '|' +
                            "set " + setVal + "|Explanations|" + randomQuestions[currentNum]);
                    answer = Paper.book().read(topic + '|' +
                            "set " + setVal + "|Answer|" + randomQuestions[currentNum]);
                    choices = Paper.book().read(topic + '|' +
                            "set " + setVal + "|Choices|" + randomQuestions[currentNum]);
                    setChoiceAmount();
                    currentNum++;
                    speakOut();
                    questionsLeft.setText(currentNum + "/" + questionLength);
                } else {
                    onResults = true;
                    next.setVisibility(View.GONE);
                    questionsLeft.setVisibility(View.INVISIBLE);
                    questionFrame.setVisibility(View.INVISIBLE);
                    if(choiceArray != null){
                        for (int i = 0; i < choiceArray.length; i++) {
                            if(i != 0){
                                choiceArray[i].setVisibility(View.GONE);
                            } else {
                                choiceArray[i].setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    String text = "<font color='#5E6762'>Overall Score: </font>" +
                            "<font color='#3F51B5'>  " + score + "/" + questionLength + "</font>";
                    TextView scoreView = findViewById(R.id.score);
                    scoreView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                    scoreFrame.setVisibility(View.VISIBLE);
                    tryAgain = findViewById(R.id.choice2);
                    tryAgain.setEnabled(true);
                    tryAgain.setBackgroundResource(R.drawable.custom_button);
                    tryAgain.setText("Try Again");
                    tryAgain.setVisibility(View.VISIBLE);
                    menu = findViewById(R.id.choice3);
                    menu.setBackgroundResource(R.drawable.custom_button);
                    menu.setText("Back to Menu");
                    menu.setEnabled(true);
                    menu.setVisibility(View.VISIBLE);
                    speakResults();
                    tryAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int newSetVal = 0;
                            while((newSetVal = ThreadLocalRandom.current().nextInt(1, 6)) == setVal){}
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
                answerInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(tts.isSpeaking()){
                            tts.stop();
                        }
                        showTooltip(v, Gravity.TOP, v.getId());
                    }
                });
            }
        });

        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tts.isSpeaking()){
                    tts.stop();
                } else {
                    if(!onResults){
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
                if(tts.isSpeaking()){
                    tts.stop();
                }
                if(SpeechListener.isListening){
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
                if(tts.isSpeaking()){
                    tts.stop();
                }
                showTooltip(v, Gravity.TOP, v.getId());
            }
        });
    }

    public void listen(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        micBtn.setImageResource(R.drawable.ico_stop);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        sr.startListening(intent);
        Log.i("111111","11111111");
    }

    public void onDestroy(){
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void onStop(){
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onStop();
    }

    public void onResume(){
        tts = new TextToSpeech(this, this);
        super.onResume();
    }

    public void onInit(int status){
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.US);
            if(result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","This language is not supported");
            } else {
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        listen();
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        listen();
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.e("TTS", "Utterance Error!" );
                    }
                });
                speakOut();

            }
        }
    }

    public void speakOut(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(question, TextToSpeech.QUEUE_ADD,null,null);
        } else {
            tts.speak(question, TextToSpeech.QUEUE_ADD, null);
        }
        speakBtn.setImageResource(R.drawable.ico_stop);
        String[] arr = choices.toArray(new String[choices.size()]);
        for (int i = 0; i < arr.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak((char)(65 + i)+ "," + arr[i],
                        TextToSpeech.QUEUE_ADD,null,null);
            } else {
                tts.speak((char)(65 + i)+ "," + arr[i],
                        TextToSpeech.QUEUE_ADD, null);
            }
        }

    }

    public void speakResults(){
        String results = "You score, " + score + "out of" + questionLength + "." +
                "Please say, try again to try again, or back to menu to go back to menu, " +
                "you may also say, repeat, to repeat this message.";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(results, TextToSpeech.QUEUE_ADD,null,null);
        } else {
            tts.speak(results, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public void explain(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(explanation, TextToSpeech.QUEUE_ADD,null,null);
        } else {
            tts.speak(explanation, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public void setChoiceAmount(){
        if(choiceArray != null){
            for (int i = 0; i < choiceArray.length; i++) {
                choiceArray[i].setVisibility(View.GONE);
            }
        }
        choiceArray = new Button[choices.size()];
        for (int i = 0; i < choiceArray.length; i++) {
            String buttonID = "choice" + (i + 1);
            String[] arr = choices.toArray(new String[choices.size()]);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            choiceArray[i] = findViewById(resID);
            choiceArray[i].setBackgroundResource(R.drawable.custom_button);
            choiceArray[i].setVisibility(View.VISIBLE);
            choiceArray[i].setText(arr[i]);
            choiceArray[i].setEnabled(true);
            choiceArray[i].setOnClickListener(this);
        }
        for (int i = 0; i < choiceArray.length; i++) {
            if(choiceArray[i].getText().equals("")){
                choiceArray[i].setVisibility(View.GONE);
            }
        }
    }

    public void showTooltip(View v, int gravity, int id){
        explain();
        Tooltip.Builder tipInfo = new Tooltip.Builder(answerInfo).setText(explanation)
                .setTextColor(getResources().getColor(R.color.black)).setGravity(gravity).setCornerRadius(16f).setDismissOnClick(true)
                .setCancelable(true).setBackgroundColor(getResources().getColor(R.color.orange));

        tipInfo.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                tts.stop();
            }
        });
        if(tipInfo.build().isShowing()){
            tipInfo.build().dismiss();
        } else {
            tipInfo.show();
        }
    }

    public void onClick(View v){
        if(tts.isSpeaking()){
            tts.stop();
        }
        answerInfo.setVisibility(View.VISIBLE);
        answerInfo.setEnabled(true);
        Button btn = findViewById(v.getId());
        Button btn1 = findViewById(R.id.choice1);
        Button btn2 = findViewById(R.id.choice2);
        Button btn3 = findViewById(R.id.choice3);
        Button btn4 = findViewById(R.id.choice4);
        Button btn5 = findViewById(R.id.choice5);
        if(btn.getText().equals(answer)){
            score++;
            btn.setBackgroundResource(R.drawable.button_correct);
            speakCorrect();
        } else {
            btn.setBackgroundResource(R.drawable.button_wrong);
            speakWrong();
            if(btn1.getText().equals(answer)){
                btn1.setBackgroundResource(R.drawable.button_correct);
            } else if (btn2.getText().equals(answer)){
                btn2.setBackgroundResource(R.drawable.button_correct);
            } else if (btn3.getText().equals(answer)){
                btn3.setBackgroundResource(R.drawable.button_correct);
            } else if (btn4.getText().equals(answer)){
                btn4.setBackgroundResource(R.drawable.button_correct);
            } else {
                btn5.setBackgroundResource(R.drawable.button_correct);
            }
        }
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btn4.setEnabled(false);
        btn5.setEnabled(false);
    }

    public void speakCorrect(){
        String results = "correct! please say, explain, to hear an explanation," +
                "or say, next, to go to the next question.";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(results, TextToSpeech.QUEUE_ADD,null,null);
        } else {
            tts.speak(results, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public void speakWrong(){
        String results = "please try again.";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(results, TextToSpeech.QUEUE_ADD,null,null);
        } else {
            tts.speak(results, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.topic_button) {
            Intent intent = new Intent(this, TopicActivity.class);
            startActivity(intent);
        } else if(id == R.id.voice_control) {

        } else {

        }
        return super.onOptionsItemSelected(item);
    }
}