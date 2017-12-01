package com.example.cidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cidapp.database.DatabaseHandler;
import com.example.cidapp.model.User;
import com.example.cidapp.service.BroadcastService;
import com.example.cidapp.utils.Constants;
import com.example.cidapp.utils.DisplayUtils;
import com.example.cidapp.utils.NetConst;
import com.example.cidapp.utils.PrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();
    @BindView(R.id.login_layEmail)
    TextInputLayout login_layEmail;
    @BindView(R.id.login_layPassword)
    TextInputLayout login_layPassword;
    @BindView(R.id.login_etEmail)
    TextInputEditText login_etEmail;
    @BindView(R.id.login_etPassword)
    TextInputEditText login_etPassword;
    @BindView(R.id.login_tvTimer)
    AppCompatTextView login_tvTimer;
    @BindView(R.id.login_btnLogin)
    AppCompatButton login_btnLogin;
    @BindView(R.id.login_tvForgot)
    AppCompatTextView login_tvForgot;
    private DatabaseHandler dbHelper;
    private Login activity;
    private int attemtps = 0;
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastService.COUNTDOWN_BR_LOGIN)) {
                updateGUI(intent); // or whatever method used to update your GUI fields
            }
        }
    };

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        activity = this;
        dbHelper = CIDApp.getInstance().getDatabase();
        login_etEmail.setText(PrefHelper.getEmail(activity));
        setUpActionBar();
        setUpTextChangeValidation();
        setUpViews();

    }

    private void setUpActionBar() {
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setUpTextChangeValidation() {
        login_etEmail.addTextChangedListener(new MyTextWatcher(login_etEmail));
        login_etPassword.addTextChangedListener(new MyTextWatcher(login_etPassword));
    }

    private void setUpViews() {
        if (!dbHelper.getSettings().getForgot_password().equals("1")) {
            login_tvForgot.setVisibility(View.GONE);
        } else {
            login_tvForgot.setVisibility(View.VISIBLE);
        }

        if (dbHelper.getSettings().getThrottle_attempts() != null && !dbHelper.getSettings().getThrottle_attempts().equals("")) {
            attemtps = Integer.parseInt(dbHelper.getSettings().getThrottle_attempts());
        } else {
            attemtps = 0;
        }
        Log.d("tag", "Attempts:" + attemtps);
        if (PrefHelper.getThrottle(activity, Constants.KEY_LOGIN_THROTTLE) >= attemtps) {
            //register_tvTimer.setVisibility(View.VISIBLE);
            login_btnLogin.setEnabled(false);
            PrefHelper.saveBoolean(activity, "isLoginThrottle", true);
            startService(new Intent(this, BroadcastService.class));
        } else {
            login_tvTimer.setVisibility(View.GONE);
            login_btnLogin.setEnabled(true);
        }
    }

    @OnClick(R.id.login_btnLogin)
    void OnLoginClick(View view) {
        if (isValid()) {

            DisplayUtils.hideKeyboard(activity);
            if (PrefHelper.getThrottle(activity, Constants.KEY_LOGIN_THROTTLE) < attemtps) {
                Login();
            } else {
                login_tvTimer.setVisibility(View.VISIBLE);
                login_btnLogin.setEnabled(false);
                PrefHelper.saveBoolean(activity, "isLoginThrottle", true);
                startService(new Intent(this, BroadcastService.class));
            }


        } else {
            //Toast.makeText(activity, "Validation error", Toast.LENGTH_SHORT).show();
        }
    }

    private void Login() {
        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d(TAG, "Response:" + response);
                parseResponse(response);
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
                params.put(NetConst.KEY_IMEI_NUMBER, Constants.getDeviceID(activity));
                params.put(NetConst.KEY_EMAIL, login_etEmail.getText().toString());
                params.put(NetConst.KEY_PASSWORD, login_etPassword.getText().toString());
                Log.d("tag", "Params:" + params.toString());
                return params;
            }
        };
        CIDApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private void parseResponse(String response) {
        User user = new User();
        try {
            JSONObject jsonObj = new JSONObject(response);
            if (jsonObj.has("success")) {
                if (jsonObj.getInt("success") == 1) {
                    JSONObject data = jsonObj.getJSONObject("data");
                    user.setUser_id(data.getString("id"));
                    user.setEmail(data.getString("email"));
                    user.setFirstname(data.getString("first_name"));
                    user.setLastname(data.getString("last_name"));
                    user.setPassword(login_etPassword.getText().toString());
                    user.setStatus(data.getString("status"));
                    user.setLogin(false);
                    user.setCreatedAt("");
                    if (data.has("device") && !data.isNull("device")) {
                        JSONArray objDevice = data.getJSONArray("device");
                        if (objDevice.length() > 0) {
                            for (int i = 0; i < objDevice.length(); i++) {
                                JSONObject dataObj = objDevice.getJSONObject(i);
                                PrefHelper.savePhoneInfo(activity, dataObj.getString(NetConst.KEY_PHONE_NUMBER), dataObj.getString(NetConst.KEY_DEVICE_ID));
                                PrefHelper.saveString(activity, Constants.KEY_PHONE_STATUS, dataObj.getString("status"));
                                PrefHelper.saveString(activity, Constants.KEY_COUNTRY_CODE, dataObj.getString("country_code"));
                                PrefHelper.saveString(activity, Constants.KEY_SMS_TOKEN, dataObj.getString("sms_token"));
                                PrefHelper.saveStatus(activity, Constants.STATUS.SMS);

                                user.setPhoneNumber(dataObj.getString(NetConst.KEY_PHONE_NUMBER));
                                user.setPhoneID(dataObj.getString(NetConst.KEY_DEVICE_ID));
                                user.setSMSVerify((dataObj.getString("status").equalsIgnoreCase(Constants.STATUS.UNCONFIRMED.toString())) ? false : true);
                            }
                        } else {
                            user.setPhoneID(null);
                            user.setPhoneNumber(null);
                            user.setSMSVerify(false);
                        }
                    } else {
                        user.setPhoneID(null);
                        user.setPhoneNumber(null);
                        user.setSMSVerify(false);
                    }
                    PrefHelper.saveUserInfo(activity, user);
                    Log.d("tag", "user id:" + PrefHelper.getUserId(activity));
                    //Toast.makeText(activity, jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                    DisplayUtils.showCustomToast(activity, jsonObj.getString("message"));
                    //PrefHelper.saveStatus(activity, Constants.STATUS.ACTIVE);
                    //PrefHelper.saveBoolean(activity,Constants.KEY_ISLOGIN,true);
                    if (user.getStatus().equalsIgnoreCase("unconfirmed")) {
                        PrefHelper.saveStatus(activity, Constants.STATUS.UNCONFIRMED);
                        PrefHelper.saveBoolean(activity, Constants.KEY_ISLOGIN, false);
                        startActivity(new Intent(activity, Email.class));
                    } else if (user.getStatus().equalsIgnoreCase("banned")) {
                        PrefHelper.saveBoolean(activity, Constants.KEY_ISLOGIN, false);
                        PrefHelper.saveStatus(activity, Constants.STATUS.BANNED);
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                        alertDialog
                                .setMessage("Your account is banned.Please contact admin to activate your account.")
                                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        activity.finish();
                                    }
                                }).create().show();
                    } else if (user.getPhoneID() == null && user.getPhoneNumber() == null) {
                        PrefHelper.saveStatus(activity, Constants.STATUS.PHONE);
                        Intent intent = new Intent(Login.this, AddPhone.class);
                        startActivity(intent);
                    } else if (!user.isSMSVerify()) {
                        PrefHelper.saveStatus(activity, Constants.STATUS.SMS);
                        Intent intent = new Intent(Login.this, VerifySMS.class);
                        startActivity(intent);
                    } else if (user.getStatus().equalsIgnoreCase("active")) {
                        PrefHelper.saveStatus(activity, Constants.STATUS.ACTIVE);
                        PrefHelper.saveBoolean(activity, Constants.KEY_ISLOGIN, true);
                        Intent intent = new Intent(Login.this, Home_old.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                } else {
                    int tmp = PrefHelper.getThrottle(activity, Constants.KEY_LOGIN_THROTTLE) + 1;
                    PrefHelper.saveThrottle(activity, Constants.KEY_LOGIN_THROTTLE, tmp);
                    //Toast.makeText(activity, jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                    DisplayUtils.showCustomToast(activity, jsonObj.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isValid() {

        if (!validateEmail())
            return false;
        if (!validatePassword())
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

    private boolean validateEmail() {
        String email = login_etEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            login_layEmail.setError("Please enter valid email");
            requestFocus(login_etEmail);
            return false;
        } else {
            login_layEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (login_etPassword.length() == 0) {
            login_layPassword.setError("Please enter password");
            requestFocus(login_etPassword);
            return false;
        } else {
            login_layPassword.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR_LOGIN));
        Log.i(TAG, "Registered broacast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        //  stopService(new Intent(this, BroadcastService.class));
        //  Log.i(TAG, "Stopped service");
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {

            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            boolean isFinish = intent.getBooleanExtra("isFinish", false);
            if (!isFinish) {
                if (login_tvTimer.getVisibility() == View.GONE)
                    login_tvTimer.setVisibility(View.VISIBLE);
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                login_tvTimer.setText("You have reached maximum attempts. Time left:" + (millisUntilFinished / 1000));
            } else {
                login_btnLogin.setEnabled(true);
                login_tvTimer.setText("");
                login_tvTimer.setVisibility(View.GONE);
                PrefHelper.saveThrottle(activity, Constants.KEY_LOGIN_THROTTLE, 0);
                PrefHelper.saveBoolean(activity, "isLoginThrottle", false);
            }
        }
    }

    @OnClick(R.id.login_tvForgot)
    void onForgotClick() {
        startActivity(new Intent(activity, Forgot.class));
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

                case R.id.login_etEmail:
                    validateEmail();
                    break;
                case R.id.login_etPassword:
                    validatePassword();
                    break;

            }
        }
    }

}
