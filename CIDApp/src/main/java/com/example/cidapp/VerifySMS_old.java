package com.example.cidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cidapp.model.PhoneModel;
import com.example.cidapp.service.OtpReader;
import com.example.cidapp.utils.Constants;
import com.example.cidapp.utils.DisplayUtils;
import com.example.cidapp.utils.NetConst;
import com.example.cidapp.utils.PrefHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VerifySMS_old extends AppCompatActivity implements OtpReader.OTPListener {

    private static final String TAG = VerifySMS.class.getSimpleName();

    @BindView(R.id.sms_imgFlag)
    ImageView sms_imgFlag;
    @BindView(R.id.sms_tvMobilenNumber)
    AppCompatTextView sms_tvMobilenNumber;
    @BindView(R.id.sms_etVerifyCode)
    AppCompatEditText sms_etVerifyCode;

    String number;
    String country_iso;
    VerifySMS_old activity;
    private PhoneModel phoneModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_sms);
        activity = this;
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Verify SMS");

        sms_tvMobilenNumber.setText(PrefHelper.getPhoneNumber(activity));
        sms_imgFlag.setVisibility(View.GONE);
        OtpReader.bind(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private int getFlagResource() {
        return getResources().getIdentifier("country_" + country_iso.toLowerCase(), "drawable", getPackageName());
    }

    @OnClick(R.id.sms_btnVerification)
    void OnVerifyClick(View view) {
        if (sms_etVerifyCode.length() == 0) {
            //    Toast.makeText(VerifySMS.this, "Please enter code", Toast.LENGTH_SHORT).show();
            DisplayUtils.showCustomToast(activity, "Please enter code");
        } else if (sms_etVerifyCode.length() != 6) {
            //Toast.makeText(VerifySMS.this, "Invalid code", Toast.LENGTH_SHORT).show();
            DisplayUtils.showCustomToast(activity, "Invalid code");
        } else if (sms_etVerifyCode.length() == 6) {
            // Toast.makeText(VerifySMS.this, "Valid code", Toast.LENGTH_SHORT).show();
            DisplayUtils.hideKeyboard(activity);
            callVerifyApi();
        }
    }

    @OnClick(R.id.sms_btnResend)
    void OnResendClick(View view) {
        resendSMSAPI();

    }

    private void callVerifyApi() {

        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.VERIFY_SMS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d(TAG, "Response:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {
                        DisplayUtils.showCustomToast(activity, jsonObject.getString("message"));
                        if (jsonObject.getInt("success") == 1) {
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            PrefHelper.saveString(activity, Constants.KEY_PHONE_STATUS, (dataObj.getString("status")));

                            PrefHelper.saveStatus(activity, Constants.STATUS.ACTIVE);
                            PrefHelper.saveBoolean(activity, Constants.KEY_ISLOGIN, true);

                            startActivity(new Intent(activity, Home_old.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            activity.finish();
                        } else {
                            PrefHelper.saveStatus(activity, Constants.STATUS.SMS);
                            //Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            //startActivity(new Intent(activity,VerifySMS.class));
                        }
                        // startActivity(new Intent(activity,VerifySMS.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DisplayUtils.hideProgressDiaog();

                //Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                DisplayUtils.showCustomToast(activity, error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(NetConst.KEY_USER_ID, PrefHelper.getUserId(activity));
                params.put(NetConst.KEY_PHONE_NUMBER, PrefHelper.getPhoneNumber(activity));
                params.put(NetConst.KEY_IMEI_NUMBER, Constants.getDeviceID(activity));
                params.put(NetConst.KEY_SMS_CODE, sms_etVerifyCode.getText().toString());
                Log.d(TAG, "Params:" + params.toString());
                return params;
            }
        };
        CIDApp.getInstance().addToRequestQueue(stringRequest, TAG);

    }

    private void resendSMSAPI() {

        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RESEND_SMS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d(TAG, "Response:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {
                        DisplayUtils.showCustomToast(activity, jsonObject.getString("message"));
                        if (jsonObject.getInt("success") == 1) {
                            JSONObject dataObj = jsonObject.getJSONObject("data");

                            PrefHelper.saveString(activity, Constants.KEY_SMS_TOKEN, dataObj.getString("sms_token"));

                            //PrefHelper.saveStatus(activity, Constants.STATUS.ACTIVE);
                            //startActivity(new Intent(activity,Home.class));
                        } else {

                            //startActivity(new Intent(activity,VerifySMS.class));
                        }
                        // startActivity(new Intent(activity,VerifySMS.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DisplayUtils.hideProgressDiaog();

                //Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                DisplayUtils.showCustomToast(activity, error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(NetConst.KEY_USER_ID, PrefHelper.getUserId(activity));
                params.put(NetConst.KEY_PHONE_NUMBER, PrefHelper.getPhoneNumber(activity));
                params.put(NetConst.KEY_IMEI_NUMBER, Constants.getDeviceID(activity));
                params.put(NetConst.KEY_COUNTRY_CODE, PrefHelper.getString(activity, Constants.KEY_COUNTRY_CODE));
                Log.d(TAG, "Params:" + params.toString());
                return params;
            }
        };
        CIDApp.getInstance().addToRequestQueue(stringRequest, TAG);

    }

    @Override
    public void otpReceived(String messageText) {
        //Toast.makeText(VerifySMS.this, "Otp received"+messageText, Toast.LENGTH_SHORT).show();
        sms_etVerifyCode.setText(messageText);
    }


}
