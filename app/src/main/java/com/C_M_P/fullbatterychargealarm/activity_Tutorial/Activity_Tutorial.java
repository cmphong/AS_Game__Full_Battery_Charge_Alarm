package com.C_M_P.fullbatterychargealarm.activity_Tutorial;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.C_M_P.fullbatterychargealarm.MainActivity;
import com.C_M_P.fullbatterychargealarm.R;

import java.util.Locale;

public class Activity_Tutorial extends AppCompatActivity {

    WebView webView;
    String locale;
    String url;
    String brand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        locale = Locale.getDefault().getLanguage();

        webView = findViewById(R.id.wv_tutorial);

        String brand = getIntent().getStringExtra(MainActivity.BRAND);
        switch (brand){
            case "xiaomi":
                webView.loadUrl("https://dontkillmyapp.com/xiaomi");
                break;
            case "samsung":
                webView.loadUrl("https://dontkillmyapp.com/samsung");
                break;
            case "huawei":
                webView.loadUrl("https://dontkillmyapp.com/huawei");
                break;
            case "oneplus":
                webView.loadUrl("https://dontkillmyapp.com/oneplus");
                break;
            case "meizu":
                webView.loadUrl("https://dontkillmyapp.com/meizu");
                break;
            case "oppo":
                webView.loadUrl("https://dontkillmyapp.com/oppo");
                break;
        }



    }


    class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }
    }





    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_Tutorial.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Tutorial.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_Tutorial.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_Tutorial.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast( String str ){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}