package mobilecomputing.ledracer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

import mobilecomputing.ledracer.Database.Tables.History;

/**
 * Created by Johannes on 05.03.2015.
 */
public class LEDRacerDatabase {
    private static LEDRacerDatabase INSTANCE;
    public static void CREATE_INSTANCE(Context context) { if(INSTANCE == null) { INSTANCE = new LEDRacerDatabase(context); } }
    public static LEDRacerDatabase GET_INSTANCE() { return INSTANCE; }

    private static final String[] HISTORY_PROJ = {History.LEVEL_NAME, History.SCORE_NAME, History.DATE_NAME};
    private static final String HISTORY_SORT_DATE_DESC = History.DATE_NAME + " DESC";
    private static final String HISTORY_SORT_SCORE_DESC = History.SCORE_NAME + " DESC";
    private static final String HISTORY_SELECT_LEVEL = History.LEVEL_NAME + " = ?";

    private HistoryDBHelper helper;
    private SQLiteDatabase db;

    private LEDRacerDatabase(Context context) {
        helper = new HistoryDBHelper(context);
    }

    public void open() {
        db = helper.getWritableDatabase();
    }

    public void addResult(long level, int score) {
        ContentValues val = new ContentValues();
        val.put(History.LEVEL_NAME, level);
        val.put(History.SCORE_NAME, score);
        val.put(History.DATE_NAME, System.currentTimeMillis());

        db.insert(History.NAME, "", val);
    }

    public ArrayList<History> getLastEntries(int maxAmount) {
        Cursor c = db.query(History.NAME, HISTORY_PROJ,null,null,null,null,HISTORY_SORT_DATE_DESC, String.format("%d", maxAmount));

        ArrayList<History> res = new ArrayList<History>();

        c.moveToFirst();
        while(!c.isAfterLast()) {
            res.add(new History(c.getLong(0), c.getInt(1), c.getLong(2)));
            c.moveToNext();
        }

        return res;
    }

    public ArrayList<History> getLastEntriesForLevel(long level, int maxAmount) {
        String[] selectArgs = {String.format("%d", level)};
        Cursor c = db.query(History.NAME, HISTORY_PROJ, HISTORY_SELECT_LEVEL, selectArgs,null,null,HISTORY_SORT_SCORE_DESC, String.format("%d", maxAmount));

        ArrayList<History> res = new ArrayList<History>();

        c.moveToFirst();
        while(!c.isAfterLast()) {
            res.add(new History(c.getLong(0), c.getInt(1), c.getLong(2)));
            c.moveToNext();
        }

        return res;
    }

    public void clearHistory() {
        this.helper.clearDatabase(this.db);
    }
}
