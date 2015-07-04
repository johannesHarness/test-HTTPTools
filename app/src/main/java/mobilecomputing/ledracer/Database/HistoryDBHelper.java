package mobilecomputing.ledracer.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Johannes on 05.03.2015.
 */
public class HistoryDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LEDRacer.db";

    public HistoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        //Create table history
        db.execSQL(SQLCommands.CREATE_TABLE_HISTORY);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.clearDatabase(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.clearDatabase(db);
    }
    public void clearDatabase(SQLiteDatabase db) {
        db.execSQL(SQLCommands.DROP_TABLE_HISTORY);
        this.onCreate(db);
    }
}
