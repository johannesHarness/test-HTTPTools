package mobilecomputing.ledracer.Database;

import mobilecomputing.ledracer.Database.Tables.History;

/**
 * Created by Johannes on 05.03.2015.
 */
public final class SQLCommands {

    protected static final String CREATE_TABLE_HISTORY =  "CREATE TABLE " + History.NAME + " (" +
            History._ID + " INTEGER PRIMARY KEY," +
            History.LEVEL_NAME + " INTEGER, " +
            History.SCORE_NAME + " INTEGER, " +
            History.DATE_NAME + " INTEGER NOT NULL)";

    protected static final String DROP_TABLE_HISTORY =
            "DROP TABLE IF EXISTS " + History.NAME;


    private SQLCommands() {}
}
