package com.example.cidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class Email extends AppCompatActivity {

    private static final String TAG = Email.class.getSimpleName();
    @BindView(R.id.email_tvEmailText)
    TextView email_tvEmailText;

    private Email activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        ButterKnife.bind(this);
        activity = this;
        getSupportActionBar().setTitle("Verify you email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        email_tvEmailText.setText(String.format("We have send verification email to (%s) to verify your identity. Please click link in that email to continue. You may request new one click on RESEND EMAIL button.", PrefHelper.getString(activity, Constants.KEY_EMAIL)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("Verify you email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        email_tvEmailText.setText(String.format("We have send verification email to (%s) to verify your identity. Please click link in that email to continue. You may request new one click on RESEND EMAIL button.", PrefHelper.getString(activity, Constants.KEY_EMAIL)));
    }

    @OnClick(R.id.email_btnChangeEmail)
    void OnEmailChangeClick(View view) {
        startActivity(new Intent(activity, ChangeEmail.class));
    }

    @OnClick(R.id.email_btnResend)
    void OnResendClick(View view) {
        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RESEND_EMAIL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d(TAG, "Response:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {
                        if (jsonObject.getInt("success") == 1) {
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            PrefHelper.saveStatus(activity, Constants.STATUS.UNCONFIRMED);
                        } else {
                            PrefHelper.saveStatus(activity, Constants.STATUS.UNCONFIRMED);
                            //startActivity(new Intent(activity,VerifySMS.class));
                        }
                        DisplayUtils.showCustomToast(activity, jsonObject.getString("message"));
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
                Log.d(TAG, "Params:" + params.toString());
                return params;
            }
        };
        CIDApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    @OnClick(R.id.email_btnRedirect)
    void OnRedirectLogin(View view) {
        startActivity(new Intent(activity, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        activity.finish();
    }


}
