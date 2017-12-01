package com.example.cidapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.cidapp.model.Setting;
import com.example.cidapp.model.User;
import com.example.cidapp.utils.Constants;

/**
 * Created by nilesh on 6/12/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE = "CIDApp.sqlite";
    private String TABLE_USER = "user";
    private String TABLE_SETTINGS = "settings";
    private static String reg_enabled = "reg_enabled";
    private static String forgot_password = "forgot_password";
    private static String two_factor_enabled = "two_factor_enabled";
    private static String about_app = "about_app";
    private static String about_us = "about_us";
    private static String about_details = "about_details";
    private static String throttle_enabled = "throttle_enabled";
    private static String throttle_attempts = "throttle_attempts";
    private static String update_date = "update_date";

    // trnasction tabel varible here
    private String TABLE_TRANSCTION_TYPE = "transctiontype";
    private static String tran_type = "tran_type";
    private static String mobile_number = "mobile";
    private String CREATE_SETTING = "CREATE TABLE " + TABLE_SETTINGS +
            " (" + reg_enabled + " TEXT,"
            + forgot_password + " TEXT,"
            + two_factor_enabled + " TEXT,"
            + about_app + " TEXT,"
            + about_us + " TEXT,"
            + about_details + " TEXT,"
            + throttle_enabled + " TEXT,"
            + throttle_attempts + " TEXT,"
            + update_date + " TEXT);";

    private String CREATE_TABLE = "CREATE TABLE " + TABLE_USER +
            " (" + Constants.KEY_ID + " INTEGER PRIMARY KEY,"
            + Constants.KEY_USER_ID + " TEXT," +
            Constants.KEY_FIRSTNAME + " TEXT," +
            Constants.KEY_LASTNAME + " TEXT," +
            Constants.KEY_EMAIL + " TEXT," +
            Constants.KEY_PASSWORD + " TEXT," +
            Constants.KEY_ISLOGIN + " INT(1)," +
            Constants.KEY_PHONE_NUMBER + " TEXT," +
            Constants.KEY_IS_SMS_VERIFY + " INT(1)," +
            Constants.KEY_IS_EMAIL_VERIFY + " INT(1)," +
            Constants.KEY_PHONE_ID + " TEXT," +
            Constants.KEY_STATUS + " TEXT," +
            Constants.KEY_CREATED_AT + " DATETIME" + ");";

    private String CREATE_TABLE_TRANSCTION_TYPE = "CREATE TABLE " + TABLE_TRANSCTION_TYPE +
            " (" + Constants.KEY_ID + " INTEGER PRIMARY KEY," +
            mobile_number + " TEXT," +
            tran_type + " TEXT" + ");";


    public DatabaseHandler(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_SETTING);
        db.execSQL(CREATE_TABLE_TRANSCTION_TYPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXIST " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXIST " + TABLE_TRANSCTION_TYPE);
        onCreate(db);
    }

    public long addNewUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.KEY_USER_ID, user.getUser_id());
        cv.put(Constants.KEY_FIRSTNAME, user.getFirstname());
        cv.put(Constants.KEY_LASTNAME, user.getLastname());
        cv.put(Constants.KEY_EMAIL, user.getEmail());
        cv.put(Constants.KEY_PASSWORD, user.getPassword());
        cv.put(Constants.KEY_ISLOGIN, user.isLogin());
        cv.put(Constants.KEY_IS_SMS_VERIFY, user.isSMSVerify());
        cv.put(Constants.KEY_IS_EMAIL_VERIFY, user.isEmailVerify());
        cv.put(Constants.KEY_PHONE_NUMBER, user.getPhoneNumber());
        cv.put(Constants.KEY_PHONE_ID, user.getPhoneID());
        cv.put(Constants.KEY_CREATED_AT, user.getCreatedAt());
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_USER, null, cv);
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String qry = "SELECT * FROM " + TABLE_USER + " where " + Constants.KEY_EMAIL + "='" + email + "' and " + Constants.KEY_PASSWORD + "='" + password + "'";
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
                user.setUser_id(cursor.getString(cursor.getColumnIndex(Constants.KEY_USER_ID)));
                user.setFirstname(cursor.getString(cursor.getColumnIndex(Constants.KEY_FIRSTNAME)));
                user.setLastname(cursor.getString(cursor.getColumnIndex(Constants.KEY_LASTNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(Constants.KEY_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(Constants.KEY_PASSWORD)));
                user.setLogin((cursor.getInt(cursor.getColumnIndex(Constants.KEY_ISLOGIN)) == 1) ? true : false);
                user.setSMSVerify((cursor.getInt(cursor.getColumnIndex(Constants.KEY_IS_SMS_VERIFY)) == 1) ? true : false);
                user.setEmailVerify((cursor.getInt(cursor.getColumnIndex(Constants.KEY_IS_EMAIL_VERIFY)) == 1) ? true : false);
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Constants.KEY_PHONE_NUMBER)));
                user.setPhoneID(cursor.getString(cursor.getColumnIndex(Constants.KEY_PHONE_ID)));
                user.setCreatedAt(cursor.getString(cursor.getColumnIndex(Constants.KEY_CREATED_AT)));
                return user;
            } else
                return null;
        }
        return null;
    }

    public long updateSMSVerify(boolean verify, String email) {
        int status = (verify) ? 1 : 0;
        ContentValues cv = new ContentValues();
        cv.put(Constants.KEY_IS_SMS_VERIFY, status);
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_USER, cv, Constants.KEY_EMAIL + " = ? ", new String[]{email});
    }

    public long updateEmailVerify(boolean verify, String email) {
        int status = (verify) ? 1 : 0;
        ContentValues cv = new ContentValues();
        cv.put(Constants.KEY_IS_EMAIL_VERIFY, status);
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_USER, cv, Constants.KEY_EMAIL + " = ? ", new String[]{email});
    }

    public long updateLoginStatus(boolean login, String email) {
        int status = (login) ? 1 : 0;
        Log.d("TAG", "Status:" + status);
        ContentValues cv = new ContentValues();
        cv.put(Constants.KEY_ISLOGIN, status);
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_USER, cv, Constants.KEY_EMAIL + " = ? ", new String[]{email});
    }

    public void saveOrUpdateSettings(Setting setting) {
        ContentValues cv = new ContentValues();
        cv.put(reg_enabled, setting.getReg_enabled());
        cv.put(two_factor_enabled, setting.isTwo_factor_enabled() + "");
        cv.put(forgot_password, setting.getForgot_password());
        cv.put(about_app, setting.getAbout_app());
        cv.put(about_us, setting.getAbout_us());
        cv.put(about_details, setting.getAbout_details());
        cv.put(throttle_enabled, setting.getThrottle_enabled());
        cv.put(throttle_attempts, setting.getThrottle_attempts());
        cv.put(update_date, setting.getUpdateDate());
        SQLiteDatabase db = getWritableDatabase();
        if (isSettingEmpty()) {
            db.insert(TABLE_SETTINGS, null, cv);
        } else {
            db.execSQL("delete from " + TABLE_SETTINGS);
            db.insert(TABLE_SETTINGS, null, cv);
        }
    }

    public void saveTranscationList(String phonenumber, String type) {

        ContentValues values = new ContentValues();
        values.put(mobile_number, phonenumber);
        values.put(tran_type, type);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_TRANSCTION_TYPE, null, values);

    }


    public boolean isSettingEmpty() {
        String qry = "SELECT * FROM " + TABLE_SETTINGS;
        SQLiteDatabase db = getReadableDatabase();
        return (db.rawQuery(qry, null).getCount() > 0) ? false : true;
    }

    public Setting getSettings() {
        String qry = "SELECT * FROM " + TABLE_SETTINGS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                Setting setting = new Setting();
                setting.setReg_enabled(cursor.getString(cursor.getColumnIndex(reg_enabled)));
                setting.setForgot_password(cursor.getString(cursor.getColumnIndex(forgot_password)));
                setting.setTwo_factor_enabled(cursor.getString(cursor.getColumnIndex(two_factor_enabled)).equals("true") ? true : false);
                setting.setAbout_app(cursor.getString(cursor.getColumnIndex(about_app)));
                setting.setAbout_details(cursor.getString(cursor.getColumnIndex(about_details)));
                setting.setAbout_us(cursor.getString(cursor.getColumnIndex(about_us)));
                setting.setThrottle_attempts(cursor.getString(cursor.getColumnIndex(throttle_attempts)));
                setting.setThrottle_enabled(cursor.getString(cursor.getColumnIndex(throttle_enabled)));
                setting.setUpdateDate(cursor.getString(cursor.getColumnIndex(update_date)));
                return setting;
            }
        }
        return null;
    }

}
