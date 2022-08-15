package com.C_M_P.fullbatterychargealarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.C_M_P.fullbatterychargealarm.activity_Tutorial.Activity_Tutorial;
import com.C_M_P.fullbatterychargealarm.broadcast_receiver.MyBroadcast;
import com.C_M_P.fullbatterychargealarm.service.MyService;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;

    public static final String BRAND = "BRAND";
    public static final String KEY_1 = "KEY_1";
    public static final String CHARGER_ALERT = "CHARGER_ALERT";
    public static final String KEY_isServiceRunning = "KEY_isServiceRunning";
    public static final String KEY_ACTIVITYMAIN = "KEY_ACTIVITYMAIN";

    private MyBroadcast myBroadcast;

    private static TextView tv_battery_level;
    private static TextView tv_voltage;
    private static TextView tv_capacity;
    private static TextView tv_temperature;
    private static TextView tv_timer;
    private TextView tv_alarm_volume;
    private static ImageView iv_info_app;
    private static ImageView iv_battery_level;
    private ImageView iv_alarm_volume;
    private static LottieAnimationView lottie_animation_charge;
    private static LottieAnimationView lottie_animation_wave;
    private Slider sb_alarm_volume;

    private AdView mAdView;

    private SwitchCompat sw_on_off;
    private boolean isServiceRunning = false;
    private SharedPreferences sharedPreferences;

    AudioManager audioManager;

    private static Timer timer;
    private static int hour = 0, minute = 0, second = 0;
    private static boolean isWatchRunning = false;


    // AdMob - Adaptive Banner
    private FrameLayout fl_ad_view_container;
    private AdView adView;

    // In-App Review
    ReviewManager manager;
    ReviewInfo reviewInfo;

    private static Dialog dialog_full_charged;
    private static LinearLayout ll_dialog_full_charged_container;
    private TextView tv_dialog_info_app_title;
    private TextView tv_dialog_info_app_content_xiaomi,
                    tv_dialog_info_app_content_samsung,
                    tv_dialog_info_app_content_huawei,
                    tv_dialog_info_app_content_oneplus,
                    tv_dialog_info_app_content_meizu,
                    tv_dialog_info_app_content_oppo;
    private Button btn_dialog_info_app_ok;
    LinearLayout ll_dialog_info_app_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        initialComponents();
        initialAdMob();

        timer = new Timer();
        dialog_full_charged = new Dialog(this);


        // START - Adaptive AdMob ======================================
        fl_ad_view_container = findViewById(R.id.fl_ad_view_container);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getResources().getString(R.string.adMob_unitID_REAL_AD));
        fl_ad_view_container.addView(adView);
        loadBanner();
        // END - Adaptive AdMob ======================================

        // START - In-App Review ===============================================
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
            } else {
                // There was some problem, log or handle the error code.
                Logdln("Review failed to start", 154);
            }
        });
        // END - In-App Review ===============================================================

        audioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);

        tv_capacity.setText(getBatteryCapacity() +"mAh");

        sharedPreferences = getSharedPreferences(CHARGER_ALERT, MODE_PRIVATE);
        isServiceRunning = sharedPreferences.getBoolean(KEY_isServiceRunning, false);

        if(isServiceRunning && MyService.isServiceRunning){
            sw_on_off.setChecked(true);
            enableVolumn();
        }else{
            sw_on_off.setChecked(false);
            disableVolumn();
        }

        myBroadcast = new MyBroadcast();

        sw_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(MainActivity.this, MyService.class);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_isServiceRunning, isChecked);
                editor.apply();

                if(isChecked) { // ON
                    startService(intent);
                    enableVolumn();
                }else{ // OFF
                    stopService(intent);
                    disableVolumn();
                }
            }
        });

        iv_info_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog_Info();
            }
        });

        sb_alarm_volume.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        (int) value,
                        AudioManager.FLAG_PLAY_SOUND
                );
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        Logd("onStart()");
        registerReceiver(myBroadcast, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onPause() {

        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Logd("onStop()");
        if(!sharedPreferences.getBoolean(KEY_isServiceRunning, false)) {
            unregisterReceiver(myBroadcast);
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    // START - In-App Review ======================================================
    private void startInAppReview(){
        Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
        flow.addOnCompleteListener(task -> {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
            Logdln("Review completed, continue your stuff...", 217);
        });
    }
    // END - In-App Review ======================================================

    // START - AdMob Adaptive Banner ===========================================================
    private void initialAdMob() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // for Standard Banner ===========================================================
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
    }
    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
    private void loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder().build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);


        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }
    // END - AdMob Adaptive Banner ===========================================================

    public static void startWatch(){
        if(isWatchRunning) return;
        isWatchRunning = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                second++;
                if(second >= 60) {
                    second = 0;
                    minute++;
                }
                if(minute >= 60){
                    minute = 0;
                    hour++;
                }
                if(hour >= 60) hour = 0;

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_timer.setVisibility(View.VISIBLE);
                        tv_timer.setText(
                            (  hour < 10 ? "0"+hour   : ""+hour)   +":"+
                            (minute < 10 ? "0"+minute : ""+minute) +":"+
                            (second < 10 ? "0"+second : ""+second)
                        );
                    }
                });

            }
        }, 0, 1000);
    }

    public static void stopWatch(){
        isWatchRunning = false;
        timer.cancel();
        hour = 0; minute = 0; second = 0;
        tv_timer.setText("00:00:00");
        tv_timer.setVisibility(View.INVISIBLE);
    }

    public void enableVolumn(){
        tv_alarm_volume.setTextColor(Color.WHITE);
        sb_alarm_volume.setEnabled(true);
        iv_alarm_volume.setImageResource(R.drawable.ic_sound_min);

        int mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        sb_alarm_volume.setValue(mediaVolume);

    }

    public void disableVolumn(){
        tv_alarm_volume.setTextColor(ActivityCompat.getColor(this, R.color.grey300));
        sb_alarm_volume.setEnabled(false);
        iv_alarm_volume.setImageResource(R.drawable.ic_sound_min_disable);
    }

    public static void showDialog_full_battery_charged() {
        dialog_full_charged.setContentView(R.layout.dialog_full_battery);
        dialog_full_charged.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // onclick event
        ll_dialog_full_charged_container = dialog_full_charged.findViewById(R.id.dialog_full_battery_ll_container);
        ll_dialog_full_charged_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog_full_charged.isShowing()) dialog_full_charged.dismiss();
            }
        });

        dialog_full_charged.show();
    }

    private void showDialog_Info() {

        // GET WIDTH SCREEN DEVICE
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float pxWidth = displayMetrics.widthPixels;


        Dialog dialog_info = new Dialog(this);
        dialog_info.setContentView(R.layout.dialog_info);

        ll_dialog_info_app_container = dialog_info.findViewById(R.id.ll_dialog_info_app_container);
        tv_dialog_info_app_title = dialog_info.findViewById(R.id.tv_dialog_info_app_title);
        tv_dialog_info_app_content_xiaomi = dialog_info.findViewById(R.id.tv_dialog_info_app_content_xiaomi);
        tv_dialog_info_app_content_samsung = dialog_info.findViewById(R.id.tv_dialog_info_app_content_samsung);
        tv_dialog_info_app_content_huawei = dialog_info.findViewById(R.id.tv_dialog_info_app_content_huawei);
        tv_dialog_info_app_content_oneplus = dialog_info.findViewById(R.id.tv_dialog_info_app_content_oneplus);
        tv_dialog_info_app_content_meizu = dialog_info.findViewById(R.id.tv_dialog_info_app_content_meizu);
        tv_dialog_info_app_content_oppo = dialog_info.findViewById(R.id.tv_dialog_info_app_content_oppo);
        btn_dialog_info_app_ok = dialog_info.findViewById(R.id.btn_dialog_info_app_ok);

        ll_dialog_info_app_container.getLayoutParams().width = (int) pxWidth - 200;

        tv_dialog_info_app_title.setText(getResources().getString(R.string.Version) + " " +BuildConfig.VERSION_NAME);

        // onclick event

        tv_dialog_info_app_content_xiaomi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_Tutorial.class);
                intent.putExtra(BRAND, "xiaomi");
                startActivity(intent);
            }
        });
        tv_dialog_info_app_content_samsung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_Tutorial.class);
                intent.putExtra(BRAND, "samsung");
                startActivity(intent);
            }
        });
        tv_dialog_info_app_content_huawei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_Tutorial.class);
                intent.putExtra(BRAND, "huawei");
                startActivity(intent);
            }
        });
        tv_dialog_info_app_content_oneplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_Tutorial.class);
                intent.putExtra(BRAND, "oneplus");
                startActivity(intent);
            }
        });
        tv_dialog_info_app_content_meizu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_Tutorial.class);
                intent.putExtra(BRAND, "meizu");
                startActivity(intent);
            }
        });
        tv_dialog_info_app_content_oppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_Tutorial.class);
                intent.putExtra(BRAND, "oppo");
                startActivity(intent);
            }
        });

        btn_dialog_info_app_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_info.dismiss();
