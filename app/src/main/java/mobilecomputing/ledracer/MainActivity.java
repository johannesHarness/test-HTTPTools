package mobilecomputing.ledracer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import mobilecomputing.ledracer.Database.LEDRacerDatabase;
import mobilecomputing.ledracer.GameElements.Game;
import mobilecomputing.ledracer.LEDConnMachine.LEDMatrixBTConn;
import mobilecomputing.ledracer.LEDConnMachine.LEDMatrixRemote;
import mobilecomputing.ledracer.Other.ListViewArrayAdapter;
import mobilecomputing.ledracer.Other.Settings;


public class MainActivity extends Activity {

    // The name this app uses to identify with the server.
    protected static final String APP_NAME = "LEDRacer";
    // Remote display x and y size.
    protected static final int X_SIZE = 24;
    protected static final int Y_SIZE = 24;
    // Remote display color mode. 0 = red, 1 = green, 2 = blue, 3 = RGB.
    protected static final int COLOR_MODE = 0;

    private static final String START_SETTINGS_CODE = "000";


    private static LEDMatrixBTConn BT;
    private static LEDMatrixRemote remote;
    public static Game game;


    private ListView lvHistory;
    private EditText txtLevel;
    private TextView txtInfo;
    private ImageButton btnStart;
    private ListViewArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LEDRacerDatabase.CREATE_INSTANCE(this);
        LEDRacerDatabase.GET_INSTANCE().open();

        Settings.CREATE_INSTANCE(this);

        this.lvHistory = (ListView)findViewById(R.id.lvHistory);
        this.btnStart = (ImageButton)findViewById(R.id.btnStart);
        this.txtLevel = (EditText)findViewById(R.id.txtLevel);
        this.txtInfo = (TextView)findViewById(R.id.tvInfo);


        this.txtInfo.setText(this.getString(R.string.txt_info_choose_lvl));
        this.adapter = new ListViewArrayAdapter(this, LEDRacerDatabase.GET_INSTANCE().getLastEntries(Settings.GET_INSTANCE().getHistoryLength()));
        ((ListView)findViewById(R.id.lvHistory)).setAdapter(this.adapter);

        //remove focus from keyboard at appstart
        this.txtInfo.requestFocus();

        this.txtLevel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) { }
            @Override
            public void afterTextChanged(Editable editable) {
                refreshHistory();
            }
        });
        this.txtLevel.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent!= null && keyEvent.getAction() == KeyEvent.ACTION_UP && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    Log.d("Enter", "enter pressed!");
                    startGame(view);
                    return true;
                }

                return false;
            }
        });
        this.lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selLvl = String.format("%d", adapter.getItem(i).getLevel());

                if(txtLevel.length() == 0 || !selLvl.equals(txtLevel.getText().toString())) {
                    txtLevel.setText(selLvl);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
    protected void onResume() {
        super.onResume();

        this.txtInfo.setText(this.getString(R.string.txt_info_choose_lvl));
        this.txtInfo.requestFocus();

        this.txtLevel.setText("");

        this.refreshHistory();
    }

    private void refreshHistory() {
        long level = -1;
        if(this.txtLevel.length() > 0) {
            try {
                level = Long.parseLong(txtLevel.getText().toString());
            } catch (NumberFormatException ex) {
                txtInfo.setText(this.getString(R.string.txt_info_illegal_level));
                return;
            }
        }

        Log.d("MainActivity", String.format("refresh history, new lvl: %d", level));

        this.txtInfo.setText(this.getString(R.string.txt_info_choose_lvl));


        if(level == -1) {
            this.adapter.clear();
            this.adapter.addAll(LEDRacerDatabase.GET_INSTANCE().getLastEntries(Settings.GET_INSTANCE().getHistoryLength()));
        } else {
            this.adapter.clear();
            this.adapter.addAll(LEDRacerDatabase.GET_INSTANCE().getLastEntriesForLevel(level, Settings.GET_INSTANCE().getHistoryLength()));
        }

        this.adapter.notifyDataSetChanged();
    }

    public void startGame(View view) {
        
        if(this.txtLevel.getText().length() > 0) {

            if(this.txtLevel.getText().toString().equals(START_SETTINGS_CODE)) {
                this.startActivity(new Intent(this, SettingsActivity.class));
                return;
            }


            final long level;
            try {
                level = Long.parseLong(txtLevel.getText().toString());
            } catch (NumberFormatException ex){
                txtInfo.setText(this.getString(R.string.txt_info_illegal_level));
                return;
            }

            this.txtInfo.setText(String.format(this.getString(R.string.txt_info_starting_game), level));
            this.btnStart.setEnabled(false);
            this.txtLevel.setEnabled(false);

            final Handler handler = new Handler();
            Thread thread = new Thread() {
                public void run() {
                    boolean start = true;
                    // Set up BT connection.
                    BT = new LEDMatrixBTConn(Settings.GET_INSTANCE().getBTDeviceName(), X_SIZE, Y_SIZE, COLOR_MODE, APP_NAME);

                    if (BT.prepare() != LEDMatrixBTConn.BTPrepResult.READY) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                txtInfo.setText(MainActivity.this.getString(R.string.txt_info_device_not_found));
                            }
                        });

                        start = false;
                    }

                    if (start) {
                        remote = new LEDMatrixRemote(BT, 125);
                        game = new Game(level, remote);

                        if (game.start()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.this.startActivity(new Intent(MainActivity.this, GameActivity.class));
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    txtInfo.setText(String.format(MainActivity.this.getString(R.string.txt_info_connection_troubles), Settings.GET_INSTANCE().getBTDeviceName()));
                                }
                            });
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            btnStart.setEnabled(true);
                            txtLevel.setEnabled(true);
                        }
                    });

                }
            };

            thread.start();

        } else {
            txtInfo.setText(this.getString(R.string.txt_info_no_level_entered));
        }
    }
}
