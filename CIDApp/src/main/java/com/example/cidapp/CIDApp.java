package com.example.cidapp;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.cidapp.database.DatabaseHandler;
import com.example.cidapp.model.Country;
import com.example.cidapp.model.PhoneModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nilesh on 7/12/16.
 */
public class CIDApp extends Application {

    public static final String TAG = CIDApp.class.getSimpleName();
    private static CIDApp app;
    private DatabaseHandler dbHelper;
    private ConnectivityManager connManager;
    private RequestQueue mRequestQueue;
    private List<Country> countryList;
    private List<PhoneModel> phoneList = new ArrayList<>();

    public static synchronized CIDApp getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public DatabaseHandler getDatabase() {
        if (dbHelper == null) {
            dbHelper = new DatabaseHandler(this);
        }
        return dbHelper;
    }

    public boolean isWIFIConnected() {
        if (connManager == null) {
            connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            return isWiFi;
        }
        return false;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public List<Country> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    public List<PhoneModel> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<PhoneModel> phoneList) {
        this.phoneList = phoneList;
    }

    public boolean updateModel(PhoneModel model) {
        int c = -1;

        for (int i = 0; i < phoneList.size(); i++) {
            if (phoneList.get(i).getPhoneId().equals(model.getPhoneId())) {
                c = i;
                break;
            }
        }

        if (c >= 0) {
            phoneList.set(c, model);
            return true;
        } else
            return false;
    }
}

