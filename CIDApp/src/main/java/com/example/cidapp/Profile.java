package com.example.cidapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.cidapp.model.Country;
import com.example.cidapp.service.AppLocationService;
import com.example.cidapp.service.LocationAddress;
import com.example.cidapp.utils.Constants;
import com.example.cidapp.utils.DisplayUtils;
import com.example.cidapp.utils.NetConst;
import com.example.cidapp.utils.PrefHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Profile extends AppCompatActivity {

    private static final String TAG = Profile.class.getSimpleName();
    @BindView(R.id.update_layFirstname)
    TextInputLayout update_layFirstname;
    @BindView(R.id.update_layLastname)
    TextInputLayout update_layLastname;
    @BindView(R.id.update_layStreet1)
    TextInputLayout update_layStreet1;
    @BindView(R.id.update_layStreet2)
    TextInputLayout update_layStreet2;
    @BindView(R.id.update_layCity)
    TextInputLayout update_layCity;
    @BindView(R.id.update_layPostal)
    TextInputLayout update_layPostal;


    @BindView(R.id.update_etFirstname)
    TextInputEditText update_etFirstname;
    @BindView(R.id.update_etLastname)
    TextInputEditText update_etLastname;
    @BindView(R.id.update_etStreet1)
    TextInputEditText update_etStreet1;
    @BindView(R.id.update_etStreet2)
    TextInputEditText update_etStreet2;
    @BindView(R.id.update_etCity)
    TextInputEditText update_etCity;
    @BindView(R.id.update_etPostal)
    TextInputEditText update_etPostal;

    @BindView(R.id.update_etDate)
    TextView update_etDate;
    @BindView(R.id.update_tvEmail)
    AppCompatTextView update_tvEmail;
    @BindView(R.id.update_tvPhone)
    AppCompatTextView update_tvPhone;
    @BindView(R.id.update_spCountry)
    Spinner update_spCountry;
    @BindView(R.id.update_tvStatus)
    AppCompatTextView update_tvStatus;
    @BindView(R.id.update_tvProfileId)
    AppCompatTextView update_tvProfileId;
    @BindView(R.id.update_tvLastupdate)
    AppCompatTextView update_tvLastupdate;
    @BindView(R.id.update_tvGeoCode)
    AppCompatTextView update_tvGeoCode;


    private Profile activity;
    private String countryId = "";
    private List<Country> countryList;
    private Calendar calendar;
    private int year, month, day;
    private AppLocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        activity = this;
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();

    }

    private void init() {
        update_etFirstname.addTextChangedListener(new MyTextWatcher(update_etFirstname));
        update_etLastname.addTextChangedListener(new MyTextWatcher(update_etLastname));
        update_etStreet1.addTextChangedListener(new MyTextWatcher(update_etStreet1));
        //update_etStreet2.addTextChangedListener(new MyTextWatcher(update_etStreet2));
        update_etCity.addTextChangedListener(new MyTextWatcher(update_etCity));
        update_etPostal.addTextChangedListener(new MyTextWatcher(update_etPostal));

      /*  try {
            update_tvLastupdate.setText("Last update :"+sdf.format(sdf1.parse(PrefHelper.getLastUpdateDate(activity))));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        update_tvStatus.setVisibility(View.GONE);
        update_tvProfileId.setVisibility(View.GONE);
        getLocationInfo();
        GetCountrys();

        //  getProfileApi();

    }

    private double lat, lng;
    private Handler locationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    LocationAddress locationAddress = new LocationAddress();
                    lat = bundle.getDouble("lat");
                    lng = bundle.getDouble("lng");
                    locationAddress.getAddressFromLocation(lat, lng, activity, new GeocoderHandler());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:

                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    update_tvGeoCode.setText("Current Location\n" + locationAddress);
                    break;
                default:
                    locationAddress = null;
            }

        }
    }

    private void getLocationInfo() {

        locationService = new AppLocationService(activity, locationHandler);
        if (locationService.isLocationServiceEnabled(activity)) {
            //DisplayUtils.showProgressDialog(activity, "Please wait getting location...");
            locationService.getLocation();
        } else {

        }
    }

    private void setUpSpinner() {
        countryList = CIDApp.getInstance().getCountryList();
        update_spCountry.setPrompt("Select Country");
        Collections.sort(countryList, new Comparator<Country>() {
            @Override
            public int compare(Country country, Country t1) {
                return country.getCountryName().compareToIgnoreCase(t1.getCountryName());
            }
        });
        update_spCountry.setAdapter(new CountrySpinnerAdapter(activity, countryList));
        update_spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void GetCountrys() {
        DisplayUtils.showProgressDialog(activity, "Please wait...");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.COUNTRY_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                DisplayUtils.hideProgressDiaog();
                if (response.has("success")) {
                    try {
                        if (response.getInt("success") == 1) {
                            if (response.has("data")) {
                                if (!response.isNull("data")) {
                                    parseContryList(response);
                                } else {
                                    //Toast.makeText(Profile.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                                    DisplayUtils.showCustomToast(activity, "Failed to load data");
                                }
                            }
                        } else {
                            //  Toast.makeText(Profile.this, "Failed to load data", Toast.LENGTH_SHORT).show();
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
                DisplayUtils.hideProgressDiaog();
                Log.d(TAG, "Error:" + error.getMessage());
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
        setUpSpinner();
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
                    if (data.has("first_name") && !data.isNull("first_name"))
                        update_etFirstname.setText(data.getString("first_name"));
                    if (data.has("last_name") && !data.isNull("last_name"))
                        update_etLastname.setText(data.getString("last_name"));
                    if (data.has("email") && !data.isNull("email"))
                        update_tvEmail.setText("Email:" + data.getString("email"));
                    if (data.has("street1") && !data.isNull("street1"))
                        update_etStreet1.setText(data.getString("street1"));
                    if (data.has("street2") && !data.isNull("street2"))
                        update_etStreet2.setText(data.getString("street2"));
                    if (data.has("city") && !data.isNull("city"))
                        update_etCity.setText(data.getString("city"));
                    if (data.has("postal_code") && !data.isNull("postal_code"))
                        update_etPostal.setText(data.getString("postal_code"));
                    if (data.has("birthday") && !data.isNull("birthday")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date d1 = sdf.parse(data.getString("birthday"));
                        calendar.setTime(d1);
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                        update_etDate.setText(sdf1.format(calendar.getTime()));
                    }
                    if (data.has("country_id") && !data.isNull("country_id")) {
                        countryId = data.getString("country_id");
                        int index = getCountryIndexForId(countryId);
                        update_spCountry.setSelection(index);
                    } else {
                        Log.d("tag", "Current country:" + PrefHelper.getString(activity, Constants.KEY_CURRENT_COUNTRY_NAME));
                        String currentCountry = PrefHelper.getString(activity, Constants.KEY_CURRENT_COUNTRY_NAME);
                        if (currentCountry == null)
                            currentCountry = "United States";
                        int index = getCountryIndexForName(currentCountry);
                        update_spCountry.setSelection(index);
                    }
                    if (data.has("status") && !data.isNull("status"))
                        update_tvStatus.setText("Status:" + data.getString("status"));
                    if (data.has("id") && !data.isNull("id"))
                        update_tvProfileId.setText("Profile ID:" + data.getString("id"));
                    if (data.has("device") && !data.isNull("device")) {
                        JSONObject device = data.getJSONObject("device");
                        update_tvPhone.setText("Phone:" + device.getString("phone_number"));
                    }
                    if (data.has("updated_at") && (!data.isNull("updated_at"))) {
                        String update_at = data.getString("updated_at");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date date = sdf1.parse(update_at);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE,dd MMM yyyy hh:mm a");
                        update_tvLastupdate.setText("Last Update :" + sdf.format(date));
                    }

                } else {
                    //Toast.makeText(Profile.this, "Failed:", Toast.LENGTH_SHORT).show();
                    DisplayUtils.showCustomToast(activity, "Failed:");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.update_btnUpdate)
    void OnUpdate(View view) {
        if (isValid()) {
            DisplayUtils.hideKeyboard(activity);
            callUpdateAPI();
        }
    }

    boolean isCancel = false;

    @OnClick(R.id.update_etDate)
    void OnDateClick(View view) {
        isCancel = false;
        final DatePickerDialog datePickerDialog = new DatePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                if (!isCancel) {
                    Log.d("tag", "On date set called");
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    Calendar currentCal = Calendar.getInstance();

                    long millis = currentCal.getTimeInMillis() - calendar.getTimeInMillis();
                    Log.d("tag", "Current Millis:" + currentCal.getTimeInMillis() + " selected:" + calendar.getTimeInMillis() + " millis:" + millis);
                    long ageInYear = millis / (24 * 60 * 60 * 1000);
                    Log.d("tag", "Age in year :" + (ageInYear / 365));
                    if ((ageInYear / 365) > 5) {
                        activity.year = year;
                        activity.month = month;
                        activity.day = dayOfMonth;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        update_etDate.setText(sdf.format(calendar.getTime()));
                    } else {
                        Toast.makeText(Profile.this, "Please select proper date for birth date.", Toast.LENGTH_SHORT).show();
                    }
                }
            }


        }, year, month, day);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        datePickerDialog.setTitle("Set Date");
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("tag", "On cancel button listener");
                isCancel = true;
            }
        });

        datePickerDialog.setCancelable(false);
        //datePickerDialog.getDatePicker().setCalendarViewShown(false);

        datePickerDialog.show();
    }

    private String getFormatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    private void callUpdateAPI() {
        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_PROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d(TAG, "Response:" + response);
                //parseProfileResponse(response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("success")) {
                        if (json.getInt("success") == 1) {
                            //  PrefHelper.saveLastUpdateDate(activity,sdf1.format(new Date()));
                            //    Toast.makeText(Profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            DisplayUtils.showCustomToast(activity, "Profile updated successfully");
                        } else {
                            //Toast.makeText(Profile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                            DisplayUtils.showCustomToast(activity, "Failed to update profile");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("tag", "error:" + e.getMessage());
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
                params.put("id", PrefHelper.getUserId(activity));
                params.put(NetConst.KEY_FIRSTNAME, update_etFirstname.getText().toString());
                params.put(NetConst.KEY_LASTNAME, update_etLastname.getText().toString());
                params.put(NetConst.KEY_STREET1, update_etStreet1.getText().toString());
                params.put(NetConst.KEY_STREET2, update_etStreet2.getText().toString());
                params.put(NetConst.KEY_CITY, update_etCity.getText().toString());
                params.put(NetConst.KEY_POSTAL_CODE, update_etPostal.getText().toString());
                params.put(NetConst.KEY_BIRTHDAY, getFormatDate());
                params.put(NetConst.KEY_COUNTRY_ID, countryList.get(update_spCountry.getSelectedItemPosition()).getId());
                Log.d("tag", "Params:" + params.toString());
                return params;
            }
        };
        CIDApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private boolean isValid() {
        if (!validateFirstname())
            return false;
        if (!validateLastname())
            return false;
        if (!validateStreet1())
            return false;
       /* if(!validateStreet2())
            return false;*/
        if (!validateCity())
            return false;
        if (!validatePostal())
            return false;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {

            switch (view.getId()) {
                case R.id.update_etFirstname:
                    validateFirstname();
                    break;
                case R.id.update_etLastname:
                    validateLastname();
                    break;
                case R.id.update_etStreet1:
                    validateStreet1();
                    break;
                case R.id.update_etStreet2:
                    validateStreet2();
                    break;
                case R.id.update_etCity:
                    validateCity();
                    break;
                case R.id.update_etPostal:
                    validatePostal();
                    break;
            }
        }
    }

    private boolean validateFirstname() {
        if (update_etFirstname.length() == 0) {
            update_layFirstname.setError("Please enter first name");
            requestFocus(update_etFirstname);
            return false;
        } else {
            update_layFirstname.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastname() {
        if (update_etLastname.length() == 0) {
            update_layLastname.setError("Please enter last name");
            requestFocus(update_etLastname);
            return false;
        } else {
            update_layLastname.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateStreet1() {
        if (update_etStreet1.length() == 0) {
            update_layStreet1.setError("Please enter street1");
            requestFocus(update_etStreet1);
            return false;
        } else {
            update_layStreet1.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateStreet2() {
        if (update_etStreet2.length() == 0) {
            update_layStreet2.setError("Please enter street2");
            requestFocus(update_etStreet2);
            return false;
        } else {
            update_layStreet2.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateCity() {
        if (update_etCity.length() == 0) {
            update_layCity.setError("Please enter city");
            requestFocus(update_etCity);
            return false;
        } else {
            update_layCity.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePostal() {
        if (update_etPostal.length() == 0) {
            update_layPostal.setError("Please enter postal code");
            requestFocus(update_etPostal);
            return false;
        } else {
            update_layPostal.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private int getCountryIndexForId(String countryId) {
        for (int i = 0; i < countryList.size(); i++) {
            if (countryId.equals(countryList.get(i).getId()))
                return i;
        }
        return 0;
    }

    private int getCountryIndexForName(String name) {
        for (int i = 0; i < countryList.size(); i++) {
            if (name.toLowerCase().equalsIgnoreCase(countryList.get(i).getCountryName().toLowerCase()))
                return i;
        }
        return 0;
    }

    private class CountrySpinnerAdapter extends ArrayAdapter<Country> {
        private List<Country> countryList;

        public CountrySpinnerAdapter(Context context, List<Country> countryList) {
            super(context, R.layout.lay_spinner_item, R.id.textView1, countryList);
            this.countryList = countryList;
        }

        @Override
        public int getCount() {
            return countryList.size();
        }

        @Override
        public Country getItem(int position) {
            return countryList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.lay_spinner_item, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView1.setText(getItem(position).getCountryName());
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        class ViewHolder {
            private TextView textView1;
        }
    }
}
