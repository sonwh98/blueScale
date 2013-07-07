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
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Bundle weightBundle = msg.getData();
                    textView.setText("weight=" + weightBundle.getByte("weight"));
                }
            };

            scale.onDataAvailable(new DeviceDataListener() {
                @Override
                public void process(Object data) {
                    byte[] byteArray = (byte[]) data;
                    byte weight = byteArray[byteArray.length - 1];
                    textView.setText("weight=" + weight);
                    Log.i(TAG, "weight " + weight);
                    Bundle weightBundle = new Bundle();
                    weightBundle.putByte("weight", weight);
                    Message weightMessage = Message.obtain();
                    weightMessage.setData(weightBundle);

                    handler.sendMessage(weightMessage);

                }
            });
        }

        byte[] tare = {0x07, 0x00, 0x72};
        byte[] readContinuous = {0x07, 0x00, 0x01};
        byte[] autoOffTimer = {0x07, 0x00, 0x7E, 0x00, 0x00, 0x00, 0x00, 127};
        scale.sendCmd(readContinuous);
    }
}
