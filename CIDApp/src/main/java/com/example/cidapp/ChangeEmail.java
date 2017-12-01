package com.example.cidapp;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeEmail extends AppCompatActivity {

    private static final String TAG = ChangeEmail.class.getSimpleName();
    @BindView(R.id.change_tvEmailText)
    TextView change_tvEmailText;
    @BindView(R.id.change_layEmail)
    TextInputLayout change_layEmail;
    @BindView(R.id.change_etEmail)
    TextInputEditText change_etEmail;
    private ChangeEmail activity;

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        ButterKnife.bind(this);
        activity = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update Email");
        change_tvEmailText.setText("Your current email address is :" + PrefHelper.getString(activity, Constants.KEY_EMAIL));
        change_etEmail.setText(PrefHelper.getEmail(activity));
        change_etEmail.addTextChangedListener(new MyTextWatcher(change_etEmail));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.change_btnUpdate)
    void onSubmitClick(View view) {
        if (isValidEmail()) {
            DisplayUtils.hideKeyboard(activity);
            callUpdateApi();
        }
    }

    private void callUpdateApi() {

        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_EMAIL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d(TAG, "Response:" + response);
                //parseProfileResponse(response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("success")) {
                        if (json.getInt("success") == 1) {
                            PrefHelper.saveString(activity, Constants.KEY_EMAIL, change_etEmail.getText().toString());
                            DisplayUtils.showCustomToast(activity, "Profile updated successfully");
                            activity.finish();
                        } else {
                            Toast.makeText(activity, "Failed to update profile", Toast.LENGTH_SHORT).show();
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
                params.put(NetConst.KEY_EMAIL, change_etEmail.getText().toString());

                Log.d("tag", "Params:" + params.toString());
                return params;
            }
        };
        CIDApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private boolean isValidEmail() {
        String email = change_etEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            change_layEmail.setError("Please enter valid email");
            requestFocus(change_etEmail);
            return false;
        } else {
            change_layEmail.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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

                case R.id.change_etEmail:
                    isValidEmail();
                    break;


            }
        }
    }
}
