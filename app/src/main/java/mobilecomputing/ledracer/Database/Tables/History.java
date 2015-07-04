package mobilecomputing.ledracer.Database.Tables;

import android.provider.BaseColumns;

/**
 * Created by Johannes on 05.03.2015.
 */
public class History implements BaseColumns {
    public static final String NAME = "History";
    public static final String DATE_NAME = "Date";
    public static final String LEVEL_NAME = "Level";
    public static final String SCORE_NAME = "Score";

    private long date;
    private long level;
    private int score;

    public History(long level, int score, long date) {
        this.level = level;
        this.score = score;
        this.date = date;
    }

    public long getLevel() { return this.level; }
    public int getScore() { return this.score; }
    public long getDate() { return this.date; }
}