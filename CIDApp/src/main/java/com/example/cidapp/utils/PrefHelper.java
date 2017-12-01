package com.example.cidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cidapp.model.User;

import java.util.Date;

/**
 * Created by nilesh on 8/12/16.
 */
public class PrefHelper {
    private static String PREF_USER = "user";

    public static void saveUserInfo(Context mContext, User user) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.KEY_USER_ID, user.getUser_id());
        editor.putString(Constants.KEY_EMAIL, user.getEmail());
        editor.putString(Constants.KEY_FIRSTNAME, user.getFirstname());
        editor.putString(Constants.KEY_LASTNAME, user.getLastname());
        editor.putString(Constants.KEY_PASSWORD, user.getPassword());
        editor.putBoolean(Constants.KEY_ISLOGIN, user.isLogin());
        editor.putBoolean(Constants.KEY_IS_SMS_VERIFY, user.isSMSVerify());
        editor.putBoolean(Constants.KEY_IS_EMAIL_VERIFY, user.isEmailVerify());
        editor.putString(Constants.KEY_PHONE_ID, user.getPhoneID());
        editor.putString(Constants.KEY_PHONE_NUMBER, user.getPhoneNumber());
        editor.putString(Constants.KEY_CREATED_AT, user.getCreatedAt());
        editor.commit();
    }

    public static void saveBoolean(Context mContext, String key, boolean value) {
        mContext.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).edit()
                .putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context mContext, String key) {
        return mContext.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .getBoolean(key, false);
    }

    public static String getPhoneId(Context mContext) {
        return mContext.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .getString(Constants.KEY_PHONE_ID, null);
    }

    public static String getPhoneNumber(Context mContext) {
        return mContext.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .getString(Constants.KEY_PHONE_NUMBER, null);
    }

    public static String getUserId(Context mContext) {
        return mContext.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .getString(Constants.KEY_USER_ID, null);
    }

    public static String getEmail(Context mContext) {
        return mContext.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .getString(Constants.KEY_EMAIL, null);
    }

    public static void savePhoneInfo(Context mContext, String phone, String phoneId) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.KEY_PHONE_ID, phoneId);
        editor.putString(Constants.KEY_PHONE_NUMBER, phone);

        editor.commit();
    }

    public static void saveString(Context context, String key, String value) {
        context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key) {
        return context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .getString(key, null);
    }

    public static void clearAll(Context context) {
        context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).edit().clear().commit();
    }


    public static String getStatus(Context context) {
        return context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .getString(Constants.KEY_STATUS, Constants.STATUS.NONE.toString());
    }

    public static void saveStatus(Context context, Constants.STATUS status) {
        context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .edit().putString(Constants.KEY_STATUS, status.toString()).commit();
    }

    public static void saveThrottle(Context context, String key, int value) {
        context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).edit()
                .putInt(key, value).commit();
    }

    public static int getThrottle(Context context, String key) {
        return context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .getInt(key, 0);
    }

    public static String getLastUpdateDate(Context context) {
        return context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .getString(Constants.KEY_LASTUPDATE, new Date().toString());
    }

    public static void saveLastUpdateDate(Context context, String value) {
        context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .edit().putString(Constants.KEY_LASTUPDATE, value).commit();
    }
}
