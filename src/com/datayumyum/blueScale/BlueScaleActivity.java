package com.datayumyum.blueScale;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class BlueScaleActivity extends Activity {
    static final String TAG = "BlueScale";

    Scale scale;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "adapter null");
        } else {
            if (scale == null) {
                initScale(mBluetoothAdapter);
            }
            byte[] tare = {0x07, 0x00, 0x72};
            byte[] readContinuous = {0x07, 0x00, 0x01};
            byte[] autoOffTimer = {0x07, 0x00, 0x7E, 0x00, 0x00, 0x00, 0x00, 127};
            scale.sendCmd(readContinuous);
        }
    }

    private void initScale(BluetoothAdapter bluetoothAdapter) {
        Log.i(TAG, "adapter=" + bluetoothAdapter);
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            String name = device.getName();
            Log.i(TAG, "device=" + name);
            if (name.equals("Elane BT5")) {
                Log.i(TAG, "found scale " + name);
                scale = new Scale(device);
                scale.onDataAvailable(new DataListener() {
                    @Override
                    public void process(Object data) {
                        byte[] byteArray = (byte[]) data;
                        byte[] weight = new byte[4];
                        for (int i = 4, j = 0; i <= 7; i++) {
                            weight[j++] = byteArray[i];
                        }
                        Log.i(TAG, "weight " + weight[weight.length-1]);
                    }
                });

                break;
            }
        }
    }
}
