package com.example.smartscreenapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartscreenapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText idScreen;
    Button btnConnect;
    String code;

    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        idScreen = findViewById(R.id.idScreen);
        btnConnect = findViewById(R.id.btnConnect);

        mPrefs = this.getSharedPreferences("IP_S", 0);
        String IP_Server = mPrefs.getString("tag", "nnnn");

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (idScreen.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Champs vide !!", Toast.LENGTH_SHORT).show();
                    return;
                }
                code = idScreen.getText().toString();
                sendId(code);
            }
        });
        String myString = "nnnn";
        if (IP_Server.equals(myString)) {

        } else {
            // sendId(idScreen.getText().toString());
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
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
                getSreen(res.getString("umLayoutHashId"));
                //order.getFoods().clear();
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

                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putString("tag", res.getString("url")).commit();
                mEditor.putString("code", code).commit();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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