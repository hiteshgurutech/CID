package com.example.cidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cidapp.model.TransctionModel;
import com.example.cidapp.utils.Constants;
import com.example.cidapp.utils.DisplayUtils;
import com.example.cidapp.utils.NetConst;
import com.example.cidapp.utils.PrefHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranscationDetail extends AppCompatActivity {

    @BindView(R.id.txtTransctionType)
    TextView txtTransctionType;
    @BindView(R.id.txtPhoneNumber)
    TextView txtPhoneNumber;
    @BindView(R.id.txtTransctionCharge)
    TextView txtTransctionCharge;
    @BindView(R.id.txtServiceType)
    TextView txtServiceType;
    @BindView(R.id.txtCreatedDate)
    TextView txtCreatedDate;
    private TranscationDetail activity;
    private List<TransctionModel> transctionModelList;
    TransctionModel transctionModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        ButterKnife.bind(this);
        activity = this;
        getSupportActionBar().setTitle("Transaction Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            transctionModel = (TransctionModel) intent.getSerializableExtra("model");
            //   getTransctionDetailApi();
        }

        txtCreatedDate.setText(transctionModel.getCreated_at());
        txtTransctionCharge.setText(transctionModel.getCharges());
        txtPhoneNumber.setText(transctionModel.getPhone_number());
        txtTransctionType.setText(transctionModel.getTransaction_type());

    }

    private void getTransctionDetailApi() {
        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, Constants.TRNASCATION_DETAIL_URL + "/" + transctionModel.getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                DisplayUtils.hideProgressDiaog();
                Log.d("tag", "Response:" + res);
                JSONObject response = null;
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
        CIDApp.getInstance().addToRequestQueue(jsonObjectRequest, "tag");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
