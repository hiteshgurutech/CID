package com.example.cidapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

public class Home extends AppCompatActivity {

    private static final String TAG = Home.class.getSimpleName();
    @BindView(R.id.home_tvDeviceModel)
    TextView home_tvDeviceModel;
    @BindView(R.id.home_tvDeviceMef)
    TextView home_tvDeviceMef;
    @BindView(R.id.home_tvDeviceOS)
    TextView home_tvDeviceOS;
    @BindView(R.id.home_tvDeviceIMEI)
    TextView home_tvDeviceIMEI;
    @BindView(R.id.home_tvPhoneNumber)
    TextView home_tvPhoneNumber;

    @BindView(R.id.txtPhoneNumber)
    TextView txtPhoneNumber;
    @BindView(R.id.txtPhoneStatus)
    TextView txtPhoneStatus;
    @BindView(R.id.btnPhoneVerify)
    Button btnPhoneVerify;
    @BindView(R.id.btnPhoneDeactive)
    Button btnPhoneDeactive;


    Home activity;
    private String phone_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        activity = this;
        initView();
    }

    private void initView() {
        home_tvDeviceModel.setText(String.format(getString(R.string.label_device_model), Build.MODEL) + "");
        home_tvDeviceMef.setText(String.format(getString(R.string.label_device_menufecture), Build.MANUFACTURER) + "");
        home_tvDeviceOS.setText(String.format(getString(R.string.label_device_os), Build.VERSION.RELEASE) + "");
        home_tvDeviceIMEI.setText(String.format(getString(R.string.label_device_imei), Constants.getDeviceID(activity)) + "");
        home_tvPhoneNumber.setText(String.format(getString(R.string.label_phone_number), PrefHelper.getPhoneNumber(activity)) + "");
        txtPhoneNumber.setText(PrefHelper.getPhoneNumber(activity));
        phone_status = PrefHelper.getString(activity, Constants.KEY_PHONE_STATUS).toUpperCase();
        txtPhoneStatus.setText(PrefHelper.getString(activity, Constants.KEY_PHONE_STATUS).toUpperCase());
        if (phone_status.equalsIgnoreCase("active")) {
            btnPhoneDeactive.setText("Deactivate");
            btnPhoneDeactive.setBackgroundColor(Color.BLACK);
        } else {
            btnPhoneDeactive.setText("Activate");
            btnPhoneDeactive.setBackgroundColor(Color.RED);
        }
        if (phone_status.equals(Constants.STATUS.UNCONFIRMED.toString().toUpperCase())) {
            btnPhoneVerify.setText("Verify");
            btnPhoneVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, VerifySMS.class);
                    startActivity(intent);
                }
            });
        } else if (phone_status.equals(Constants.STATUS.ACTIVE.toString().toUpperCase())) {
            btnPhoneVerify.setText("Verified");

        } else {
            btnPhoneVerify.setText("Verified");
        }

        btnPhoneDeactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone_status.equalsIgnoreCase("active")) {
                    updatePhoneStatus(0);
                } else {
                    //finalHolder.btnDeactivate.setText("Deactivate");
                    //finalHolder.btnDeactivate.setBackgroundColor(Color.RED);
                    updatePhoneStatus(1);
                }
            }
        });
    }

    private void updatePhoneStatus(final int status) {

        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_PHONE_STATUS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d("tag", "response:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {
                        if (jsonObject.getInt("success") == 1) {
                            phone_status = jsonObject.getJSONObject("data").getString(NetConst.KEY_STATUS);
                            PrefHelper.saveString(activity, Constants.KEY_PHONE_STATUS, phone_status);
                            txtPhoneStatus.setText(PrefHelper.getString(activity, Constants.KEY_PHONE_STATUS).toUpperCase());
                            if (phone_status.equalsIgnoreCase("active")) {
                                btnPhoneDeactive.setText("Deactivate");
                                btnPhoneDeactive.setBackgroundColor(Color.BLACK);
                            } else {
                                btnPhoneDeactive.setText("Activate");
                                btnPhoneDeactive.setBackgroundColor(Color.RED);
                            }
                        } else {
                            DisplayUtils.showCustomToast(activity, "Update failed.");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DisplayUtils.hideProgressDiaog();
                Log.d("tag", "Error:" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(NetConst.KEY_DEVICE_ID, PrefHelper.getPhoneId(activity));
                params.put(NetConst.KEY_STATUS, "" + status);
                return params;
            }
        };
        /////
        CIDApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            DisplayUtils.showCustomToast(activity, "Logout");
            //PrefHelper.clearAll(getApplicationContext());
            PrefHelper.saveStatus(activity, Constants.STATUS.ACTIVE);
            PrefHelper.saveBoolean(activity, Constants.KEY_ISLOGIN, false);
            startActivity(new Intent(Home.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(activity, Profile.class));
        }
//        else if (item.getItemId() == R.id.action_support) {
//            startActivity(new Intent(activity, SupportScreen.class));
//        }
//        else if (item.getItemId() == R.id.action_addDevice) {
//            startActivity(new Intent(activity, AddPhone.class));
//        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.footer_tvAboutUs)
    void onClickAboutUs(View view) {
        DisplayUtils.displayCMSDialog(activity, "About Us", CIDApp.getInstance().getDatabase().getSettings().getAbout_us());
    }

    @OnClick(R.id.footer_tvAboutApp)
    void onClickAboutApp(View view) {
        DisplayUtils.displayCMSDialog(activity, "About App", CIDApp.getInstance().getDatabase().getSettings().getAbout_app());
    }

    @OnClick(R.id.footer_tvDetail)
    void onClickDetail(View view) {
        DisplayUtils.displayCMSDialog(activity, "Details", CIDApp.getInstance().getDatabase().getSettings().getAbout_details());
    }
}
