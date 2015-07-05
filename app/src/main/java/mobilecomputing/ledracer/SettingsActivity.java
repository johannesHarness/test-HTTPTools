package mobilecomputing.ledracer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import mobilecomputing.ledracer.Database.LEDRacerDatabase;
import mobilecomputing.ledracer.Other.Settings;


public class SettingsActivity extends Activity {


    private Spinner spBTName;
    private EditText txtHistLen;
    private TextView tvBTNameInfo;

    private ArrayAdapter<String> adapterBTName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.initBTName();
        this.initHistoryLength();
    }

    private void initBTName(){

        this.tvBTNameInfo = (TextView)findViewById(R.id.tvNameOfDevice);

        this.spBTName = (Spinner)findViewById(R.id.txtNameOfDevice);
        this.spBTName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spBTName.getItemAtPosition(i) != null) {
                    Settings.GET_INSTANCE().setBTDeviceName(spBTName.getItemAtPosition(i).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapterBTName = new ArrayAdapter<String>(this, R.layout.spinner_item);
        boolean error = false;
        int c = 0;
        try {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btAdapter != null) {
                //try to add bluetooth devices as suggested names
                for (BluetoothDevice device : btAdapter.getBondedDevices()) {
                    adapterBTName.add(device.getName());
                    c++;
                }
            } else {
                error = true;
            }
        } catch (Exception ex) {
            Log.e("Settings", "Error retrieving list of bluetooth devices!!!");
            error = true;
        }

        if(error || c == 0) {
            this.spBTName.setEnabled(false);
            this.tvBTNameInfo.setText(R.string.txt_settings_btdevice_error);
        }
        else {
            String currentValue = Settings.GET_INSTANCE().getBTDeviceName();
            int idx = this.adapterBTName.getPosition(currentValue);

            //if old device isn't paired anymore, set to first device (which exists because c > 0)
            if(idx < 0) idx = 0;

            this.spBTName.setAdapter(adapterBTName);
            this.spBTName.setSelection(idx);
        }
    }

    private void initHistoryLength() {

        this.txtHistLen = (EditText)findViewById(R.id.txtHistoryLength);
        this.txtHistLen.setText(String.format("%d", Settings.GET_INSTANCE().getHistoryLength()));
        this.txtHistLen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int len = -1;
                try {
                    len = Integer.parseInt(editable.toString());
                } catch (NumberFormatException ex) {
                    len = -1;
                }
                if(len > 0) {
                    Settings.GET_INSTANCE().setHistoryLength(len);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
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

    public void btnResetDatabase_Click(View view) {
        LEDRacerDatabase.GET_INSTANCE().clearHistory();
    }
}
