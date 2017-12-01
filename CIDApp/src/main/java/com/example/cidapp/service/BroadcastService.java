package com.example.cidapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.example.cidapp.utils.PrefHelper;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "com.example.cidapp.countdown_br";
    public static final String COUNTDOWN_BR_LOGIN = "com.example.cidapp.countdown_br_login";
    CountDownTimer cdt = null;
    @Override
        public void onCreate() {       
            super.onCreate();
        final Intent bi = new Intent();

            if(PrefHelper.getBoolean(getApplicationContext(),"isLoginThrottle"))
                bi.setAction(COUNTDOWN_BR_LOGIN);
            if(PrefHelper.getBoolean(getApplicationContext(),"isRegisterThrottle"))
                bi.setAction(COUNTDOWN_BR);
            Log.i(TAG, "Starting timer...");

            cdt = new CountDownTimer(30000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                    bi.putExtra("countdown", millisUntilFinished);
                    bi.putExtra("isFinish",false);
                    sendBroadcast(bi);
                }

                @Override
                public void onFinish() {
                    Log.i(TAG, "Timer finished");
                    bi.putExtra("isFinish",true);
                    sendBroadcast(bi);
                }
            };

            cdt.start();
        }

        @Override
        public void onDestroy() {

           // cdt.cancel();
            Log.i(TAG, "Timer cancelled");
            super.onDestroy();
        }


        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {


            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }
}