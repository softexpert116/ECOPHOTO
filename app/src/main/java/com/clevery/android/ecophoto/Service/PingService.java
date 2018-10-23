package com.clevery.android.ecophoto.Service;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.clevery.android.ecophoto.App;
import com.clevery.android.ecophoto.LoginActivity;
import com.clevery.android.ecophoto.MainActivity;
import com.clevery.android.ecophoto.httpModule.RequestBuilder;
import com.clevery.android.ecophoto.httpModule.ResponseElement;
import com.clevery.android.ecophoto.httpModule.RunanbleCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PingService extends Service {
    private static Timer timer = new Timer();
    private Context ctx;
    final Handler handler = new Handler();
    final int ping_time = 5000;

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
    }

    private void startService()
    {
        ping_request();
        timer = new Timer();
        timer.scheduleAtFixedRate(new mainTask(), 0, ping_time);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
        }
    }

    public void onDestroy()
    {
        timer.cancel();
        super.onDestroy();
    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            ping_request();
        }
    };
    private void ping_request() {
        RequestBuilder requestBuilder = new RequestBuilder(App.serverUrl);
        requestBuilder
                .addParam("type", "ping")
                .sendRequest(callback);
    }

    RunanbleCallback callback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            int code = element.getStatusCode();
            switch (code) {
                case 200:
//                    Toast.makeText(getApplicationContext(), "online", Toast.LENGTH_SHORT).show();
                    MainActivity.setNetStatus(true);
                    break;
                case 400:
//                    break;
                case 500:
                    MainActivity.setNetStatus(false);
                    break;
                default:
                    break;
            }
        }

    };
}
