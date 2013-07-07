package com.datayumyum.blueScale;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.datayumyum.device.DeviceDataListener;
import com.datayumyum.device.ElaneScale;

import static com.datayumyum.device.ElaneScale.Command.*;

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
                        }
                    });

                }
            });
        }

        scale.sendCmd(READ_CONTINUOUS);
    }
}
