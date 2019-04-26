package com.sogou.xshuo.btvirtualcomm;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sogou.xshuo.btvirtualcomm.common.activities.ActivityBase;
import com.sogou.xshuo.btvirtualcomm.common.logger.Log;

import java.io.UnsupportedEncodingException;


public class MainActivity extends ActivityBase {
    public static final String TAG = "MainActivity";
    private static final int ACTION_ID_CHOOSE_DEVICE = 100;
    private static final int ACTION_ID_CHOOSE_DEVICE_SECURE = 101;
    private static final int ACTION_ID_DISCONNECT = 102;

    private BluetoothAdapter mBtAdapter = null;
    private BluetoothChatService mChatService = null;
    private String mConnectedDeviceName = "";
    private ListView mConversionListView = null;
    private Button mSendButton = null;
    private EditText mSendMsgEditText = null;
    private ArrayAdapter<String> mListViewAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "---onCreate()---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
            Toast.makeText(this, R.string.toast_bluetooth_not_available, Toast.LENGTH_LONG).show();
            finish();
        }
        mChatService = new BluetoothChatService(this, mUIHandler);
        mConversionListView = findViewById(R.id.in);
        mSendButton = findViewById(R.id.button_send);
        mSendMsgEditText = findViewById(R.id.edit_text_out);
        mListViewAdapter = new ArrayAdapter<>(this, R.layout.message);

        mConversionListView.setAdapter(mListViewAdapter);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBTMessage(mSendMsgEditText.getText().toString());
            }
        });

        mSendMsgEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // If the action is a key-up event on the return key, send the message
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                    sendBTMessage(v.getText().toString());
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "---onStart()---");
        super.onStart();
        if (mChatService != null)
            mChatService.start();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "---onResume()---");
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "---onCreate()---");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
         Log.d(TAG, "---onStart()---");
         super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "---onStop()---");
        super.onStop();
        if (mChatService != null)
            mChatService.stop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "---onDestroy()---");
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "---onConfigurationChanged()---");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    private void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
        device.fetchUuidsWithSdp();
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    private void sendBTMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            Log.d(TAG, "sendBTMessage: " + message);
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = new byte[0];
            try {
                send = message.getBytes("UTF-8");
                mChatService.write(send);
                mSendMsgEditText.getText().clear();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_ID_CHOOSE_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case ACTION_ID_CHOOSE_DEVICE_SECURE:
                 // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
       }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_id_choose_device:
                Intent choose_device = new Intent(this, DeviceListActivity.class);
                startActivityForResult(choose_device, ACTION_ID_CHOOSE_DEVICE);
                return true;
            case R.id.menu_id_choose_device_secure:
                choose_device = new Intent(this, DeviceListActivity.class);
                startActivityForResult(choose_device, ACTION_ID_CHOOSE_DEVICE_SECURE);
                return true;
            case R.id.menu_id_disconnect_current_device:
                return true;
        }
        return false;
    }

    private Handler mUIHandler = new Handler(Looper.myLooper()) {
        @SuppressLint("StringFormatInvalid")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mListViewAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mListViewAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    //byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = null;
                    readMessage = (String) msg.obj;
                    //try {
                        //readMessage = new String(readBuf, 0, msg.arg1, "UTF-8");
                        Log.d(TAG, "receiveBTMessage: " + readMessage);
                        mListViewAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    //} catch (UnsupportedEncodingException e) {
                       // e.printStackTrace();
                    //}
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                        Toast.makeText(MainActivity.this, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                        Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}