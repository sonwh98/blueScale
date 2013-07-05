package com.datayumyum.blueScale;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * User: son.c.to@gmail.com/son.to@conmio.com
 * Date: 7/2/13
 * Time: 4:13 PM
 */
public class Scale {
    OutputStream outputStream;

    BluetoothDevice device;


    InputThread inputThread;

    static final String TAG = "com.datayumyum.blueScale.Scale";

    public Scale(BluetoothDevice device) {
        this.device = device;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);

            boolean connected = false;
            while (!connected) {
                try {
                    socket.connect();
                    connected = true;
                } catch (IOException ex) {
                    Log.i(TAG, "trying to reconnect " + device.getName());
                }
            }

            outputStream = socket.getOutputStream();
            inputThread = new InputThread(socket.getInputStream());
            new Thread(inputThread).start();
        } catch (IOException ex2) {

        }
    }

    public void sendCmd(byte[] cmdBuffer) {
        try {
            outputStream.write(cmdBuffer);
            outputStream.flush();
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    public void onDataAvailable(DataListener listener) {
        inputThread.dataListeners.add(listener);
    }
}

class InputThread implements Runnable {
    InputStream inputStream;
    List<DataListener> dataListeners;

    public InputThread(InputStream ins) {
        dataListeners = new LinkedList<DataListener>();
        inputStream = ins;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int available = inputStream.available();
                if (available > 0) {
                    byte[] buffer = new byte[available];
                    inputStream.read(buffer);
                    for (DataListener listener : dataListeners) {
                        listener.process(buffer);
                    }
                }
            } catch (IOException e) {
                Log.e(Scale.TAG, e.getMessage());
            }

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//            }
        }
    }
}