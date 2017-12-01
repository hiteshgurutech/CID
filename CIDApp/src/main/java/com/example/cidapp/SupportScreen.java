package com.example.cidapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cidapp.model.PhoneModel;
import com.example.cidapp.utils.Constants;
import com.example.cidapp.utils.DisplayUtils;
import com.example.cidapp.utils.NetConst;
import com.example.cidapp.utils.PrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SupportScreen extends AppCompatActivity {

    private static final String TAG = SupportScreen.class.getSimpleName();
    private SupportScreen activity;
    @BindView(R.id.support_txtName)
    AppCompatTextView support_txtName;
    @BindView(R.id.support_txtEmail)
    AppCompatTextView support_txtEmail;
    @BindView(R.id.support_txtBirthdate)
    AppCompatTextView support_txtBirthdate;
    @BindView(R.id.support_txtAddress)
    AppCompatTextView support_txtAddress;
    @BindView(R.id.support_listDevices)
    ListView support_listDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_screen);
        ButterKnife.bind(this);
        activity = this;
        getSupportActionBar().setTitle("Support");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProfileApi();
    }

    private void getProfileApi() {
        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_PROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d(TAG, "Response:" + response);
                parseProfileResponse(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DisplayUtils.hideProgressDiaog();

                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(NetConst.KEY_USER_ID, PrefHelper.getUserId(activity));
                params.put(NetConst.KEY_IMEI_NUMBER, Constants.getDeviceID(activity));

                return params;
            }
        };
        CIDApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private void parseProfileResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            if (json.has("success")) {
                if (json.getInt("success") == 1) {
                    JSONObject data = json.getJSONObject("data");
                    if (data.has("first_name") && data.has("last_name"))
                        support_txtName.setText("Name:" + data.getString("first_name") + " " + data.getString("last_name"));

                    if (data.has("email"))
                        support_txtEmail.setText("Email:" + data.getString("email"));
                    String addr = "";
                    if (data.has("street1"))
                        addr += data.getString("street1") + ",";
                    if (data.has("street2"))
                        addr += data.getString("street2") + ",";
                    if (data.has("city"))
                        addr += data.getString("city") + ",";
                    if (data.has("postal_code"))
                        addr += data.getString("postal_code");
                    support_txtAddress.setText("Address:" + addr);
                    if (data.has("birthday") && !data.isNull("birthday")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date d1 = sdf.parse(data.getString("birthday"));

                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                        support_txtBirthdate.setText("Birthdate:" + sdf1.format(d1));
                    }
                    getDeviceList();
                } else {
                    Toast.makeText(activity, "Failed:", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void getDeviceList() {

        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, Constants.GET_DEVICE_LIST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                DisplayUtils.hideProgressDiaog();
                Log.d("tag", "Response:" + res);
                JSONObject response = null;
                try {
                    response = new JSONObject(res);

                    if (response.has("success")) {

                        if (response.getInt("success") == 1) {

                            if (response.has("devices") && !response.isNull("devices")) {
                                JSONArray objDevice = response.getJSONArray("devices");
                                List<PhoneModel> phoneList = new ArrayList<>();

                                if (objDevice.length() > 0) {
                                    for (int i = 0; i < objDevice.length(); i++) {
                                        PhoneModel phoneModel = new PhoneModel();
                                        JSONObject obj = objDevice.getJSONObject(i);
                                        phoneModel.setPhoneNumber(obj.getString(NetConst.KEY_PHONE_NUMBER));
                                        phoneModel.setPhoneId(obj.getString(NetConst.KEY_DEVICE_ID));
                                        phoneModel.setUserId(obj.getString(NetConst.KEY_USER_ID));
                                        phoneModel.setPhoneSMSToken(obj.getString(NetConst.KEY_SMS_CODE));
                                        phoneModel.setPhoneCountryCode(obj.getString(NetConst.KEY_COUNTRY_CODE));
                                        phoneModel.setPhoneStatus(obj.getString(NetConst.KEY_STATUS));
                                        phoneModel.setPhoneIMEI(obj.getString(NetConst.KEY_IMEI_NUMBER));
                                        phoneModel.setOs_api_level(obj.getString(NetConst.KEY_OS_API_LEVEL));
                                        phoneModel.setDevice(obj.getString(NetConst.KEY_DEVICE));
                                        phoneModel.setModel(obj.getString(NetConst.KEY_MODEL));
                                        phoneModel.setManufacturer(obj.getString(NetConst.KEY_MANUFACTURER));
                                        phoneModel.setBrand(obj.getString(NetConst.KEY_BRAND));
                                        phoneModel.setDisplay(obj.getString(NetConst.KEY_DISPLAY));
                                        phoneModel.setOs_version(obj.getString(NetConst.KEY_OS_VERSION));

                                        PrefHelper.saveString(activity, Constants.KEY_COUNTRY_CODE, obj.getString(NetConst.KEY_COUNTRY_CODE));
                                        PrefHelper.saveString(activity, Constants.KEY_SMS_TOKEN, obj.getString(NetConst.KEY_SMS_CODE));
                                        phoneList.add(phoneModel);
                                    }
                                    CIDApp.getInstance().setPhoneList(phoneList);
                                    support_listDevices.setAdapter(new PhoneListAdapter(activity));
                                }

                            }

                        } else {
                            Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
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
                Log.d("tag", "error:" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.d("tag", "=====> User id:" + PrefHelper.getUserId(activity));
                params.put(NetConst.KEY_USER_ID, PrefHelper.getUserId(activity));
                Log.d("tag", "params:" + params.toString());
                return params;
            }

        };
        CIDApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
    }

    class PhoneListAdapter extends BaseAdapter {
        Context context;
        List<PhoneModel> phoneList;
        LayoutInflater mInflater;

        PhoneListAdapter(Context context) {

            this.context = context;
            this.phoneList = CIDApp.getInstance().getPhoneList();
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return this.phoneList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            PhoneHolder holder = null;
            if (view == null) {
                view = mInflater.inflate(R.layout.lay_device_row, null);
                holder = new PhoneHolder();
                holder.txtPhoneNumber = (TextView) view.findViewById(R.id.txtPhoneNumber);
                holder.txtPhoneStatus = (TextView) view.findViewById(R.id.txtPhoneStatus);
                holder.btnVerify = (Button) view.findViewById(R.id.btnPhoneVerify);
                holder.btnDeactivate = (Button) view.findViewById(R.id.btnPhoneDeactive);
                view.setTag(holder);
            } else {
                holder = (PhoneHolder) view.getTag();
            }
            final PhoneModel model = this.phoneList.get(i);
            holder.txtPhoneNumber.setText(model.getPhoneNumber());
            holder.txtPhoneStatus.setText("Status: " + model.getPhoneStatus());
            holder.btnVerify.setVisibility(View.GONE);
            holder.btnDeactivate.setVisibility(View.GONE);
            return view;
        }

        class PhoneHolder {
            TextView txtPhoneNumber, txtPhoneStatus;
            Button btnVerify, btnDeactivate;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
