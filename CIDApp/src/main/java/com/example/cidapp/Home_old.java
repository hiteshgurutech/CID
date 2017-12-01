package com.example.cidapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.cidapp.utils.NetConst.KEY_DEVICE_ID;

public class Home_old extends AppCompatActivity {

    private static final String TAG = Home_old.class.getSimpleName();
    @BindView(R.id.listDevices)
    ListView listDevices;

    Home_old activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_old);
        ButterKnife.bind(this);
        activity = this;
        initView();
    }

    private void initView() {
//        if (CIDApp.getInstance().getPhoneList() != null && CIDApp.getInstance().getPhoneList().size() > 0) {
//            listDevices.setAdapter(new PhoneListAdapter(activity));
//        } else {
//
//        }

        getDeviceList();
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
                                        phoneModel.setPhoneId(obj.getString(NetConst.KEY_IMEI_NUMBER));
                                        phoneModel.setUserId(obj.getString(NetConst.KEY_USER_ID));
                                        phoneModel.setPhoneSMSToken(obj.getString(NetConst.KEY_SMS_CODE));
                                        phoneModel.setPhoneCountryCode(obj.getString(NetConst.KEY_COUNTRY_CODE));
                                        phoneModel.setPhoneStatus(obj.getString(NetConst.KEY_STATUS));
                                        phoneModel.setPhoneIMEI(obj.getString(NetConst.KEY_DEVICE_ID));
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
                                    listDevices.setAdapter(new PhoneListAdapter(activity));
                                }

                            }

                        } else {
                            Toast.makeText(Home_old.this, "Error", Toast.LENGTH_SHORT).show();
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
                params.put(KEY_DEVICE_ID, PrefHelper.getPhoneId(activity));
                params.put(NetConst.KEY_USER_ID, PrefHelper.getUserId(activity));
                Log.d("tag", "params:" + params.toString());
                return params;
            }

        };
        CIDApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
    }


    @OnClick(R.id.btnAddDevice)
    void OnAddDevice(View view) {
        Intent intent = new Intent(activity, AddPhone.class);
        intent.putExtra("new", true);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Toast.makeText(Home_old.this, "Logout", Toast.LENGTH_SHORT).show();
            //PrefHelper.clearAll(getApplicationContext());
            PrefHelper.saveStatus(activity, Constants.STATUS.ACTIVE);
            PrefHelper.saveBoolean(activity, Constants.KEY_ISLOGIN, false);
            startActivity(new Intent(Home_old.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(activity, Profile.class));
        } else if (item.getItemId() == R.id.action_transction) {
            startActivity(new Intent(activity, TranscationForm.class));
        } else if (item.getItemId() == R.id.action_transction_list) {
            startActivity(new Intent(activity, TranscationList.class));
        }

//         else if (item.getItemId() == R.id.action_support) {
//            startActivity(new Intent(activity, SupportScreen.class));
//        }
        return super.onOptionsItemSelected(item);
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
            if (model.getPhoneStatus().equalsIgnoreCase(Constants.STATUS.UNCONFIRMED.toString())) {
                holder.btnVerify.setEnabled(true);
                holder.btnVerify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, VerifySMS.class);
                        intent.putExtra("phoneModel", phoneList.get(i));
                        startActivity(intent);
                    }
                });
            } else if (model.getPhoneStatus().equalsIgnoreCase(Constants.STATUS.ACTIVE.toString())) {
                holder.btnVerify.setEnabled(false);
                holder.btnVerify.setText("Verified");
            }

            if (model.getPhoneStatus().equalsIgnoreCase("active")) {
                holder.btnDeactivate.setText("Deactivate");
                holder.btnDeactivate.setBackgroundColor(Color.RED);
            } else {
                holder.btnDeactivate.setText("Activate");
                holder.btnDeactivate.setBackgroundColor(Color.BLACK);
            }
            if (model.getPhoneStatus().equalsIgnoreCase(Constants.STATUS.UNCONFIRMED.toString())) {
                holder.btnDeactivate.setEnabled(false);

                holder.btnDeactivate.setBackgroundColor(Color.BLACK);
            } else {
                holder.btnDeactivate.setEnabled(true);
                final PhoneHolder finalHolder = holder;
                holder.btnDeactivate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getPhoneStatus().equalsIgnoreCase("active")) {

                            updatePhoneStatus(i, 0, finalHolder.btnDeactivate);
                        } else {
                            //finalHolder.btnDeactivate.setText("Deactivate");
                            //finalHolder.btnDeactivate.setBackgroundColor(Color.RED);
                            updatePhoneStatus(i, 1, finalHolder.btnDeactivate);
                        }
                    }
                });
            }
            return view;
        }

        class PhoneHolder {
            TextView txtPhoneNumber, txtPhoneStatus;
            Button btnVerify, btnDeactivate;
        }

        private void updatePhoneStatus(final int pos, final int status, final Button btn) {
            final PhoneModel model = this.phoneList.get(pos);
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
                                model.setPhoneStatus(jsonObject.getJSONObject("data").getString(NetConst.KEY_STATUS));
                                if (model.getPhoneStatus().equalsIgnoreCase("active")) {
                                    btn.setText("Deactivate");
                                    btn.setBackgroundColor(Color.BLACK);
                                } else {
                                    btn.setText("Activate");
                                    btn.setBackgroundColor(Color.RED);
                                }
                                phoneList.set(pos, model);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(activity, "Update failed.", Toast.LENGTH_SHORT).show();
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
                    params.put(NetConst.KEY_IMEI_NUMBER, model.getPhoneId());
                    params.put(NetConst.KEY_STATUS, "" + status);
                    params.put(NetConst.KEY_DEVICE_ID, model.getPhoneIMEI());

                    Log.d(TAG, "Params:" + params.toString());

                    return params;
                }
            };
            /////
            CIDApp.getInstance().addToRequestQueue(stringRequest, TAG);
        }
    }


}
