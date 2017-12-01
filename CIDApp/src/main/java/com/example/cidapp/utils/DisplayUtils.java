package com.example.cidapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.SslError;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cidapp.R;

/**
 * Created by nilesh on 10/12/16.
 */
public class DisplayUtils {

    public static ProgressDialog progressDialog;
    static {

    }
    public static void showProgressDialog(Context context,String message)
    {

            progressDialog=new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
    }

    public static void hideProgressDiaog()
    {
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public static void showCustomToast(Context context,String msg)
    {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.custom_toast,null);
        int Y = context.getResources().getDimensionPixelSize(android.support.v7.appcompat.R.dimen.abc_action_bar_default_height_material);
        Toast toast=new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,Y);
        TextView txtToast=(TextView)view.findViewById(R.id.txtToast);
        txtToast.setText(msg);
        toast.setView(view);
        toast.show();

    }
    public static Dialog displayCMSDialog(Context context,String title,String url)
    {

        final Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_full_screen);

        AppCompatTextView tvTitle= (AppCompatTextView) dialog.findViewById(R.id.dialog_tvTitle);
        AppCompatButton btnIAgree=(AppCompatButton)dialog.findViewById(R.id.dialog_btnIAgree);
        WebView webView=(WebView)dialog.findViewById(R.id.dialog_webView);
        ImageView btnClose=(ImageView)dialog.findViewById(R.id.dialog_btnClose);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }
        });

        Log.d("tag","==> URL:"+url+".");
        webView.loadUrl(url.trim());
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnIAgree.setVisibility(View.GONE);
        tvTitle.setText(title);
        dialog.show();
        return dialog;
    }

    public static void hideKeyboard(Context context)
    {

        View view = ((Activity)context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
