package mobilecomputing.ledracer.GameElements;

import android.os.Handler;

/**
 * Created by Johannes on 04.03.2015.
 */
public abstract class GameHandler {
    private Handler handler;

    public GameHandler(Handler handler) {
        this.handler = handler;
    }

    public void scoreUpdate(final int newScore) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                handleScoreUpdate(newScore);
            }
        });
    }

    public void gameFinished(final long level, final int finalScore) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                handleGameFinished(level, finalScore);
            }
        });
    }

    protected abstract void handleScoreUpdate(int newScore);
    protected abstract void handleGameFinished(long level, int finalScore);

}
