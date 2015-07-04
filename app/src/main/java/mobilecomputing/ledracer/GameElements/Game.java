package mobilecomputing.ledracer.GameElements;

import android.util.Log;

import mobilecomputing.ledracer.LEDConnMachine.LEDMatrixPainter;
import mobilecomputing.ledracer.LEDConnMachine.LEDMatrixRemote;

/**
 * Created by Johannes on 02.03.2015.
 */
public class Game implements LEDMatrixPainter {

    public enum GameState {
        CountDown,
        Driving,
        Crashed,
        Over
    }

    private static final String DEBUG_TAG = "Game";

    //in ms
    private static final int LOADING_TIME = 3000;
    private static final int CRASH_ANIMATION_TIME = 3000;
    private static final int CRASH_DURATION = 3500; // > Crash animation time!!
    private static final byte CRASH_COLOR = (byte)255;

    private static final byte SCORE_COLOR = (byte)255;
    private static final int SCORE_Y_POS = 7;
    private static final int SCORE_LABEL_POS_X = 1;
    private static final int SCORE_LABEL_POS_Y = 0;



    private GameHandler handler;
    private boolean reportedEndOfGame = false;
    private LEDMatrixRemote remote;
    private Car car;
    private long level;
    private LevelCreator levelCreator;
    private GameState state;
    private int currentScore = 0;

    private long animationStartTime = 0;
    private byte[] levelCopy;

    public Game(long level, LEDMatrixRemote remote) {
        this.remote = remote;
        this.remote.setPainter(this);
        this.level = level;

        this.initializeGameElements();
    }

    private void initializeGameElements() {
        this.remote.setUsingPainter(true);

        this.levelCreator = new LevelCreator(level, remote.getWidth(), remote.getHeight());

        this.car = new Car();
        this.car.setLeft((remote.getWidth() - Car.WIDTH()) / 2);
        this.car.setBot(remote.getHeight());
        this.car.changeY(-1, 0, remote.getHeight());

        this.reportedEndOfGame = false;
        this.currentScore = 0;
        this.animationStartTime = 0;
        this.levelCopy = null;
        this.state = GameState.CountDown;
    }

    public void restart() {
        this.initializeGameElements();
        this.start();
    }

    public boolean start() {
        //if paused during  race, redo countdown!
        if( this.state == GameState.Driving) {
            this.state = GameState.CountDown;
        }//it's possible that the animation finished already and thus painter was disabled
        else if(this.state == GameState.Over) {
            this.remote.setUsingPainter(true);
        }

        //if during an animation, restart animation
        this.animationStartTime = 0;

        //if connection is already established it remains.
        return this.remote.startConnection();
    }

    public void stop() {
        this.remote.stopConnection();
    }

    public void setHandler(GameHandler handler) {
        this.handler = handler;
    }

    @Override
    public void update(byte[] image, int width, int height, long updateCount) {
        if(this.state == GameState.CountDown) {
            this.updateCounting(image, width, height, updateCount);
        } else if(this.state == GameState.Driving) {
            this.updateDriving(image, width, height, updateCount);
        } else if(this.state == GameState.Crashed) {
            this.updateCrash(image, width, height, updateCount);
        } else {
            this.drawScore(image, width, height, updateCount);
        }
    }

