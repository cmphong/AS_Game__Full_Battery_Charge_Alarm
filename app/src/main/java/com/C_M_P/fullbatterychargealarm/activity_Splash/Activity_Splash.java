package com.C_M_P.fullbatterychargealarm.activity_Splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.C_M_P.fullbatterychargealarm.MainActivity;
import com.C_M_P.fullbatterychargealarm.R;

public class Activity_Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Activity_Splash.this, MainActivity.class));
                finish();
            }
        }, 3000);
    }
}