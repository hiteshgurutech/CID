package com.example.cidapp;

import android.app.Dialog;
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
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Register extends AppCompatActivity {

    private static final String TAG = Register.class.getSimpleName();
    @BindView(R.id.register_layFirstname)
    TextInputLayout register_layFirstname;
    @BindView(R.id.register_layLastname)
    TextInputLayout register_layLastname;
    @BindView(R.id.register_layEmail)
    TextInputLayout register_layEmail;
    @BindView(R.id.register_layPassword)
    TextInputLayout register_layPassword;
    @BindView(R.id.register_layConfirmPass)
    TextInputLayout register_layConfirmPass;
    @BindView(R.id.register_etFirstname)
    TextInputEditText register_etFirstname;
    @BindView(R.id.register_etLastname)
    TextInputEditText register_etLastname;
    @BindView(R.id.register_etEmail)
    TextInputEditText register_etEmail;
    @BindView(R.id.register_etPassword)
    TextInputEditText register_etPassword;
    @BindView(R.id.register_etConfirmPass)
    TextInputEditText register_etConfirmPass;
    @BindView(R.id.register_chkTerms)
    CheckBox register_chkTerms;
    @BindView(R.id.register_tvTerms)
    AppCompatTextView register_tvTerms;
    @BindView(R.id.register_tvTimer)
    AppCompatTextView register_tvTimer;
    @BindView(R.id.register_btnRegister)
    AppCompatButton register_btnRegister;
    @BindView(R.id.register_etCompanyCode)
    TextInputEditText register_etCompanyCode;
    @BindView(R.id.register_layCompanyCode)
    TextInputLayout register_layCompanyCode;
    @BindView(R.id.register_tvAlreadyAccount)
    AppCompatTextView registerTvAlreadyAccount;
    @BindView(R.id.footer_tvAboutUs)
    TextView footerTvAboutUs;
    @BindView(R.id.footer_tvAboutApp)
    TextView footerTvAboutApp;
    @BindView(R.id.footer_tvDetail)
    TextView footerTvDetail;
    @BindView(R.id.footerMenu)
    LinearLayout footerMenu;
    private DatabaseHandler dbHelper;
    private Register activity;
    private int attemtps;
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastService.COUNTDOWN_BR)) {
                updateGUI(intent); // or whatever method used to update your GUI fields
            }
        }
    };

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        activity = this;
        dbHelper = CIDApp.getInstance().getDatabase();
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        register_tvTimer.setVisibility(View.GONE);
        register_etFirstname.addTextChangedListener(new MyTextWatcher(register_etFirstname));
        register_etLastname.addTextChangedListener(new MyTextWatcher(register_etLastname));
        register_etEmail.addTextChangedListener(new MyTextWatcher(register_etEmail));
        register_etPassword.addTextChangedListener(new MyTextWatcher(register_etPassword));
        register_etConfirmPass.addTextChangedListener(new MyTextWatcher(register_etConfirmPass));
        register_etCompanyCode.addTextChangedListener(new MyTextWatcher(register_etCompanyCode));
        register_tvTerms.setText(getString(R.string.label_terms_conditions));
        register_tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTermsDialog();
            }
        });

        if (dbHelper.getSettings().getThrottle_attempts() != null && !dbHelper.getSettings().getThrottle_attempts().equals("")) {
            attemtps = Integer.parseInt(dbHelper.getSettings().getThrottle_attempts());
        } else {
            attemtps = 0;
        }
        if (PrefHelper.getThrottle(activity, Constants.KEY_REGISTER_THROTTLE) >= attemtps) {
            //register_tvTimer.setVisibility(View.VISIBLE);
            register_btnRegister.setEnabled(false);
            PrefHelper.saveBoolean(activity, "isRegisterThrottle", true);
            startService(new Intent(this, BroadcastService.class));
        } else {
            register_tvTimer.setVisibility(View.GONE);
            register_btnRegister.setEnabled(true);
        }

    }

    @OnClick(R.id.register_tvAlreadyAccount)
    void onAlreadyAccount(View view) {
        startActivity(new Intent(activity, Login.class));
    }

    @OnClick(R.id.register_btnRegister)
    void OnRegisterClick(View view) {
        if (isValid()) {
            DisplayUtils.hideKeyboard(activity);
            if (PrefHelper.getThrottle(activity, Constants.KEY_REGISTER_THROTTLE) < attemtps) {
                registerUser();
            } else {
                register_tvTimer.setVisibility(View.VISIBLE);
                register_btnRegister.setEnabled(false);
                PrefHelper.saveBoolean(activity, "isRegisterThrottle", true);
                startService(new Intent(this, BroadcastService.class));
            }
            /*long rowid=dbHelper.addNewUser(user);
            Log.d("tag","New row added:"+rowid);
            if(rowid!=-1)
            {
                Toast.makeText(Register.this, "Register successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Register.this,AddPhone.class));
                Register.this.finish();
            }*/
        }


    }

    private void registerUser() {
        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REGISTER_URL, new Response.Listener<String>() {
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
                DisplayUtils.showCustomToast(activity, error.toString());
                //Toast.makeText(Register.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(NetConst.KEY_EMAIL, register_etEmail.getText().toString());
                params.put(NetConst.KEY_FIRSTNAME, register_etFirstname.getText().toString());
                params.put(NetConst.KEY_LASTNAME, register_etLastname.getText().toString());
                params.put(NetConst.KEY_PASSWORD, register_etPassword.getText().toString());
                //     params.put(NetConst.KEY_COMPANY_CODE, register_etCompanyCode.getText().toString().trim());
                params.put(NetConst.KEY_PASSWORD_CONFIRM, register_etConfirmPass.getText().toString());
                params.put(NetConst.KEY_USERNAME, Constants.getDeviceID(activity));
                params.put(NetConst.KEY_ROLE, NetConst.VAL_ROLE);
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
                    user.setPassword(register_etPassword.getText().toString());
                    user.setPhoneID(PrefHelper.getPhoneId(activity));
                    user.setPhoneNumber(PrefHelper.getPhoneNumber(activity));
                    // user.setEmailVerify(data.getString("is_email_verified").equals("1")?true:false);
                    user.setSMSVerify(false);
                    user.setLogin(false);
                    user.setCreatedAt("");
                    user.setStatus(data.getString("status"));
                    PrefHelper.saveUserInfo(activity, user);
                    PrefHelper.saveStatus(activity, Constants.STATUS.UNCONFIRMED);

                    //Toast.makeText(Register.this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                    DisplayUtils.showCustomToast(activity, jsonObj.getString("message"));
                    if (user.getStatus().equalsIgnoreCase("unconfirmed")) {
                        startActivity(new Intent(Register.this, Email.class));
                        Register.this.finish();
                    } else {
                        //  Toast.makeText(Register.this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                        /*long rowid=dbHelper.addNewUser(user);
                        Log.d("tag","New row added:"+rowid);
                        if(rowid!=-1)
                        {
                            Toast.makeText(Register.this, "Register successfully", Toast.LENGTH_SHORT).show();
                            if(user.isEmailVerify())
                            {
                                startActivity(new Intent(Register.this,AddPhone.class));
                                Register.this.finish();
                            }
                            else
                            {
                                Toast.makeText(Register.this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this,Login.class));
                                Register.this.finish();
                            }
                        }*/
                } else {
                    int tmp = PrefHelper.getThrottle(activity, Constants.KEY_REGISTER_THROTTLE) + 1;
                    PrefHelper.saveThrottle(activity, Constants.KEY_REGISTER_THROTTLE, tmp);
                    //PrefHelper.saveStatus(activity, Constants.STATUS.FAILED);
                    // Toast.makeText(Register.this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                    DisplayUtils.showCustomToast(activity, jsonObj.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitDialog();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Are you want to Exit?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

    }

    private boolean isValid() {
        if (!validateFirstname())
            return false;
        if (!validateLastname())
            return false;
        if (!validateEmail())
            return false;
        if (!validatePassword())
            return false;
        if (!validateConfirmPass())
            return false;
        if (!register_chkTerms.isChecked()) {
            DisplayUtils.showCustomToast(activity, "Please accept terms and condition");
            //Toast.makeText(Register.this, "Please accept terms and condition", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateFirstname() {
        if (register_etFirstname.length() == 0) {
            register_layFirstname.setError("Please enter first name");
            requestFocus(register_etFirstname);
            return false;
        } else {
            register_layFirstname.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastname() {
        if (register_etLastname.length() == 0) {
            register_layLastname.setError("Please enter last name");
            requestFocus(register_etLastname);
            return false;
        } else {
            register_layLastname.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validateCompnayCode() {
        if (register_etCompanyCode.length() == 0) {
            register_layCompanyCode.setError("Please enter company code");
            requestFocus(register_etCompanyCode);
            return false;
        } else {
            register_layCompanyCode.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        String email = register_etEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            register_layEmail.setError("Please enter valid email");
            requestFocus(register_etEmail);
            return false;
        } else {
            register_layEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (register_etPassword.length() == 0) {
            register_layPassword.setError("Please enter password");
            requestFocus(register_etPassword);
            return false;
        } else if (register_etPassword.length() > 0 && register_etPassword.length() <= 6) {
            register_layPassword.setError(Html.fromHtml("<font color='#ed6409'>Passoword is too small</font>"));
            //register_etPassword.getBackground().setColorFilter(Color.parseColor("#ed6409"), PorterDuff.Mode.SRC_IN);
            requestFocus(register_etPassword);
            return false;
        } else {
            register_layPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateConfirmPass() {
        String pass = register_etPassword.getText().toString().trim();
        String confirm = register_etConfirmPass.getText().toString().trim();
        if (register_etConfirmPass.length() == 0) {
            register_layConfirmPass.setError("Please enter Confirm password");
            requestFocus(register_etConfirmPass);
            return false;
        } else if (!pass.equals(confirm)) {
            register_layConfirmPass.setError(Html.fromHtml("<font color='#ed6409'>Confirm passoword not match with password</font>"));
            //register_etPassword.getBackground().setColorFilter(Color.parseColor("#ed6409"), PorterDuff.Mode.SRC_IN);
            requestFocus(register_etConfirmPass);
            return false;
        } else {
            register_layConfirmPass.setErrorEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            exitDialog();
            //onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openTermsDialog() {
        final Dialog dialog = new Dialog(Register.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_full_screen);

        AppCompatTextView tvTitle = (AppCompatTextView) dialog.findViewById(R.id.dialog_tvTitle);
        AppCompatButton btnIAgree = (AppCompatButton) dialog.findViewById(R.id.dialog_btnIAgree);
        WebView webView = (WebView) dialog.findViewById(R.id.dialog_webView);
        ImageView btnClose = (ImageView) dialog.findViewById(R.id.dialog_btnClose);
        webView.loadData("Terms & Conditions content will be update soon....", "text/html", "UTF-8");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnIAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register_chkTerms.setChecked(true);
                dialog.dismiss();
            }
        });
        tvTitle.setText("Terms & Conditions");
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
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
                if (register_tvTimer.getVisibility() == View.GONE)
                    register_tvTimer.setVisibility(View.VISIBLE);
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                register_tvTimer.setText("You have reached maximum attempts. Time left:" + (millisUntilFinished / 1000));
            } else {
                register_btnRegister.setEnabled(true);
                register_tvTimer.setText("");
                register_tvTimer.setVisibility(View.GONE);
                PrefHelper.saveThrottle(activity, Constants.KEY_REGISTER_THROTTLE, 0);
                PrefHelper.saveBoolean(activity, "isRegisterThrottle", false);
            }
        }
    }

    @OnClick(R.id.footer_tvAboutUs)
    void onClickAboutUs(View view) {
        DisplayUtils.displayCMSDialog(activity, "About Us", dbHelper.getSettings().getAbout_us());
    }

    @OnClick(R.id.footer_tvAboutApp)
    void onClickAboutApp(View view) {
        DisplayUtils.displayCMSDialog(activity, "About App", dbHelper.getSettings().getAbout_app());
    }

    @OnClick(R.id.footer_tvDetail)
    void onClickDetail(View view) {
        DisplayUtils.displayCMSDialog(activity, "Details", dbHelper.getSettings().getAbout_details());
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
                case R.id.register_etFirstname:
                    validateFirstname();
                    break;
                case R.id.register_etLastname:
                    validateLastname();
                    break;
                case R.id.register_etEmail:
                    validateEmail();
                    break;
                case R.id.register_etCompanyCode:
                    validateCompnayCode();
                    break;

                case R.id.register_etPassword:
                    validatePassword();
                    break;
                case R.id.register_etConfirmPass:
                    validateConfirmPass();
                    break;
            }
        }
    }


}
