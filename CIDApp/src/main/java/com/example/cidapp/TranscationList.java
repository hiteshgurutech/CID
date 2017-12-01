package com.example.cidapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranscationList extends AppCompatActivity {


    @BindView(R.id.listTranscation)
    ListView listTranscation;
    private TranscationList activity;

    private List<TransctionModel> transctionModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        ButterKnife.bind(this);
        activity = this;
        getSupportActionBar().setTitle("Transaction List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getTransctionListApi();

        listTranscation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TransctionModel transctionModel = transctionModelList.get(i);
                Intent intent = new Intent(TranscationList.this, TranscationDetail.class);
                intent.putExtra("model", transctionModel);
                startActivity(intent);
            }
        });

    }

    private void getTransctionListApi() {
        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, Constants.TRNASCATION_LIST_URL + "/" + PrefHelper.getUserId(activity), new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                DisplayUtils.hideProgressDiaog();
                Log.d("tag", "Response:" + res);
                JSONObject response = null;
                try {
                    response = new JSONObject(res);
                    if (response.has("success")) {
                        if (response.getInt("success") == 1) {
                            if (response.has("data")) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                transctionModelList = new ArrayList<>();
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        TransctionModel transctionModel = new TransctionModel();
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        transctionModel.setId(jsonObject.getString("id"));
                                        transctionModel.setPhone_number(jsonObject.getString("phone_number"));
                                        transctionModel.setService_type("Android");
                                        transctionModel.setTransaction_type(jsonObject.getString("transaction_type"));
                                        transctionModel.setCreated_at(jsonObject.getString("created_at"));
                                        transctionModel.setUpdated_at(jsonObject.getString("updated_at"));
                                        transctionModel.setCharges(jsonObject.getString("charge"));
                                        transctionModelList.add(transctionModel);
                                    }
                                    listTranscation.setAdapter(new TranscationListAdapter(activity, transctionModelList));
                                }
                            }

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
        CIDApp.getInstance().addToRequestQueue(jsonObjectRequest, "tag");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    class TranscationListAdapter extends BaseAdapter {

        Context context;
        List<TransctionModel> transctionModels;
        LayoutInflater mInflater;


        TranscationListAdapter(Context context, List<TransctionModel> transctionModelList) {
            this.context = context;
            this.transctionModels = transctionModelList;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return transctionModels.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            TransctionHolder transctionHolder = null;
            if (view == null) {
                view = mInflater.inflate(R.layout.lay_transction_row, null);
                transctionHolder = new TransctionHolder();
                transctionHolder.txtTransctionType = (TextView) view.findViewById(R.id.txtTransctionType);
                transctionHolder.txtPhoneNumber = (TextView) view.findViewById(R.id.txtPhoneNumber);
                transctionHolder.txtServiceType = (TextView) view.findViewById(R.id.txtServiceType);
                transctionHolder.txtCreatedDate = (TextView) view.findViewById(R.id.txtCreatedDate);
                view.setTag(transctionHolder);
            } else {
                transctionHolder = (TransctionHolder) view.getTag();
            }

            final TransctionModel model = transctionModels.get(i);

            transctionHolder.txtTransctionType.setText(model.getTransaction_type());
            transctionHolder.txtCreatedDate.setText(model.getCreated_at());
            transctionHolder.txtPhoneNumber.setText(model.getPhone_number());

            return view;
        }

        class TransctionHolder {
            TextView txtTransctionType, txtPhoneNumber, txtServiceType, txtCreatedDate;
        }

    }

}
