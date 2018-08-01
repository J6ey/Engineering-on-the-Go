package com.engotg.creator.engotg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tooltip.Tooltip;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import io.paperdb.Paper;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private Button next;
    private TextView questionsLeft, questionText;
    private HashSet<String> choices;
    private String answer, explanation, title, question, topic;
    private Button[] choiceArray;
    private Integer[] randomQuestions;
    private ImageButton answerInfo;
    private int score;
    private int questionLength, currentNum, setVal, topicVal;

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

        // Must be declared for each question
        setChoiceAmount();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerInfo.setVisibility(View.GONE);
                if(Paper.book().read(topic + "|" +
                        "set " + setVal + "|" + "Questions|" + currentNum) != null){
                    String question = Paper.book().read(topic + '|' +
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
                    questionsLeft.setText(currentNum + "/" + questionLength);
                } else {
                    next.setVisibility(View.GONE);
                    questionsLeft.setVisibility(View.INVISIBLE);
                    questionText.setVisibility(View.INVISIBLE);
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
                    Button tryAgain = findViewById(R.id.choice2);
                    tryAgain.setEnabled(true);
                    tryAgain.setBackgroundResource(R.drawable.custom_button);
                    tryAgain.setText("Try Again");
                    tryAgain.setVisibility(View.VISIBLE);
                    Button menu = findViewById(R.id.choice3);
                    menu.setBackgroundResource(R.drawable.custom_button);
                    menu.setText("Back to Menu");
                    menu.setEnabled(true);
                    menu.setVisibility(View.VISIBLE);
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
                        showTooltip(v, Gravity.TOP, v.getId());
                    }
                });
            }
        });

        answerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTooltip(v, Gravity.TOP, v.getId());
            }
        });

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
            answerInfo = findViewById(R.id.expButton);
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
        Tooltip.Builder tipInfo = new Tooltip.Builder(answerInfo).setText(explanation)
                .setTextColor(getResources().getColor(R.color.black)).setGravity(gravity).setCornerRadius(16f).setDismissOnClick(true)
                .setCancelable(true).setBackgroundColor(getResources().getColor(R.color.orange));

        if(tipInfo.build().isShowing()){
            tipInfo.build().dismiss();
        } else {
            tipInfo.show();
        }
    }

    public void onClick(View v){
        answerInfo.setVisibility(View.VISIBLE);
        Button btn = findViewById(v.getId());
        Button btn1 = findViewById(R.id.choice1);
        Button btn2 = findViewById(R.id.choice2);
        Button btn3 = findViewById(R.id.choice3);
        Button btn4 = findViewById(R.id.choice4);
        Button btn5 = findViewById(R.id.choice5);
        if(btn.getText().equals(answer)){
            score++;
            btn.setBackgroundResource(R.drawable.button_correct);
        } else {
            btn.setBackgroundResource(R.drawable.button_wrong);
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