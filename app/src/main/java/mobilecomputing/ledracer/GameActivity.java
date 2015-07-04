package mobilecomputing.ledracer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import mobilecomputing.ledracer.Database.LEDRacerDatabase;
import mobilecomputing.ledracer.GameElements.GameHandler;


public class GameActivity extends Activity {

    private TextView tvScore;
    private LinearLayout ctrlContainer;
    private LinearLayout gameOverContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        this.tvScore = (TextView)findViewById(R.id.tvScore);
        this.ctrlContainer = (LinearLayout)findViewById(R.id.ctrlContainer);
        this.gameOverContainer = (LinearLayout)findViewById(R.id.gameOverContainer);

       this.initGame();
    }

    private void initGame() {

        this.tvScore.setText("0");

        this.ctrlContainer.setVisibility(View.VISIBLE);
        this.gameOverContainer.setVisibility(View.GONE);

        MainActivity.game.setHandler(new GameHandler(new Handler()) {
            @Override
            protected void handleScoreUpdate(int newScore) {
                tvScore.setText(String.format("%d", newScore));
            }

            @Override
            protected void handleGameFinished(long level, int finalScore) {
                ctrlContainer.setVisibility(View.GONE);
                gameOverContainer.setVisibility(View.VISIBLE);

                //add score to database
                LEDRacerDatabase.GET_INSTANCE().addResult(level, finalScore);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //isCalled by leaving app or by calling finish
    @Override
    public void onPause() {
        super.onPause();

        // Avoid crash if user exits the app before pressing start.
        this.stopGame();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.startGame();
    }

    public void btnRestart_Click(View view) {
        this.restartGame();
    }

    public void btnMenu_Click(View view) {
        this.finish();
    }

    private void startGame() {
        if(MainActivity.game != null) {
            MainActivity.game.start();
        }
    }

    //is called by onPause
    private void stopGame() {
        if(MainActivity.game != null) {
            MainActivity.game.stop();
        }
    }

    private void restartGame() {
        if(MainActivity.game != null) {
            this.initGame();
            MainActivity.game.restart();
        }
    }

    public void moveLeft(View view) {
        MainActivity.game.changeHorizontalCarPos(-1);
    }
    public void moveRight(View view) {
        MainActivity.game.changeHorizontalCarPos(1);
    }
}
