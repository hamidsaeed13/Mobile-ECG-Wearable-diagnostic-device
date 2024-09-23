package com.killer.mobileecg;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.androidplot.util.Redrawer;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;


import java.lang.ref.WeakReference;
import java.text.DecimalFormat;



public class MainActivity extends AppCompatActivity {
     static final int HISTORY_SIZE = 200;
     DataAppend DA=null;
     static SimpleXYSeries mSeries = null;
    private SimpleXYSeries mSeries1 = null;
    static CircularBuffer<String> Buff =null;
    private XYPlot plot = null;
    private Redrawer redrawer;

    static final int REQUEST_CONNECT_DEVICE = 1;
    static final int REQUEST_ENABLE_BT = 2;

    // Message types sent from the DeviceConnector Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private static BluetoothResponseHandler mHandler;
    private String deviceName;
    private static DeviceConnector connector;
    static DataAppend DP;

    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;

    BluetoothAdapter btAdapter;
    TextView txt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       txt = (TextView) findViewById(R.id.textview);
        Buff = new CircularBuffer(5000);
        DP= new DataAppend();
        new Thread(DP).start();
        if (mHandler == null) mHandler = new BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);


        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            final String no_bluetooth = getString(R.string.no_bt_support);
            showAlertDialog(no_bluetooth);

        }

        plot = (XYPlot) findViewById(R.id.plot);

        mSeries = new SimpleXYSeries("R");
        mSeries.useImplicitXVals();
        mSeries1 = new SimpleXYSeries("L");
        mSeries1.useImplicitXVals();
        plot.setRangeBoundaries(0, 1000, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
        plot.addSeries(mSeries,new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
        plot.addSeries(mSeries1, new LineAndPointFormatter(Color.rgb(200, 100, 100), null, null, null));
        plot.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        plot.setDomainStepValue(HISTORY_SIZE/10);
       // plot.setLinesPerRangeLabel(3);
        plot.setDomainLabel("Domain");
        plot.getDomainTitle().pack();
        plot.setRangeLabel("Range");
        plot.getRangeTitle().pack();
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("#"));

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("#"));

        redrawer = new Redrawer(plot,100,true);
        PanZoom.attach(plot);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    void Tst(String str)
    {
    Context context = getApplicationContext();
    CharSequence text = str;
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, text, duration);
    toast.show();
    }



/*
    String[] sepline;

    void dataAppend(final String messag) {

    try {
        runOnUiThread(new Runnable() {
            @Override

            public void run() {

                sepline=messag.split("\n");
                int i = 0;
                while(i<sepline.length)

                {
                    if (sepline[i].trim() == "") {
                        sepline[i] = "0";
                    }
                    if (isInteger(sepline[i].trim())) {


                        int num = Integer.parseInt(sepline[i].trim());

                        if (mSeries.size() > HISTORY_SIZE) {
                            mSeries.removeFirst();

                        }
                        mSeries.addLast(null, num);
                    }

                    i++;
                }
            }


        });
    }
    catch(Exception e){
        return ;
    }

    }
   */



    private void startDeviceListActivity() {
        stopConnection();
       Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    private void stopConnection() {
        if (connector != null) {
            connector.stop();
            connector = null;
            deviceName = null;
        }
    }


    void showAlertDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.app_name));
        alertDialogBuilder.setMessage(message);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            if (btAdapter == null) {
                final String no_bluetooth = getString(R.string.no_bt_support);
                showAlertDialog(no_bluetooth);
            }
            else {
                if (btAdapter.isEnabled()) {

                    startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    boolean isAdapterReady() {
        return (btAdapter != null) && (btAdapter.isEnabled());
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    if (isAdapterReady() && (connector == null)) setupConnector(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode != Activity.RESULT_OK) {
                    Utils.log("BT not enabled");
                }
                break;
        }
    }

    private void setupConnector(BluetoothDevice connectedDevice) {
        stopConnection();
        try {
            String emptyName = getString(R.string.empty_device_name);
            DeviceData data = new DeviceData(connectedDevice, emptyName);
            connector = new DeviceConnector(data, mHandler);
            connector.connect();
        } catch (IllegalArgumentException e) {
            Utils.log("setupConnector failed: " + e.getMessage());
        }
    }


    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
    }



    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<MainActivity> mActivity;

        public BluetoothResponseHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        public void setTarget(MainActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<MainActivity>(target);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:

                        Utils.log("MESSAGE_STATE_CHANGE: " + msg.arg1);
                        switch (msg.arg1) {
                            case DeviceConnector.STATE_CONNECTED:
                                activity.setDeviceName(MSG_CONNECTED);
                                break;
                            case DeviceConnector.STATE_CONNECTING:
                                activity.setDeviceName(MSG_CONNECTING);
                                break;
                            case DeviceConnector.STATE_NONE:
                                activity.setDeviceName(MSG_NOT_CONNECTED);
                                break;
                        }
                        break;
                        case MESSAGE_READ:
                        final String readMessage = (String) msg.obj;
                        if (readMessage != null) {
                            message = readMessage;
                            DataAppend r;
                           synchronized (this) {

                                r = DP;
                                r.start();
                            }
                            //Buff.store(readMessage);

                        }
                        break;

                    case MESSAGE_DEVICE_NAME:
                        activity.setDeviceName((String) msg.obj);
                        break;

                    case MESSAGE_WRITE:
                        // stub
                        break;

                    case MESSAGE_TOAST:

                        break;
                }
            }
        }


    }
    @Override
    public void onResume() {
        redrawer.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        redrawer.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        redrawer.finish();
        super.onDestroy();
    }



}

