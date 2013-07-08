package com.datayumyum.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * User: son.c.to@gmail.com
 * Date: 7/2/13
 * Time: 6:13 PM
 */
public class ElaneScale {
    OutputStream outputStream;
    InputStream inputStream;

    InputThread inputThread;

    static final String TAG = "device.Scale";
    final BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private final BluetoothDevice device;

    public ElaneScale() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        device = findBluetoothDevice();
        tryToConnect();
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
        inputThread.deviceDataListeners.add(listener);
    }

    private void tryToConnect() {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        while (true) {
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                if (inputThread == null) {
                    inputThread = new InputThread();
                    inputThread.start();
                }
                break;
            } catch (IOException ex) {
                Log.i(TAG, "trying to reconnect " + device.getName());
                closeResources();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private void closeResources() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            Log.w(TAG, ex.getMessage());
        }
    }

    private BluetoothDevice findBluetoothDevice() {

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

    private class InputThread extends Thread {
        List<DeviceDataListener> deviceDataListeners;
        boolean stop;
        byte[] lastInput;

        public InputThread() {
            deviceDataListeners = new LinkedList<DeviceDataListener>();
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    byte[] buffer = new byte[8];
                    int bytesRead = inputStream.read(buffer);
                    if (!Arrays.equals(buffer, lastInput) && bytesRead > 0) {
                        lastInput = buffer;
                        notifyDeviceDataListeners(buffer);
                    }
                } catch (IOException e) {
                    Log.e(ElaneScale.TAG, e.getMessage());

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                    }

                    tryToConnect();
                }
            }
        }

        private void notifyDeviceDataListeners(byte[] data) {
            for (DeviceDataListener listener : deviceDataListeners) {
                listener.process(data);
            }
        }
    }

    public static class Command {
        public static final byte[] TARE = {0x07, 0x00, 0x72};
        public static final byte[] AUTO_OFF_TIMER = {0x07, 0x00, 0x7E, 0x00, 0x00, 0x00, 0x00, 127};
        public static final byte[] READ_CONTINUOUS = {0x07, 0x00, 0x01};
    }
}

