package mobilecomputing.ledracer.GameElements;

import android.util.Log;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Johannes on 02.03.2015.
 */
public class LevelCreator {

    private final static String DEBUG_TAG = "LevelCreator";

    private final static int[] DIFFICULTY_STEPS = {0, 10, 30, 60, 100, 200, 500, 1000 };
    private final static int HIGHEST_DIFFICULTY = DIFFICULTY_STEPS.length - 1;
    private final static int MULTIPLE_PATH_MIN_DIFFICULTY = (DIFFICULTY_STEPS.length + 1) / 2;
    private final static double ADD_PATH_POSSIBILITY = 0.30;
    private final static double CHANGE_PATH_DIR_POSSIBILITY = 0.10;
    private final static double CHANGE_BORDER_PATH_DIR_POSSIBILITY = 0.50; // change dir faster in order to avoid long straight paths at the edge of the world
    private final static int LOWEST_MIN_WIDTH = 4;
    public final static byte COLOR = (byte)180;

    private Random rand;
    private int width;
    private int height;
    private byte[] curState;
    private int difficulty;
    private int minWidth;
    private int maxWidth;
    private boolean directionOfPath; //true = right, false = left;
    private int amountOfStepsNotRemovingSingleBlocks = 0;

    private int lastDiffResult = 0;

    public LevelCreator(long level, int width, int height) {
        this.rand = new Random(level);
        this.width = width;
        this.height = height;

        this.initializeMapProperties(level);
        this.initializeMap();
    }

    private void initializeMapProperties(long level) {
        //calculate class
        for(int i = LevelCreator.HIGHEST_DIFFICULTY; i >= 0; i--) {
            if(LevelCreator.DIFFICULTY_STEPS[i] <= level) {
                this.difficulty = i;
                break;
            }
        }

        Log.d(LevelCreator.DEBUG_TAG, String.format("add path difficulty: %d", LevelCreator.MULTIPLE_PATH_MIN_DIFFICULTY));
        Log.d(LevelCreator.DEBUG_TAG, String.format("difficulty: %d", this.difficulty));

        this.minWidth = LevelCreator.LOWEST_MIN_WIDTH + (LevelCreator.HIGHEST_DIFFICULTY - difficulty);
        this.maxWidth = this.width - 2; //Math.max(Math.min(this.width - 2, 2 * this.minWidth), this.width - 2 - level);
    }

    private void initializeMap() {
        this.curState = new byte[this.width * this.height];
        Arrays.fill(this.curState, (byte)0);
        for(int y = 0; y < this.height; y++) {
            for (int x = 0; x < (this.width - this.minWidth) / 2; x++) {
                this.curState[y * this.width + x] = LevelCreator.COLOR;
                this.curState[y * this.width + this.width - x - 1] = LevelCreator.COLOR;
            }
        }
    }

    public byte[] getCurrentState() {
        return Arrays.copyOf(this.curState, this.curState.length);
    }

    public void calcNextState() {
        //get first line
        byte[] firstLine = Arrays.copyOfRange(this.curState, 0, this.width);

        //move level one line down
        for(int i = this.width * this.height - 1; i >= this.width; i--) this.curState[i] = this.curState[i - this.width];

        //create new line
        firstLine = this.getNextLine(firstLine);

        //insert new first line
        for(int i = 0; i < this.width; i++) this.curState[i] = firstLine[i];
    }

    private byte[] getNextLine(byte[] oldLine) {
        byte[] newLine = Arrays.copyOf(oldLine, oldLine.length);

        this.amountOfStepsNotRemovingSingleBlocks = Math.max(0, this.amountOfStepsNotRemovingSingleBlocks - 1);

        int start = -1; int len = 0; int diff;
        for(int i = 1; i < newLine.length; i++) {
            if(oldLine[i] == 0 && start < 0) start = i;
            if(oldLine[i] != 0 && start > -1) {
                len = i-start;
                diff = getNextDiff();

                //randomly reset direction
                if(this.rand.nextDouble() < LevelCreator.CHANGE_PATH_DIR_POSSIBILITY) {
                    this.directionOfPath = rand.nextBoolean();
                }


                //Log.d(LevelCreator.DEBUG_TAG, String.format("diff: %d", diff));
                //Log.d(LevelCreator.DEBUG_TAG, String.format("length: %d", len));



                if(this.minWidth > len + diff) diff = this.minWidth - len;
                else if(this.maxWidth < len + diff) diff = this.maxWidth - len;


                //Log.d(LevelCreator.DEBUG_TAG, String.format("new Diff: %d", diff));


                while(diff < 0) {
                    double r = rand.nextDouble();
                    if(r >= LevelCreator.ADD_PATH_POSSIBILITY || (len < 2 * this.minWidth + 1) || this.difficulty < LevelCreator.MULTIPLE_PATH_MIN_DIFFICULTY) {
                        if(directionOfPath) {
                            newLine[start] = LevelCreator.COLOR;
                            start++;
                        } else {
                            newLine[start + len - 1] = LevelCreator.COLOR;
                        }
                    } else {
                        //split path in two parts,  and start again at beginning of the most left path
                        int idx = start + this.minWidth + (int)(rand.nextDouble() * (len - (2 * this.minWidth + 1)));
                        newLine[idx] = LevelCreator.COLOR;
                        oldLine[idx] = LevelCreator.COLOR;//insert new element also in oldpath in order to process correctly and keep width of paths >= MIN_WIDTH!!

                        this.amountOfStepsNotRemovingSingleBlocks = 3;
                        i = start-1;

                        break;
                    }
                    len--; diff++;
                }

                while(diff > 0) {
                    if(this.directionOfPath == false && start > 1) {
                        newLine[start-1] = 0;
                        start--;
                    } else if (this.directionOfPath && start + len < newLine.length - 1) {
                        newLine[start + len] = 0;
                    } else {
                        if(this.rand.nextDouble() < CHANGE_BORDER_PATH_DIR_POSSIBILITY) {
                            this.directionOfPath = !this.directionOfPath;
                        }
                        break;
                    };

                    len++; diff--;
                }

                start = -1;
            }
        }

        if(this.amountOfStepsNotRemovingSingleBlocks > 0) {
            for(int i = 2; i < oldLine.length - 1; i++) {
                if(oldLine[i] != 0 && oldLine[i-1] == 0 && oldLine[i+1] == 0) {
                    newLine[i] = oldLine[i];
                }
            }
        }

        return newLine;
    }

    private int getNextDiff() {
        double r = rand.nextDouble();
        r *= (1.0 + ((double)lastDiffResult) / 5);

        //Log.d(LevelCreator.DEBUG_TAG, String.format("random value: %f", r));

        /*if(this.amountOfStepsNotRemovingSingleBlocks > 0) {
            if (r < 0.4) this.lastDiffResult = -1;
            else this.lastDiffResult = 0;
        } else {*/
            if (r < 0.45) this.lastDiffResult = -1;
            else if (r < 0.6) this.lastDiffResult = 0;
            else this.lastDiffResult = 1;
        //}

        return this.lastDiffResult;
    }
}
