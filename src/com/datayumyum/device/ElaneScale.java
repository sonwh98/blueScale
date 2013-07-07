package com.datayumyum.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * User: son.c.to@gmail.com
 * Date: 7/2/13
 * Time: 6:13 PM
 */
public class ElaneScale {
    OutputStream outputStream;
    private InputStream inputStream;

    InputReader inputReader;

    static final String TAG = "device.Scale";

    public ElaneScale() {
        initialize();
    }

    public void sendCmd(byte[] cmdBuffer) {
        try {
            outputStream.write(cmdBuffer);
            outputStream.flush();
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    public void onDataAvailable(DeviceDataListener listener) {
        inputReader.deviceDataListeners.add(listener);
    }

    private void initialize() {
        try {
            BluetoothDevice device = findBluetoothDevice();
            BluetoothSocket socket = tryToConnect(device);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            inputReader = new InputReader();
            inputReader.start();
        } catch (IOException ex2) {
            Log.e(TAG, ex2.getMessage());
        }
    }

    private BluetoothSocket tryToConnect(BluetoothDevice device) {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        while (true) {
            try {
                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
                return socket;
            } catch (IOException ex) {
                Log.i(TAG, "trying to reconnect " + device.getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private BluetoothDevice findBluetoothDevice() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            String name = device.getName();
            Log.i(TAG, "device=" + name);
            if (name.equals("Elane BT5")) {
                Log.i(TAG, "found scale " + name);
                return device;
            }
        }
        return null;
    }

    private class InputReader extends Thread {
        List<DeviceDataListener> deviceDataListeners;

        public InputReader() {
            deviceDataListeners = new LinkedList<DeviceDataListener>();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    int available = inputStream.available();
                    if (available > 0) {
                        byte[] buffer = new byte[available];
                        inputStream.read(buffer);
                        for (DeviceDataListener listener : deviceDataListeners) {
                            listener.process(buffer);
                        }
                    }
                } catch (IOException e) {
                    Log.e(ElaneScale.TAG, e.getMessage());
                }
            }
        }
    }
}

