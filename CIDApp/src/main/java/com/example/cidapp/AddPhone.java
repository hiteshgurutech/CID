package com.example.cidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cidapp.utils.Constants;
import com.example.cidapp.utils.DisplayUtils;
import com.example.cidapp.utils.NetConst;
import com.example.cidapp.utils.PrefHelper;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPhone extends AppCompatActivity {


    private static final String TAG = AddPhone.class.getSimpleName();
    @BindView(R.id.phone_input_lay)
    IntlPhoneInput phone_input_lay;
    @BindView(R.id.phone_btnContinue)
    AppCompatButton phone_btnContinue;
    AddPhone activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone);
        ButterKnife.bind(this);
        activity = this;
        getSupportActionBar().setTitle("Add Mobile Number");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        phone_input_lay.setView(phone_btnContinue);

        if (PrefHelper.getString(activity, Constants.KEY_CURRENT_COUNTRY) != null) {
            Log.d("tag", "Current Country:" + PrefHelper.getString(activity, Constants.KEY_CURRENT_COUNTRY));
            phone_input_lay.setCUREENT_COUNTRY(PrefHelper.getString(activity, Constants.KEY_CURRENT_COUNTRY));
        }

        phone_btnContinue.setEnabled(false);
        phone_btnContinue.setBackgroundColor(Color.GRAY);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.phone_btnContinue)
    void OnContinueClick(View view) {

        if (phone_input_lay.isValid()) {

            DisplayUtils.hideKeyboard(activity);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                    .setMessage(getString(R.string.label_number_verification_info))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            PrefHelper.saveString(getApplicationContext(), Constants.KEY_PHONE_NUMBER, phone_input_lay.getNumber());
                            PrefHelper.saveString(getApplicationContext(), Constants.KEY_ISO, phone_input_lay.getSelectedCountry().getIso());
                            PrefHelper.saveString(activity, Constants.KEY_COUNTRY_CODE, phone_input_lay.getSelectedCountry().getDialCode() + "");
                            /*startActivity(new Intent(AddPhone.this, VerifySMS.class));*/
                            addDevice();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.create().show();

        } else {
            Toast.makeText(AddPhone.this, "Please check mobile number", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDevice() {
        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_DEVICE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d(TAG, "Response:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {
                        if (jsonObject.getInt("success") == 1) {
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            PrefHelper.savePhoneInfo(activity, dataObj.getString(NetConst.KEY_PHONE_NUMBER), dataObj.getString(NetConst.KEY_DEVICE_ID));
                            PrefHelper.saveString(activity, Constants.KEY_PHONE_STATUS, (dataObj.getString("status")));
                            PrefHelper.saveString(activity, Constants.KEY_COUNTRY_CODE, dataObj.getString("country_code"));
                            PrefHelper.saveString(activity, Constants.KEY_SMS_TOKEN, dataObj.getString("sms_token"));
                            PrefHelper.saveStatus(activity, Constants.STATUS.SMS);
                            startActivity(new Intent(activity, VerifySMS.class));
                        } else {
                            //startActivity(new Intent(activity,VerifySMS.class));
                        }
                        DisplayUtils.showCustomToast(AddPhone.this, jsonObject.getString("message"));
                        //startActivity(new Intent(activity,VerifySMS.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DisplayUtils.hideProgressDiaog();

                DisplayUtils.showCustomToast(activity, error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(NetConst.KEY_USER_ID, PrefHelper.getUserId(activity));
                params.put(NetConst.KEY_PHONE_NUMBER, phone_input_lay.getNumber());
                params.put(NetConst.KEY_DEVICE_ID, Constants.getDeviceID(activity));
                params.put(NetConst.KEY_COUNTRY_CODE, phone_input_lay.getSelectedCountry().getDialCode() + "");
                params.put(NetConst.KEY_OS_API_LEVEL, "" + Build.VERSION.SDK_INT);
                params.put(NetConst.KEY_DEVICE, Build.DEVICE);
                params.put(NetConst.KEY_MODEL, Build.MODEL);
                params.put(NetConst.KEY_MANUFACTURER, Build.MANUFACTURER);
                params.put(NetConst.KEY_BRAND, Build.BRAND);
                params.put(NetConst.KEY_DISPLAY, Build.DISPLAY);
                params.put(NetConst.KEY_OS_VERSION, Build.VERSION.RELEASE);
                Log.d("tag", "device info:" + params.toString());
                //    Log.d("tag", "mobile number :" + phone_input_lay.getNumber());

                String ContactNumber = phone_input_lay.getNumber();
                String newS = ContactNumber.replace("+" + phone_input_lay.getSelectedCountry().getDialCode(), "");
                Log.d(TAG, "Params:" + params.toString());
                Log.d("tag", "mobile number :" + newS);

                return params;
            }
        };
        CIDApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }


    private void getDeviceSuperInfo() {
        Log.i(TAG, "getDeviceSuperInfo");

        try {

            String s = "Debug-infos:";
            s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
            s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
            s += "\n Device: " + android.os.Build.DEVICE;
            s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";

            s += "\n RELEASE: " + android.os.Build.VERSION.RELEASE;
            s += "\n BRAND: " + android.os.Build.BRAND;
            s += "\n DISPLAY: " + android.os.Build.DISPLAY;
            s += "\n CPU_ABI: " + android.os.Build.CPU_ABI;
            s += "\n CPU_ABI2: " + android.os.Build.CPU_ABI2;
            s += "\n UNKNOWN: " + android.os.Build.UNKNOWN;
            s += "\n HARDWARE: " + android.os.Build.HARDWARE;
            s += "\n Build ID: " + android.os.Build.ID;
            s += "\n MANUFACTURER: " + android.os.Build.MANUFACTURER;
            s += "\n SERIAL: " + android.os.Build.SERIAL;
            s += "\n USER: " + android.os.Build.USER;
            s += "\n HOST: " + android.os.Build.HOST;

            StringBuilder sb = new StringBuilder();
            sb.append("os_api_level=" + Build.VERSION.SDK_INT);
            sb.append("device=" + Build.DEVICE);
            sb.append("model=" + Build.MODEL);
            sb.append("manufacturer=" + Build.MANUFACTURER);
            sb.append("brand=" + Build.BRAND);
            sb.append("display=" + Build.DISPLAY);
            Log.i(TAG + " | Device Info > ", s);

        } catch (Exception e) {
            Log.e(TAG, "Error getting Device INFO");
        }

    }//end getDeviceSuperInfo
}
