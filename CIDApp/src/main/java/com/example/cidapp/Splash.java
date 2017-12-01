package com.example.cidapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.cidapp.database.DatabaseHandler;
import com.example.cidapp.model.Country;
import com.example.cidapp.model.Setting;
import com.example.cidapp.service.AppLocationService;
import com.example.cidapp.service.LocationAddress;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Splash extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 101;
    private static String TAG = Splash.class.getSimpleName();
    private static int SPLASH_TIME = 5000;
    @BindView(R.id.layProgress)
    LinearLayout layProgress;
    @BindView(R.id.spalsh_tvMessage)
    AppCompatTextView splash_tvMessage;
    @BindView(R.id.splash_tvStart)
    AppCompatTextView splash_tvStart;
    @BindView(R.id.txtChkWifi)
    AppCompatCheckedTextView txtChkWifi;
    @BindView(R.id.txtChkLocation)
    AppCompatCheckedTextView txtChkLocation;
    @BindView(R.id.txtChkUpdate)
    AppCompatCheckedTextView txtChkUpdate;
    @BindView(R.id.txtChkRegister)
    AppCompatCheckedTextView txtChkRegister;
    @BindView(R.id.layUpperLayer)
    FrameLayout layUpperLayer;
    private Splash activity;
    private AppLocationService locationService;
    private DatabaseHandler dbHelper;
    private Handler locationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    DisplayUtils.hideProgressDiaog();
                    Bundle bundle = msg.getData();
                    txtChkLocation.setChecked(true);
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(bundle.getDouble("lat"), bundle.getDouble("lng"), activity, new GeocoderHandler());
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private boolean isLocationAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        dbHelper = CIDApp.getInstance().getDatabase();
        activity = this;
        getSupportActionBar().hide();
        layProgress.setVisibility(View.GONE);
        splash_tvStart.setVisibility(View.INVISIBLE);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);



        /*AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                AdvertisingIdClient.Info idInfo = null;
                try {
                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());

                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String advertId = null;
                try{
                    advertId = idInfo.getId();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return advertId;
            }
            @Override
            protected void onPostExecute(String advertId) {
                Toast.makeText(getApplicationContext(), advertId, Toast.LENGTH_SHORT).show();
            }
        };
        task.execute();*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PrefHelper.getStatus(activity).equals(Constants.STATUS.ACTIVE.toString())
                && PrefHelper.getBoolean(activity, Constants.KEY_ISLOGIN)) {
            layUpperLayer.setVisibility(View.VISIBLE);

            getProfileApi();
           /* new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        startActivity(new Intent(Splash.this,Home.class));
                        Splash.this.finish();
                }
            },2000);*/
        } else {
            layUpperLayer.setVisibility(View.GONE);
            loadData();
        }

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

                //Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                DisplayUtils.showCustomToast(activity, error.getMessage());
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
                    if (data.has("status") && (!data.isNull("status"))) {
                        if (data.getString("status").equalsIgnoreCase("banned")) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                            alertDialog
                                    .setMessage("Your account is banned.Please contact admin to activate your account.")
                                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            activity.finish();
                                        }
                                    }).create().show();
                        } else {
                            startActivity(new Intent(Splash.this, Home_old.class));
                            Splash.this.finish();
                        }
                    }

                } else {
                    //Toast.makeText(Splash.this, "Failed:", Toast.LENGTH_SHORT).show();
                    DisplayUtils.showCustomToast(activity, "Failed:");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        if (!CIDApp.getInstance().isWIFIConnected()) {
            showWifiAlert();
            txtChkWifi.setChecked(false);
        } else {
            txtChkWifi.setChecked(true);
            txtChkLocation.setChecked(false);
            if (isLocationPermission()) {
                getLocationInfo();
            } else {
                requestLocationService();
            }

        }

    }

    private void showWifiAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(getString(R.string.wifi_error_message))
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (isLocationPermission()) {
                            getLocationInfo();
                        } else {
                            requestLocationService();
                        }

                    }
                }).create().show();
    }

    private void GetSettings() {
        // DisplayUtils.showProgressDialog(activity,"Loading settings...");
        boolean is24hourUpdate = true;
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        if (!dbHelper.isSettingEmpty()) {
            Setting setting = dbHelper.getSettings();

            Date currentDate = new Date();
            try {
                Date updateDate = simpleDateFormat.parse(setting.getUpdateDate());
                long diffMillis = currentDate.getTime() - updateDate.getTime();
                long hours = diffMillis / (60 * 60 * 1000);
                Log.d("tag", "Hours" + hours);
                if (hours >= 24) {
                    is24hourUpdate = true;
                } else {
                    is24hourUpdate = false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (is24hourUpdate) {
            layProgress.setVisibility(View.VISIBLE);
            splash_tvMessage.setText("Loading settings...");
            Log.d("tag", "Loading:" + Constants.SETTINGS_URL);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constants.SETTINGS_URL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    // DisplayUtils.hideProgressDiaog();
                    if (response.length() > 0) {
                        try {
                            JSONObject object = response.getJSONObject(0);
                            Setting setting = new Setting();
                            if (object.has("reg_enabled")) {
                                setting.setReg_enabled(object.getString("reg_enabled"));
                            }
                            if (object.has("forgot_password")) {
                                setting.setForgot_password(object.getString("forgot_password"));
                            }
                            if (object.has("2fa_enabled")) {
                                setting.setTwo_factor_enabled(object.getBoolean("2fa_enabled"));
                            }
                            if (object.has("about_app")) {
                                setting.setAbout_app(object.getString("about_app"));
                            }
                            if (object.has("about_us")) {
                                setting.setAbout_us(object.getString("about_us"));
                            }
                            if (object.has("about_details")) {
                                setting.setAbout_details(object.getString("about_details"));
                            }
                            if (object.has("throttle_enabled")) {
                                setting.setThrottle_enabled(object.getString("throttle_enabled"));
                            }
                            if (object.has("throttle_attempts")) {
                                setting.setThrottle_attempts(object.getString("throttle_attempts"));
                            }
                            setting.setUpdateDate(simpleDateFormat.format(new Date()));
                            dbHelper.saveOrUpdateSettings(setting);
                            txtChkUpdate.setChecked(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (CIDApp.getInstance().getCountryList() == null) {
                        splash_tvMessage.setText("Fetching data from server...");
                        GetCountrys();
                    } else {
                        layProgress.setVisibility(View.GONE);
                        splash_tvMessage.setText("Complete");
                        splash_tvStart.setVisibility(View.VISIBLE);
                        if (PrefHelper.getStatus(activity).equals(Constants.STATUS.NONE.toString())) {
                            txtChkRegister.setChecked(true);
                        } else {
                            txtChkRegister.setChecked(false);
                        }
                        splash_tvStart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                launchApp();
                            }
                        });
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // DisplayUtils.hideProgressDiaog();
                    Log.d(TAG, "Error:" + error.getMessage());
                    layProgress.setVisibility(View.GONE);
                    //Toast.makeText(activity, "Connection fail...", Toast.LENGTH_SHORT).show();
                    DisplayUtils.showCustomToast(activity, "Connection fail...");
                }
            });
            CIDApp.getInstance().addToRequestQueue(jsonArrayRequest, TAG);
        } else {
            layProgress.setVisibility(View.GONE);
            splash_tvMessage.setText("Complete");
            splash_tvStart.setVisibility(View.VISIBLE);
            if (PrefHelper.getStatus(activity).equals(Constants.STATUS.NONE.toString())) {
                txtChkRegister.setChecked(true);
            } else {
                txtChkRegister.setChecked(false);
            }
            splash_tvStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchApp();
                }
            });
        }
    }

    private void GetCountrys() {
        // DisplayUtils.showProgressDialog(activity,"Fetching data from server...");
        splash_tvMessage.setText("Fetching data from server...");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.COUNTRY_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //DisplayUtils.hideProgressDiaog();
                if (response.has("success")) {
                    try {
                        if (response.getInt("success") == 1) {
                            if (response.has("data")) {
                                if (!response.isNull("data")) {
                                    parseContryList(response);
                                } else {
                                    //Toast.makeText(Splash.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                                    DisplayUtils.showCustomToast(activity, "Failed to load data");
                                }
                            }
                        } else {
                            //Toast.makeText(Splash.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                            DisplayUtils.showCustomToast(activity, "Failed to load data");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // DisplayUtils.hideProgressDiaog();
                Log.d(TAG, "Error:" + error.getMessage());
                layProgress.setVisibility(View.GONE);
                //Toast.makeText(activity, "Connection fail...", Toast.LENGTH_SHORT).show();
                DisplayUtils.showCustomToast(activity, "Connection fail...");
            }
        });
        CIDApp.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
    }

    private void parseContryList(JSONObject object) {
        List<Country> countryList = new ArrayList<>();
        try {
            JSONObject objData = object.getJSONObject("data");

            Iterator<String> iterator = objData.keys();
            while (iterator.hasNext()) {

                String currentKey = iterator.next();

                Country country = new Country();
                country.setId(currentKey);
                country.setCountryName(objData.getString(currentKey));
                countryList.add(country);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        CIDApp.getInstance().setCountryList(countryList);
        splash_tvMessage.setText("Complete");
        layProgress.setVisibility(View.GONE);
        splash_tvStart.setVisibility(View.VISIBLE);

        if (PrefHelper.getStatus(activity).equals(Constants.STATUS.NONE.toString())) {
            txtChkRegister.setChecked(true);
        } else {
            txtChkRegister.setChecked(false);
        }

        splash_tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchApp();
            }
        });
    }

    private void launchApp() {
        if (PrefHelper.getStatus(activity).equals(Constants.STATUS.NONE.toString())) {
            startActivity(new Intent(Splash.this, Register.class));
            Splash.this.finish();
        } else if (PrefHelper.getStatus(activity).equals(Constants.STATUS.PHONE.toString())) {
            startActivity(new Intent(Splash.this, AddPhone.class));
            Splash.this.finish();
        } else if (PrefHelper.getStatus(activity).equals(Constants.STATUS.SMS.toString())) {
            startActivity(new Intent(Splash.this, VerifySMS.class));
            Splash.this.finish();
        } else if (PrefHelper.getStatus(activity).equals(Constants.STATUS.UNCONFIRMED.toString())) {
            startActivity(new Intent(Splash.this, Email.class));
            Splash.this.finish();
        } else if (PrefHelper.getStatus(activity).equals(Constants.STATUS.ACTIVE.toString())) {
            if (PrefHelper.getBoolean(activity, Constants.KEY_ISLOGIN)) {
                startActivity(new Intent(Splash.this, Home_old.class));
                Splash.this.finish();
            } else {
                startActivity(new Intent(Splash.this, Login.class));
                Splash.this.finish();
            }
        } else if (PrefHelper.getStatus(activity).equals(Constants.STATUS.BANNED.toString())) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            alertDialog
                    .setMessage("Your account is banned.Please contact admin to activate your account.")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    }).create().show();

        }
    }

    private void getLocationInfo() {

        locationService = new AppLocationService(activity, locationHandler);
        if (locationService.isLocationServiceEnabled(activity)) {
            DisplayUtils.showProgressDialog(activity, "Please wait getting location...");
            locationService.getLocation();
            /*if (gpsLocation != null) {
                *//*DisplayUtils.hideProgressDiaog();
                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();
                String result = "Latitude: " + gpsLocation.getLatitude() +
                        " Longitude: " + gpsLocation.getLongitude();
                //tvAddress.setText(result);

                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latitude, longitude, activity, new GeocoderHandler());*//*
            } else {
              //  locationService.requestUpdate();
            }*/
        } else {
            showSettingsAlert();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Some features might not work as expected if location services is disabled.Please enable location service.");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        txtChkLocation.setChecked(false);
                        GetSettings();
                    }
                });
        alertDialog.show();
    }

    private boolean isLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    private void requestLocationService() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

        }
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                getLocationInfo();
            } else {
                //Displaying another toast if permission is not granted
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationService != null) {
            locationService.stopUsingGPS();
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            String country, countryCode;
            switch (message.what) {
                case 1:
                    txtChkLocation.setChecked(true);
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    country = bundle.getString("country");
                    countryCode = bundle.getString("countryCode");
                    if (countryCode != null) {
                        PrefHelper.saveString(activity, Constants.KEY_CURRENT_COUNTRY, countryCode.toString().toLowerCase());
                        PrefHelper.saveString(activity, Constants.KEY_CURRENT_COUNTRY_NAME, country);
                    }
                    //Toast.makeText(activity, "Country:"+country+",Country Code:"+countryCode, Toast.LENGTH_SHORT).show();
                    if (!isLocationAvailable) {
                        isLocationAvailable = true;
                        /*if(dbHelper==null)
                            dbHelper=CIDApp.getInstance().getDatabase();
                        if(dbHelper.isSettingEmpty())
                        {
                            GetSettings();
                        }
                        else
                        {
                            launchApp();
                        }*/
                        GetSettings();
                    }

                    break;
                default:
                    locationAddress = null;
            }

        }
    }
}
