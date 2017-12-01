package com.example.cidapp.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nilesh on 1/12/16.
 */
public class Constants {
    //  public static final String BASE_URL ="https://calleridregistry.com/customer/api/" ; //this is web api base url
    public static final String BASE_URL = "http://198.199.90.178/api/";
    public static final String SETTINGS_URL = BASE_URL + "settings";
    public static final String COUNTRY_URL = BASE_URL + "countries";
    public static final String REGISTER_URL = BASE_URL + "register";
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String ADD_DEVICE_URL = BASE_URL + "add_device";
    public static final String VERIFY_SMS_URL = BASE_URL + "verify_sms";
    public static final String RESEND_SMS_URL = BASE_URL + "resend_token";
    public static final String FORGOT_PASSWORD_URL = BASE_URL + "forgot_password";
    public static final String RESEND_EMAIL_URL = BASE_URL + "resend_email";
    public static final String GET_PROFILE_URL = BASE_URL + "get_profile";
    public static final String UPDATE_PROFILE_URL = BASE_URL + "update_profile";
    public static final String UPDATE_EMAIL_URL = BASE_URL + "email_update";
    public static final String GET_DEVICE_LIST_URL = BASE_URL + "device_list";
    public static final String UPDATE_PHONE_STATUS_URL = BASE_URL + "update_phone_status";
    public static final String TRNASCATION_TYPE_URL = BASE_URL + "get_transaction_types";
    public static final String ADD_TRNASCATION_URL = BASE_URL + "transaction";
    public static final String TRNASCATION_LIST_URL = BASE_URL + "get_all_transactions";
    public static final String TRNASCATION_DETAIL_URL = BASE_URL + "get_transaction_details";


    public static final String KEY_ID = "_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_ISLOGIN = "is_login";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_IS_SMS_VERIFY = "is_sms_verify";
    public static final String KEY_IS_EMAIL_VERIFY = "is_email_verify";
    public static final String KEY_PHONE_ID = "phone_id";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_ISO = "iso";
    public static final String KEY_SMS_TOKEN = "sms_token";
    public static final String KEY_COUNTRY_CODE = "country_code";
    public static final String KEY_CURRENT_COUNTRY = "current_country";
    public static final String KEY_STATUS = "status";
    public static final String OTP_DELIMITER = ":";
    public static final String KEY_REGISTER_THROTTLE = "register_throttle_attempts";
    public static final String KEY_LOGIN_THROTTLE = "login_throttle_attempts";
    public static final String KEY_LASTUPDATE = "last_update";
    public static final String KEY_CURRENT_COUNTRY_NAME = "country_name";
    public static final String KEY_PHONE_STATUS = "phone_status";
    public static final String KEY_TRNASCATION_TYPE_URL = "get_transaction_types";

    public static enum STATUS {

        NONE("NONE"),
        PHONE("PHONE"),
        SMS("SMS"),
        ACTIVE("ACTIVE"),
        UNCONFIRMED("Unconfirmed"),
        BANNED("Banned"),
        FAILED("FAILED");

        private final String name;

        private STATUS(String s) {
            name = s;
        }

        public boolean equals(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return name;
        }
    }


    public static String getFormatedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(new Date());
    }

    public static String getDeviceID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

}
