package com.C_M_P.fullbatterychargealarm.activity_Tutorial;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.C_M_P.fullbatterychargealarm.R;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class Activity_Tutorial extends AppCompatActivity {

    SubsamplingScaleImageView iv_lock_app_in_recent,
                                iv_setting_app_app_002,
                                iv_setting_app_app_003,
                                iv_autostart,
                                iv_battery_saver;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        String locale = getResources().getConfiguration().getLocales().get(0).getLanguage();

        iv_lock_app_in_recent = (SubsamplingScaleImageView)findViewById(R.id.img_tutorial_001);
        iv_lock_app_in_recent.setImage(ImageSource.resource(
                locale.equals("vi")
                ? R.drawable.tutorial_001_lock_app_in_recent_vi
                : R.drawable.tutorial_001_lock_app_in_recent_en
        ));

        iv_setting_app_app_002 = (SubsamplingScaleImageView)findViewById(R.id.img_tutorial_002_1);
        iv_setting_app_app_002.setImage(ImageSource.resource(
                locale.equals("vi")
                ? R.drawable.tutorial_002_setting_app_app_vi
                : R.drawable.tutorial_002_setting_app_app_en
        ));

        iv_autostart = (SubsamplingScaleImageView)findViewById(R.id.img_tutorial_002_2);
        iv_autostart.setImage(ImageSource.resource(
                locale.equals("vi")
                ? R.drawable.tutorial_002_autostart_vi
                : R.drawable.tutorial_002_autostart_en
        ));

        iv_setting_app_app_003 = (SubsamplingScaleImageView)findViewById(R.id.img_tutorial_003_1);
        iv_setting_app_app_003.setImage(ImageSource.resource(
                locale.equals("vi")
                ? R.drawable.tutorial_002_setting_app_app_vi
                : R.drawable.tutorial_002_setting_app_app_en
        ));

        iv_battery_saver = (SubsamplingScaleImageView)findViewById(R.id.img_tutorial_003_2);
        iv_battery_saver.setImage(ImageSource.resource(
                locale.equals("vi")
                ? R.drawable.tutorial_003_battery_saver_vi
                : R.drawable.tutorial_003_battery_saver_en
        ));


        Logd("Ngôn ngữ: " +locale);


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