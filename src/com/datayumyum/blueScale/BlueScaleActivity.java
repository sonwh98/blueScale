package com.datayumyum.blueScale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import com.datayumyum.device.DeviceDataListener;
import com.datayumyum.device.ElaneScale;

public class BlueScaleActivity extends Activity {
    static final String TAG = "BlueScaleActivity";

    ElaneScale scale;
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.weight);

        if (scale == null) {
            scale = new ElaneScale();
            scale.onDataAvailable(new DeviceDataListener() {
                @Override
                public void process(final Object data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] byteArray = (byte[]) data;
                            byte weight = byteArray[byteArray.length - 1];
                            textView.setText("weight=" + weight);
                            Log.i(TAG, "weight " + weight);
                            textView.setText("weight=" + weight);
                        }
                    });

                }
            });
        }

        byte[] tare = {0x07, 0x00, 0x72};
        byte[] readContinuous = {0x07, 0x00, 0x01};
        byte[] autoOffTimer = {0x07, 0x00, 0x7E, 0x00, 0x00, 0x00, 0x00, 127};
        scale.sendCmd(readContinuous);
    }
}
