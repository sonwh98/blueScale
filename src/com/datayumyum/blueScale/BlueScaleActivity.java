package com.datayumyum.blueScale;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.datayumyum.device.DeviceDataListener;
import com.datayumyum.device.ElaneScale;

public class BlueScaleActivity extends Activity {
    static final String TAG = "BlueScaleActivity";

    ElaneScale scale;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (scale == null) {
            scale = new ElaneScale();
            scale.onDataAvailable(new DeviceDataListener() {
                @Override
                public void process(Object data) {
                    byte[] byteArray = (byte[]) data;
                    Log.i(TAG, "weight " + byteArray[byteArray.length - 1]);
                }
            });
        }

        byte[] tare = {0x07, 0x00, 0x72};
        byte[] readContinuous = {0x07, 0x00, 0x01};
        byte[] autoOffTimer = {0x07, 0x00, 0x7E, 0x00, 0x00, 0x00, 0x00, 127};
        scale.sendCmd(readContinuous);
    }
}
