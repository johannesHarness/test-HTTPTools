package mobilecomputing.ledracer.Other;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Johannes on 06.03.2015.
 */
public class Settings {
    private static Settings INSTANCE;
    public static void CREATE_INSTANCE(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new Settings(context);
        }
    }
    public static Settings GET_INSTANCE() {
        return INSTANCE;
    }

    private static final String PREF_NAME = "LEDRacer_Preferences";
    //device name
    private static final String VAR_BT_DEVICE_NAME = "BTDeviceName";
    private static final String DEFAULT_BT_DEVICE_NAME = "ledpi-teco";
    //history length
    private static final String VAR_HISTORY_LENGTH = "HistoryLength";
    private static final int DEFAULT_HISTORY_LENGTH = 30;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Settings(Context context) {
        this.pref = context.getSharedPreferences(PREF_NAME, 0);
        this.editor = pref.edit();
    }

    public void setBTDeviceName(String name) {
        if(name == null || name.length() == 0) return;

        this.editor.putString(VAR_BT_DEVICE_NAME, name);
        this.editor.commit();
    }
    public String getBTDeviceName() {
        return this.pref.getString(VAR_BT_DEVICE_NAME, DEFAULT_BT_DEVICE_NAME);
    }

    public void setHistoryLength(int len) {
        if(len <= 0) return;

        this.editor.putInt(VAR_HISTORY_LENGTH, len);
        this.editor.commit();
    }
    public int getHistoryLength() {
        return this.pref.getInt(VAR_HISTORY_LENGTH, DEFAULT_HISTORY_LENGTH);
    }
}