    private void updateCounting(byte[] image, int width, int height, long updateCount) {
        //init counting, game started
        if(this.animationStartTime == 0) {
            this.animationStartTime = System.currentTimeMillis();
            this.setLevelCopy();
        }


        long diffMS = System.currentTimeMillis() - this.animationStartTime;
        if(diffMS < Game.LOADING_TIME) {
            int ex = (int)((width - 1) * (((double)diffMS) / Game.LOADING_TIME));
            int ey = (int)((height - 1) * (((double)diffMS) / Game.LOADING_TIME));
            //show loading
            for(int y = 0; y <= ey; y++) {
                for(int x = 0; x <= ex; x++) {
                    image[this.remote.convertCoordinates(x, y)] = this.levelCopy[this.remote.convertCoordinates(x,y)];
                }
            }
        } else {
            //stop counting and start game
            this.animationStartTime = 0;
            this.levelCopy = null;
            this.state = Game.GameState.Driving;
        }
    }
    private void updateDriving(byte[] image, int width, int height, long updateCount) {
        boolean crashed = false;

        //update world
        this.levelCreator.calcNextState();
        byte[] newImage = this.levelCreator.getCurrentState();

        //update car, doesn't use insertImage because of integrated collision detection.
        for (int y = 0; y < Car.HEIGHT(); y++) {
            for (int x = 0; x < Car.WIDTH(); x++) {
                int posInImage = (this.car.getTop() + y) * width + this.car.getLeft() + x;
                int posInCar = y * Car.WIDTH() + x;

                if (newImage[posInImage] != 0 && Car.IMG[posInCar] != 0) {
                    crashed = true;
                }

                newImage[posInImage] = Car.IMG[posInCar];
            }
        }

        if(crashed) {
            this.animationStartTime = 0;
            this.state = Game.GameState.Crashed;
        } else {
            this.currentScore++;

            if(this.handler != null) this.handler.scoreUpdate(this.currentScore);
        }

        //update the image.
        System.arraycopy(newImage, 0, image, 0, image.length);
    }
    private void updateCrash(byte[] image, int width, int height, long updateCount) {
        //initialise crash animation
        if(this.animationStartTime == 0) {
            this.animationStartTime = System.currentTimeMillis();

            this.setLevelCopy();
            System.arraycopy(this.levelCopy, 0, image, 0, image.length);

            return;
        }

        long diffMS = System.currentTimeMillis() - this.animationStartTime;
        if(diffMS < Game.CRASH_DURATION) {
            int edx = (int)((width + 1) * (((double)diffMS) / Game.CRASH_ANIMATION_TIME) / 2);
            int edy = (int)((height + 1) * (((double)diffMS) / Game.CRASH_ANIMATION_TIME) / 2);

            edx = Math.min(edx, (width + 1) / 2);
            edy = Math.min(edy, (width + 1) / 2);

            //show loading
            for(int dy = 0; dy < edy; dy++) {
                for(int x = 0; x < width; x++) {
                    image[this.remote.convertCoordinates(x, dy)] = Game.CRASH_COLOR;
                    image[this.remote.convertCoordinates(x, height - 1 - dy)] = Game.CRASH_COLOR;
                }
            }

            for(int y = 0; y < height; y++) {
                for(int dx = 0; dx < edx; dx++) {
                    image[this.remote.convertCoordinates(dx, y)] = Game.CRASH_COLOR;
                    image[this.remote.convertCoordinates(width - 1 - dx, y)] = Game.CRASH_COLOR;
                }
            }
        } else {
            //stop crash animation and end game
            this.remote.clearImage();
            this.animationStartTime = 0;
            this.levelCopy = null;
            this.state = Game.GameState.Over;
        }
    }

    private void drawScore(byte[] image, int width, int height, long updateCount) {

        //draw score label
        this.insertImage(image, ImageCollection.GET_SCORE_IMAGE(SCORE_COLOR), SCORE_LABEL_POS_X, SCORE_LABEL_POS_Y, width, ImageCollection.SCORE_WIDTH, ImageCollection.SCORE_HEIGHT);

        //draw score
        int len = ImageCollection.CALC_LENGTH(this.currentScore);
        int scoreCopy = this.currentScore;

        int left = width / 2;
        for(int i = 2; i >= 0 ; i--) {
            if (len <= 2 - i) break;

            byte[] digit = ImageCollection.GET_DIGIT_IMG(scoreCopy % 10, Game.SCORE_COLOR);
            scoreCopy /= 10;

            this.insertImage(image, digit, left + i * (ImageCollection.DIGIT_WIDTH + 1), SCORE_Y_POS, width, ImageCollection.DIGIT_WIDTH, ImageCollection.DIGIT_HEIGHT);
        }

        int right = width / 2;
        for(int i = 0; i < 3 ; i++) {
            if (len - 3 <= i) break;

            byte[] digit = ImageCollection.GET_DIGIT_IMG(scoreCopy % 10, Game.SCORE_COLOR);
            scoreCopy /= 10;

            this.insertImage(image, digit, right - i * (ImageCollection.DIGIT_WIDTH + 1) - ImageCollection.DIGIT_WIDTH, SCORE_Y_POS, width, ImageCollection.DIGIT_WIDTH, ImageCollection.DIGIT_HEIGHT);
        }

        Log.d(Game.DEBUG_TAG, String.format("Score: %d", this.currentScore));

        //turn painter off <=> freeze score image by stop updating image.
        this.remote.setUsingPainter(false);

        //tell game finished
        if(this.handler != null && !this.reportedEndOfGame) {
            this.reportedEndOfGame = true;
            this.handler.gameFinished(this.level, this.currentScore);
        }
    }

    private void setLevelCopy() {
        this.levelCopy = this.levelCreator.getCurrentState();
        this.insertImage(this.levelCopy, Car.IMG, this.car.getLeft(), this.car.getTop(), this.remote.getWidth(), Car.WIDTH(), Car.HEIGHT());
    }

    private void insertImage(byte[] dest, byte[] source, int px, int py, int dWidth, int sWidth, int sHeight) {
        for (int y = 0; y < sHeight; y++) {
            for (int x = 0; x < sWidth; x++) {
                dest[(py + y) * dWidth + px + x] = source[y * sWidth + x];
            }
        }
    }

    public void changeHorizontalCarPos(int dX) {
        if(this.state == GameState.Driving) {
            this.car.changeX(dX, 0, this.remote.getWidth());
        }
    }

    public void changeVerticalCarPos(int dY) {
        if(this.state == GameState.Driving) {
            this.car.changeY(dY, 0, this.remote.getHeight());
        }
    }
}
