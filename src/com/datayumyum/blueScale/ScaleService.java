package com.datayumyum.blueScale;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.datayumyum.device.ElaneScale;

/**
 * User: Sonny To sonny.to@conmio.com/son.c.to@gmail.com
 * Date: 7/6/13
 * Time: 2:45 AM
 */
public class ScaleService extends Service {
    ElaneScale scale;
    static final String TAG = "ScaleService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCreate() {
        if (scale == null) {
            scale = new ElaneScale();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
