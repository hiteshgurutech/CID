package com.example.cidapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cidapp.database.DatabaseHandler;
import com.example.cidapp.model.TransctionListModel;
import com.example.cidapp.utils.Constants;
import com.example.cidapp.utils.DisplayUtils;
import com.example.cidapp.utils.NetConst;
import com.example.cidapp.utils.PrefHelper;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.cidapp.R.id.phone_input_lay;

public class TranscationForm extends AppCompatActivity {


    @BindView(R.id.form_transctionTypeList)
    Spinner formTransctionTypeList;
    @BindView(R.id.form_etMessage)
    TextInputEditText formEtMessage;
    @BindView(R.id.form_layMessage)
    TextInputLayout formLayMessage;
    @BindView(R.id.form_btnTransction)
    AppCompatButton formBtnTransction;
    @BindView(phone_input_lay)
    IntlPhoneInput phoneInputLay;
    private TranscationForm activity;
    private String tran_id = "", tran_type = "", mobile = "";
    private List<TransctionListModel> transctionListModelList;
    private List<String> arrayList = new ArrayList<>();
    private DatabaseHandler dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_form);
        ButterKnife.bind(this);
        activity = this;
        getSupportActionBar().setTitle("Transaction");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHelper = CIDApp.getInstance().getDatabase();
        init();

        getTranscationTypeList();

        formBtnTransction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    addTranscation();
                }
            }
        });
    }

    private boolean isValid() {
        if (formLayMessage.getVisibility() == View.VISIBLE) {
            if (!validateMessages())
                return false;
        }
        return true;
    }


    private void addTranscation() {
        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_TRNASCATION_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d("tag", "Response:" + response);
                parseTransctionSubmitResponse(response);
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
                params.put("transaction_type", tran_type);
                params.put("phone_number", phoneInputLay.getNumber());
                params.put("service_type", "Andorid");
                params.put("message", formEtMessage.getText().toString());
                Log.d("tag", "param:" + params.toString());
                return params;
            }
        };
        CIDApp.getInstance().addToRequestQueue(stringRequest, "tag");
    }

    private void init() {
        formEtMessage.addTextChangedListener(new MyTextWatcher(formEtMessage));
        phoneInputLay.setView(formBtnTransction);
        if (PrefHelper.getString(activity, Constants.KEY_CURRENT_COUNTRY) != null) {
            Log.d("tag", "Current Country:" + PrefHelper.getString(activity, Constants.KEY_CURRENT_COUNTRY));
            phoneInputLay.setCUREENT_COUNTRY(PrefHelper.getString(activity, Constants.KEY_CURRENT_COUNTRY));
        }

        formBtnTransction.setEnabled(false);
        formBtnTransction.setBackgroundColor(Color.GRAY);

    }

    private void getTranscationTypeList() {
        DisplayUtils.showProgressDialog(activity, "Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.TRNASCATION_TYPE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DisplayUtils.hideProgressDiaog();
                Log.d("tag", "Response:" + response);
                parseResponse(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DisplayUtils.hideProgressDiaog();
                DisplayUtils.showCustomToast(activity, error.getMessage());
            }
        }) {

        };
        CIDApp.getInstance().addToRequestQueue(stringRequest, "tag");
    }

    private void parseTransctionSubmitResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("success")) {
                if (jsonObject.getInt("success") == 1) {
                    Intent intent = new Intent(activity, Home_old.class);
                    startActivity(intent);
                    finish();
                }
                DisplayUtils.showCustomToast(TranscationForm.this, jsonObject.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseResponse(String response) {
        Log.d("tag", "Response:" + response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject object = jsonObject.getJSONObject("data");
            arrayList = new ArrayList<>();

            if (object.has("Reverse Lookup"))
                arrayList.add(object.getString("Reverse Lookup"));
            if (object.has("Request Removal from Call List"))
                arrayList.add(object.getString("Request Removal from Call List"));
            if (object.has("Request Quote"))
                arrayList.add(object.getString("Request Quote"));
            if (object.has("Business Text Message Sent"))
                arrayList.add(object.getString("Business Text Message Sent"));

            formTransctionTypeList.setAdapter(new TransctionListSpinnerAdapter(activity, arrayList));

            tran_type = arrayList.get(0);

            formTransctionTypeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    tran_type = arrayList.get(i);

                    if (tran_type.equalsIgnoreCase("Business Text Message Sent")) {
                        formLayMessage.setVisibility(View.VISIBLE);
                    } else {
                        formLayMessage.setVisibility(View.GONE);
                    }
                    if (mobile != null && mobile.length() > 0) {
                        // dbHelper.saveTranscationList();
                    }
                    Log.d("tag", "trnasction type ;" + tran_type);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
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
                case R.id.form_etMessage:
                    validateMessages();
                    break;
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateMessages() {
        if (formEtMessage.length() == 0) {
            formLayMessage.setError("Please enter message");
            requestFocus(formEtMessage);
            return false;
        } else {
            formLayMessage.setErrorEnabled(false);
        }
        return true;
    }


    private class TransctionListSpinnerAdapter extends ArrayAdapter<String> {
        private List<String> stringList;

        public TransctionListSpinnerAdapter(Context context, List<String> stringList) {
            super(context, R.layout.lay_spinner_item, R.id.textView1, stringList);
            this.stringList = stringList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public String getItem(int position) {
            return stringList.get(position);
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
            holder.textView1.setText(getItem(position));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
