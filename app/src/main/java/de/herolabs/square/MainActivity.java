package de.herolabs.square;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    private ImageButton startButton;
    private ImageButton tutBtn;
    private TextView pointsTextView;
    private TextView highscoreTextView;
    private TextView welcomeTextView;
    private Animation animationFadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        View v = findViewById(R.id.rootLayout);
        v.startAnimation(animationFadeIn);

        startButton = (ImageButton) findViewById(R.id.playBtn);
        tutBtn = (ImageButton) findViewById(R.id.tutorialBtn);
        pointsTextView = (TextView) findViewById(R.id.pointsTextView);
        welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        highscoreTextView = (TextView) findViewById(R.id.highscoreTextView);
    }

    @Override
    public void onClick(View v) {
    }

    public void onClickPlay(View v) {
        startActivityForResult(new Intent(this, GameActivity.class), 1);
    }

    public void onClickTut(View v) {
        startActivity(new Intent(this, TutorialActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView points = (TextView) findViewById(R.id.pointsTextView);
        points.setText(Integer.toString(getHighscore()));
    }

    public int getHighscore() {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        return pref.getInt("HIGHSCORE", 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode > getHighscore()) {
                setHighscore(resultCode);
            }
        }
    }

    public void setHighscore(int highscore) {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("HIGHSCORE", highscore);
        editor.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
