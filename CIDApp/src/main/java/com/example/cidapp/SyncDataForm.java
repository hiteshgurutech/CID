package com.example.cidapp;

import android.content.Context;
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
import android.widget.Toast;

import com.example.cidapp.database.DatabaseHandler;
import com.example.cidapp.model.TransctionListModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SyncDataForm extends AppCompatActivity {

    @BindView(R.id.form_etPhoneNumber)
    TextInputEditText formEtPhoneNumber;
    @BindView(R.id.form_layPhoneNumber)
    TextInputLayout formLayPhoneNumber;
    @BindView(R.id.form_etName)
    TextInputEditText formEtName;
    @BindView(R.id.form_layName)
    TextInputLayout formLayName;
    @BindView(R.id.form_etNotes)
    TextInputEditText formEtNotes;
    @BindView(R.id.form_layNote)
    TextInputLayout formLayNote;
    @BindView(R.id.form_transctionTypeList)
    Spinner formTransctionTypeList;
    @BindView(R.id.form_btnSyncData)
    AppCompatButton formBtnSyncData;
    private SyncDataForm activity;
    private String tran_id = "", tran_type = "", mobile = "";

    private List<TransctionListModel> transctionListModelList;
    private DatabaseHandler dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data_form);
        ButterKnife.bind(this);
        activity = this;
        getSupportActionBar().setTitle("Sync Data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHelper = CIDApp.getInstance().getDatabase();
        init();

        formBtnSyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("tag", "click in to sync noe button ");
                if (tran_id != null && tran_type != null && formEtPhoneNumber.getText().toString().length() > 0) {
                   // dbHelper.saveTranscationList(tran_id, formEtPhoneNumber.getText().toString(), tran_type);
                    Log.d("tag", "click in to sync noe button data insert ");
                }
            }
        });
    }

    private void init() {

        formEtPhoneNumber.addTextChangedListener(new MyTextWatcher(formEtPhoneNumber));
        formEtName.addTextChangedListener(new MyTextWatcher(formEtName));
        formEtNotes.addTextChangedListener(new MyTextWatcher(formEtNotes));

        getTranscationTypeList();
    }

    private void getTranscationTypeList() {

        Toast.makeText(SyncDataForm.this, "Transction list api here call", Toast.LENGTH_SHORT).show();

        setUpSpinner();

    }

    private void setUpSpinner() {

        transctionListModelList = new ArrayList<>();
        for (int i = 0; i <= 2; i++) {
            TransctionListModel model = new TransctionListModel();
            model.setTranscationType("TransctionType" + i);
            transctionListModelList.add(model);

        }
        formTransctionTypeList.setPrompt("Transaction Type List");

        formTransctionTypeList.setAdapter(new TransctionListSpinnerAdapter(activity, transctionListModelList));


        tran_type = transctionListModelList.get(0).getTranscationType();


        Log.d("tag", "trnasction type ;" + tran_type);

        formTransctionTypeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //   tran_id = transctionListModelList.get(i).getId();
                tran_type = transctionListModelList.get(i).getTranscationType();
                if (mobile != null && mobile.length() > 0) {
                    //dbHelper.saveTranscationList();
                }

                Log.d("tag", "trnasction type ;" + tran_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                case R.id.form_etPhoneNumber:
                    validatePhoneNumber();
                    break;
                case R.id.form_etName:
                    validateName();
                    break;
                case R.id.form_etNotes:
                    validateNotes();
                    break;
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validatePhoneNumber() {
        if (formEtPhoneNumber.length() == 0) {
            formLayPhoneNumber.setError("Please enter phone number");
            requestFocus(formEtPhoneNumber);
            return false;
        } else {
            formLayPhoneNumber.setErrorEnabled(false);
            mobile = formEtPhoneNumber.getText().toString();
            if (tran_id != null && tran_id.length() > 0) {
                //dbHelper.saveTranscationList();
            }
        }
        return true;
    }

    private boolean validateName() {
        if (formEtName.length() == 0) {
            formLayName.setError("Please enter name");
            requestFocus(formEtName);
            return false;
        } else {
            formLayName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateNotes() {
        if (formEtNotes.length() == 0) {
            formLayNote.setError("Please enter note");
            requestFocus(formEtNotes);
            return false;
        } else {
            formLayNote.setErrorEnabled(false);
        }
        return true;
    }


    private class TransctionListSpinnerAdapter extends ArrayAdapter<TransctionListModel> {
        private List<TransctionListModel> transctionListModelList;

        public TransctionListSpinnerAdapter(Context context, List<TransctionListModel> transctionListModelList) {
            super(context, R.layout.lay_spinner_item, R.id.textView1, transctionListModelList);
            this.transctionListModelList = transctionListModelList;
        }

        @Override
        public int getCount() {
            return transctionListModelList.size();
        }

        @Override
        public TransctionListModel getItem(int position) {
            return transctionListModelList.get(position);
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
            holder.textView1.setText(getItem(position).getTranscationType());
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
