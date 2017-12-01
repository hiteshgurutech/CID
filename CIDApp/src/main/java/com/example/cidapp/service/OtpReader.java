package com.example.cidapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.cidapp.utils.Constants;

public class OtpReader extends BroadcastReceiver {

    /**
     * Constant TAG for logging key.
     */
    private static final String TAG = "OtpReader";

    /**
     * The bound OTP Listener that will be trigerred on receiving message.
     */
    private static OTPListener otpListener;

    /**
     * The Sender number string.
     */


    /**
     * Binds the sender string and listener for callback.
     *
     * @param listener
     * @param sender
     */

    public static final String OTP_NUMBER="17702828175";//"DM-088689";
    public static final String OTP_STRING="DM-088689";//"DM-088689";
    public static void bind(OTPListener listener) {
        otpListener = listener;

    }
    public interface OTPListener {

        public void otpReceived(String messageText);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
            if (bundle != null) {

                final Object[] pdusArr = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusArr.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusArr[i]);
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i(TAG, "senderNum: " + senderNum + " message: " + message);

                    if (senderNum.contains(OTP_NUMBER) || senderNum.contains(OTP_STRING))
                    { //If message received is from required number.
                        //If bound a listener interface, callback the overriden method.
                        if (otpListener != null) {
                            otpListener.otpReceived(getVerificationCode(message));
                        }
                    }
                }
            }
    }

    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(Constants.OTP_DELIMITER);

        if (index != -1) {
            int start = index + 1;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}