//                startInAppReview();
            }
        });

        dialog_info.show();

    }

    public static void showBatteryInfo(float voltage, int temperature){
        DecimalFormat dm = new DecimalFormat(voltage == 0 ? "0" : "0.0");
        tv_voltage.setText(dm.format(voltage/1000) + "V");
        tv_temperature.setText((temperature/10) +"" +(char) 0x00B0 +"C");
    }

    public int getBatteryCapacity() {
        Object mPowerProfile_ = null;
        double result = 0;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            result = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (int) result;
    }

    public static void showChargingAnimation(boolean isCharge){
        if(isCharge){
            lottie_animation_charge.playAnimation();
            lottie_animation_charge.setVisibility(View.VISIBLE);

            iv_battery_level.setVisibility(View.GONE);

            lottie_animation_wave.playAnimation();

        }else{
            lottie_animation_charge.pauseAnimation();
            lottie_animation_charge.setVisibility(View.GONE);

            iv_battery_level.setVisibility(View.VISIBLE);

            lottie_animation_wave.pauseAnimation();
        }
    }

    public static void setBatteryLevel(float n){
        tv_battery_level.setText((int) n +"%");

        if(n <= 20){
            iv_battery_level.setImageResource(R.drawable.level_1);
            tv_battery_level.setTextColor(Color.parseColor("#de6409"));
        }
        else if(n > 20 && n <= 55){
            iv_battery_level.setImageResource(R.drawable.level_2);
            tv_battery_level.setTextColor(Color.parseColor("#deb908"));
        }
        if(n > 55 && n < 100){
            iv_battery_level.setImageResource(R.drawable.level_3);
            tv_battery_level.setTextColor(Color.parseColor("#35c500"));
        }
        if(n == 100){
            iv_battery_level.setImageResource(R.drawable.level_4);
            tv_battery_level.setTextColor(Color.parseColor("#35c500"));
        }
    }

    private void initialComponents() {
        tv_battery_level        = findViewById(R.id.tv_battery_level);
        tv_voltage              = findViewById(R.id.tv_voltage);
        tv_capacity             = findViewById(R.id.tv_capacity);
        tv_temperature          = findViewById(R.id.tv_temperature);
        tv_timer                = findViewById(R.id.tv_timer);
        lottie_animation_charge = findViewById(R.id.lottie_animation_charge);
        lottie_animation_wave   = findViewById(R.id.lottie_animation_wave);
        iv_battery_level        = findViewById(R.id.iv_battery_level);
        iv_info_app             = findViewById(R.id.iv_info_app);
        
        tv_alarm_volume         = findViewById(R.id.tv_alarm_volume);
        sw_on_off               = findViewById(R.id.sw_on_off);
        iv_alarm_volume         = findViewById(R.id.iv_alarm_volume);
        sb_alarm_volume         = findViewById(R.id.sb_alarm_volume);
    }








    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== MainActivity.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== MainActivity.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== MainActivity.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== MainActivity.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast( String str ){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


}