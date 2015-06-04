package de.herolabs.square;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import de.herolabs.square.game.Character;
import de.herolabs.square.game.pathTile;

public class TutorialActivity extends Activity implements OnClickListener, Runnable {

    public static final int SIZE = 10;
    private final pathTile[][] TILES = new pathTile[SIZE][SIZE];

    private TextView roundTextView;
    private ImageView gridImageView;
    private android.os.Handler timeHandler = new android.os.Handler();
    private RelativeLayout mainRelativeLayout;
    private ImageButton upButton;
    private ImageButton leftButton;
    private FrameLayout timerBar;
    private ImageView character;
    private int nonModuleViews;
    private int width;
    private float density;
    private boolean isRunning = true;
    private Integer round;
    private Integer tutDialogSlide = 0;
    private int markerX;
    private int markerY;
    private int tileSize;
    private int numberSize;
    private int tick;
    private int time;
    private float sizePerTime;
    private Character player = new Character(0, SIZE);
    private int margin;
    private Dialog tutorial;
    private ImageView tutorialImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tutorial = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        tutorial.setContentView(R.layout.dialog_tutorial);
        final ImageButton nextBtn = (ImageButton) tutorial.findViewById(R.id.nextButton);
        tutorialImageView = (ImageView) tutorial.findViewById(R.id.tutorialImageView);
        nextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                        onClickNextBtn();
            }
        });
        tutorial.show();

        density = getResources().getDisplayMetrics().density;

        margin = dpToPx(20);

        gridImageView = (ImageView) findViewById(R.id.gridImage);
        roundTextView = (TextView) findViewById(R.id.roundTextView);
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.playingfield);
        width = getResources().getDisplayMetrics().widthPixels;

        tileSize = (int) Math.round(((width - dpToPx(40)) / 10) * 0.9);
        numberSize = Math.round((width - dpToPx(40)) / 10);

        RelativeLayout.LayoutParams giParams = new RelativeLayout.LayoutParams(0, 0);
        giParams.width = tileSize * 10;
        giParams.height = giParams.width;
        giParams.topMargin = giParams.topMargin + numberSize;
        gridImageView.setLayoutParams(giParams);

        RelativeLayout.LayoutParams chParams = new RelativeLayout.LayoutParams(0, 0);
        chParams.width = tileSize;
        chParams.height = chParams.width;
        chParams.leftMargin = 0;
        chParams.topMargin = tileSize * 9;
        character = new ImageView(this);
        character.setImageResource(R.drawable.characterred);
        mainRelativeLayout.addView(character, chParams);

        upButton = (ImageButton) findViewById(R.id.playBtn);
        leftButton = (ImageButton) findViewById(R.id.rightBtn);

        nonModuleViews = 5;

        startGame();
    }

    public void onClickNextBtn() {
        tutDialogSlide++;
        if(tutDialogSlide >= 3) {
            tutorial.dismiss();
        } else {
            tutorialImageView.setImageResource(getResources().getIdentifier
                    ("tutorial" + tutDialogSlide.toString(), "drawable", this.getPackageName()));
            Log.i("debug", ""+tutDialogSlide);
        }

    }

    private void startGame() {
        isRunning = false;
        round = 0;
        startRound();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }*/
        return super.onKeyDown(keyCode, event);
    }

    private void startRound() {
        round++;

        tick = 0;

        roundTextView.setText(round.toString());

        if (round < 20) {
            time = Math.round(100 - round * 3);
        }
        timerBar = (FrameLayout) findViewById(R.id.timerBar);
        RelativeLayout.LayoutParams tbParams = new RelativeLayout.LayoutParams(0, 0);
        tbParams.leftMargin = width - (numberSize - dpToPx(10)) - dpToPx(40);
        tbParams.width = numberSize - dpToPx(10);
        tbParams.height = tileSize * 10;
        tbParams.topMargin = numberSize;
        timerBar.setLayoutParams(tbParams);
        sizePerTime = tbParams.height / (100 - tick);

        if (round > 1) {
            for (int i = mainRelativeLayout.getChildCount(); i > nonModuleViews; i--) {
                mainRelativeLayout.removeView(mainRelativeLayout.getChildAt(i));
                timeHandler.removeCallbacks(this);
            }
        }

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                TILES[x][y] = new pathTile(false);
            }
        }

        generatePath();

        RelativeLayout.LayoutParams chParams =
                (RelativeLayout.LayoutParams) character.getLayoutParams();
        chParams.leftMargin = 0;
        chParams.topMargin = Math.round(tileSize * 9) + numberSize;
        character.setLayoutParams(chParams);

        player.setPosX(0);
        player.setPosY(SIZE - 1);

        tickTime();
    }

    private void generatePath() {
        markerX = 0;
        markerY = SIZE - 1;
        boolean index;
        Random random = new Random();
        while (markerX < SIZE - 1 || markerY > 0) {
            index = random.nextBoolean();
            if (index) {                    //right
                if (markerX < SIZE - 1) {
                    markerX++;
                } else {
                    markerY--;
                }
            } else {                            //up
                if (markerY > 0) {
                    markerY--;
                } else {
                    markerX++;
                }
            }
            TILES[markerX][markerY].setPassable(true);
            TILES[0][SIZE - 1].setPassable(true);
        }
        generateModules();
    }

    public void generateModules() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (TILES[x][y].isPassable()) {
                    generateModuleNumbers(x, true, y + 1);
                    y = SIZE;
                }
            }
        }
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (TILES[x][y].isPassable()) {
                    x = SIZE;
                }
            }
        }
    }

    ;

    public void generateModuleNumbers(int pos, boolean loc /*true=x,false=y*/, Integer number) {
        if (loc) {
            RelativeLayout.LayoutParams nuParams = new RelativeLayout.LayoutParams(0, 0);
            nuParams.width = tileSize;
            nuParams.height = numberSize;
            nuParams.leftMargin = pos * tileSize;
            nuParams.topMargin = 0;
            ImageView xNumber = new ImageView(this);
            xNumber.setImageResource(getResources().getIdentifier("number" + number.toString(), "drawable", this.getPackageName()));
            mainRelativeLayout.addView(xNumber, nuParams);
        } else {
            RelativeLayout.LayoutParams nuParams = new RelativeLayout.LayoutParams(0, 0);
            nuParams.width = numberSize;
            nuParams.height = tileSize;
            nuParams.leftMargin = tileSize * 10;
            nuParams.topMargin = pos * tileSize + numberSize;
            ImageView yNumber = new ImageView(this);
            yNumber.setImageResource(getResources().getIdentifier("number" + number.toString(), "drawable", this.getPackageName()));
            mainRelativeLayout.addView(yNumber, nuParams);
        }
    }


    public void onClickRightBtn(View v) {
        player.moveRight();
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) character.getLayoutParams();
        params.leftMargin = Math.round((player.getPosX()) * tileSize);
        character.setLayoutParams(params);
        if (player.getPosY() == 0 && player.getPosX() == TutorialActivity.SIZE - 1) {
            startRound();
        }
        if (!TILES[player.getPosX()][player.getPosY()].isPassable()) {
            gameOver();
        }
    }


    public void onClickUpBtn(View v) {
        player.moveUp();
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) character.getLayoutParams();
        params.topMargin = Math.round((player.getPosY()) * tileSize) + numberSize;
        character.setLayoutParams(params);
        if (player.getPosY() == 0 && player.getPosX() == TutorialActivity.SIZE - 1) {
            startRound();
        } else {
            if (!TILES[player.getPosX()][player.getPosY()].isPassable()) {
                gameOver();
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    private int dpToPx(float dp) {
        return Math.round(dp * density);
    }

    @Override
    public void run() {
        tickTime();
    }

    private void tickTime() {
        RelativeLayout.LayoutParams tbParams = (RelativeLayout.LayoutParams) timerBar.getLayoutParams();
        if(tick < 100) {
            sizePerTime = tbParams.height / (100 - tick);
        }
        tbParams.height = tbParams.height - (int) sizePerTime;
        tbParams.topMargin = tbParams.topMargin + (int) sizePerTime;
        timerBar.setLayoutParams(tbParams);
        if (isRunning) {
            tick++;
            if (tick >= 99) {
                gameOver();
            }
            timeHandler.postDelayed(this, time);
        }
    }

    public void gameOver() {
        isRunning = false;
        timeHandler.removeCallbacks(this);
        setResult(0);
        final Dialog gameover = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        gameover.setContentView(R.layout.dialog_gameover);
        gameover.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        gameover.show();
        TextView scoreTextView = (TextView) gameover.findViewById(R.id.scoreTextView);
        scoreTextView.setTextSize(30);
        scoreTextView.setText("Score: Not counted");
        ImageButton replayBtn = (ImageButton) gameover.findViewById(R.id.replayButton);
        ImageButton homeBtn = (ImageButton) gameover.findViewById(R.id.homeButton);
        replayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gameover.dismiss();
                for (int i = mainRelativeLayout.getChildCount(); i > nonModuleViews; i--) {
                    mainRelativeLayout.removeView(mainRelativeLayout.getChildAt(i));
                }
                startGame();
            }
        });
        homeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gameover.dismiss();
                finish();
            }
        });
    }
}
