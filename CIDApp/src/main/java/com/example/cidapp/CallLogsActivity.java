package com.example.cidapp;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.cidapp.model.CallHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallLogsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CallLogs";
    private static final int URL_LOADER = 1;
    ListView listCallLogs;
    List<CallHistory> callLogs;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);
        getSupportActionBar().setTitle("CallLogs");

        listCallLogs = (ListView) findViewById(R.id.listCallLogs);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportLoaderManager().initLoader(URL_LOADER,null,CallLogsActivity.this);
            }
        },2000l);*/

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        Log.d(TAG, "onCreateLoader() >> loaderID : " + loaderID);

        switch (loaderID) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        CallLog.Calls.CONTENT_URI,        // Table to query
                        null,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor managedCursor) {
        Log.d(TAG, "onLoadFinished()");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

        callLogs = new ArrayList<>();
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String callerName = managedCursor.getString(name);
            String dir = null;

            int callTypeCode = Integer.parseInt(callType);
            switch (callTypeCode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "Outgoing";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "Incoming";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "Missed";
                    break;
            }


            CallHistory model = new CallHistory();
            model.setName(callerName);
            model.setPhoneNumber(phNumber);
            model.setCallDate(callDayTime);
            model.setCallType(dir);
            model.setCallDuration(callDuration);
            callLogs.add(model);
        }


        managedCursor.close();
        progressBar.setVisibility(View.GONE);
        listCallLogs.setAdapter(new CallLogsAdapter());
        //callLogsTextView.setText(Html.fromHtml(sb.toString()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset()");
        // do nothing
    }

    class CallLogsAdapter extends BaseAdapter {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMM dd yyyy, hh:mm a");
        private final ColorGenerator generator;

        public CallLogsAdapter() {
            generator = ColorGenerator.MATERIAL;
        }

        @Override
        public int getCount() {
            return callLogs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = getLayoutInflater().inflate(R.layout.lay_call_log_row, null);
                holder.txtName = (TextView) convertView.findViewById(R.id.log_txtName);
                holder.txtPhone = (TextView) convertView.findViewById(R.id.log_txtPhoneNo);
                holder.txtDate = (TextView) convertView.findViewById(R.id.log_txtDate);
                holder.txtType = (TextView) convertView.findViewById(R.id.log_txtCallType);
                holder.txtDuration = (TextView) convertView.findViewById(R.id.log_txtDuration);
                holder.imgContact = (ImageView) convertView.findViewById(R.id.log_imgContact);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            CallHistory model = callLogs.get(position);

            String color;
            int res;
            if (model.getCallType().equalsIgnoreCase("incoming")) {
                color = "#44dd44";
                res = R.drawable.ic_call_received_black_24dp;
            } else if (model.getCallType().equalsIgnoreCase("outgoing")) {
                color = "#088dc6";
                res = R.drawable.ic_call_made_black_24dp;
            } else {
                color = "#dd4444";
                res = R.drawable.ic_call_missed_black_24dp;
            }
            Drawable img = getResources().getDrawable(res);
            img.setBounds(0, 0, 24, 24);

            if (model.getName() == null) {
                holder.txtName.setVisibility(View.GONE);
                holder.txtPhone.setVisibility(View.VISIBLE);
                holder.imgContact.setImageResource(R.drawable.ic_account_circle_black_24dp);
                holder.imgContact.setColorFilter(generator.getRandomColor());
            } else {
                holder.txtPhone.setVisibility(View.GONE);
                holder.txtName.setVisibility(View.VISIBLE);
                holder.txtName.setText(model.getName());
                TextDrawable drawable1 = TextDrawable.builder()
                        .beginConfig().useFont(Typeface.DEFAULT).bold().textColor(Color.WHITE).endConfig()

                        .buildRound("" + model.getName().toUpperCase().charAt(0), generator.getRandomColor());
                holder.imgContact.setImageDrawable(drawable1);
            }
            holder.txtPhone.setText(model.getPhoneNumber());
            holder.txtType.setCompoundDrawables(img, null, null, null);
            holder.txtType.setCompoundDrawablePadding(10);
            holder.txtType.setText(model.getCallType() + " call");
            // holder.txtType.setTextColor(Color.parseColor(color));
            holder.txtDate.setText(sdf.format(model.getCallDate()));
            holder.txtDuration.setText(model.getCallDuration() + " sec");
            return convertView;
        }

        class Holder {
            TextView txtName, txtPhone, txtType, txtDate, txtDuration;
            ImageView imgContact;
        }
    }
}
