package com.example.smartscreenapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartscreenapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    WebView webview;
    JSONObject lastRes;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        webview = findViewById(R.id.webview);
        SharedPreferences mPrefs = this.getSharedPreferences("IP_S", 0);
        String url = mPrefs.getString("tag", "default_value_if_variable_not_found");
        code = mPrefs.getString("code", "default_value_if_variable_not_found");
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(url);
        new ThreadUpdate().start();
    }

    class ThreadUpdate extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(10000);
                    sendId(code);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendId(String orderCmd) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://mm2qr.com/api/monitors/listUserMonitors";
        URL = URL.replace(" ", "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY RESPONSE ", "response order : " + response);
            try {
                JSONObject res = new JSONObject(response);
                JSONArray array = res.getJSONArray("data");
                res = array.getJSONObject(0);
                if (!res.equals(lastRes))
                    getSreen(res.getString("umLayoutHashId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("userId", orderCmd);
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getSreen(String orderCmd) {
        System.out.println("screen : " + orderCmd);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://mm2qr.com/api/monitors/userMonitorInfo";
        URL = URL.replace(" ", "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.i("VOLLEY RESPONSE ", "response screen : " + response);
            try {
                JSONObject res = new JSONObject(response);
                res = res.getJSONObject("data");
                res = res.getJSONObject("embed");
                System.out.println("url : " + res.getString("url"));
                webview.loadUrl(res.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("umLayoutHashId", orderCmd);
                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }
}