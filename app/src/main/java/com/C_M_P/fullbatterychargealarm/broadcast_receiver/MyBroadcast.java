package com.C_M_P.fullbatterychargealarm.broadcast_receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.C_M_P.fullbatterychargealarm.MainActivity;
import com.C_M_P.fullbatterychargealarm.R;
import com.C_M_P.fullbatterychargealarm.initial.MyApplication;
import com.C_M_P.fullbatterychargealarm.service.MyService;

// Training:
// https://developer.android.com/training/monitoring-device-state/battery-monitoring#java
public class MyBroadcast extends BroadcastReceiver {


    SharedPreferences sharedPreferences;
    boolean isCharge = false;
    boolean isShowGifNotification = false;


    @Override
    public void onReceive(Context context, Intent intent) {

        // Guides from developer.android.com
        // How are we charging?
        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        // Power source is an USB Port
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        // Power source is an AC charger
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;


        // ========================================
        // Xác định phần trăm pin
        // Guides from developer.android.com
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPercent = level * 100 / (float)scale;

        sharedPreferences = context.getSharedPreferences(MainActivity.CHARGER_ALERT, Context.MODE_PRIVATE);
        boolean isStartAlarm = sharedPreferences.getBoolean(MainActivity.KEY_isServiceRunning, false);

        if(isStartAlarm && MyService.isServiceRunning) {
            if (acCharge || usbCharge) {
                isCharge = true;
                if(!isShowGifNotification) {
                    showGifNotification(context, false);
                    isShowGifNotification = true;
                }
                if (batteryPercent == 100) {
                    MyService.startAlarm(context);
                    MainActivity.showDialog();
                    showGifNotification(context, true);
                    isShowGifNotification = false;
                }
            }else{
                if(isCharge) {
                    showImageNotification(context);
                    isShowGifNotification = false;
                }
                isCharge = false;
                MyService.stopAlarm(context);
            }
        }

        if(acCharge || usbCharge) {
            MainActivity.showChargingAnimation(true);
            MainActivity.startWatch();
        }else{
            MainActivity.showChargingAnimation(false);
            MainActivity.stopWatch();

        }
        MainActivity.setBatteryLevel(batteryPercent);

        // ==================================================
        float voltage = 0;
        int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        if(acCharge || usbCharge){
            voltage = (float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        }
        MainActivity.showBatteryInfo(voltage, temperature);
    }


    private void showGifNotification(Context context, boolean isFull) {

        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(),
                isFull
                    ? R.layout.notification_custom_full_battery
                    : R.layout.notification_custom_charging
        );

        // =======================================================
        // Start Activity from Notification
        Intent intentOpenActivity = new Intent(context, MainActivity.class);
        // Set the Activity to start in a new, empty task
        intentOpenActivity.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intentOpenActivity,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        // =======================================================

        // START - Notification =======================================================
        Notification notification = new NotificationCompat.Builder(
                context,
                MyApplication.CHANNEL_ID)
                .setContentTitle(context.getResources().getString(R.string.Full_Battery_Alert_is_running))
//                        .setContentText(getString(R.string.Tap_for_more_infomation))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentIntent(pendingIntent) // tạo hành động khi click lên Notification:
                // Khi click lên, sẽ chuyển tới activity
                // Main_Demo_ForegroundService bằng intent
                .setCustomContentView(remoteViews) // Sử dụng Custom layout
//                        .setColor(getResources().getColor(R.color.white))
//                        .setColorized(true)
//                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle()) // show content title in setContentTitle() above
                .build();
        // END - Notification ====================================================


        // (2) - Cách 2 - Sử dụng NotificationManagerCompat
        // Khi sử dụng cách này thì ta không cần phải check "null" như cách 1
        // vì trong class NotificationManagerCompat đã xử lý check null rồi
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(MyService.MY_NOTIFICATION_ID, notification);

    }

    private void showImageNotification(Context context) {

        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(),
                R.layout.notification_custom_app_running
        );

        // =======================================================
        // Start Activity from Notification
        Intent intentOpenActivity = new Intent(context, MainActivity.class);
        // Set the Activity to start in a new, empty task
        intentOpenActivity.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intentOpenActivity,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        // =======================================================

        // START - Notification =======================================================
        Notification notification = new NotificationCompat.Builder(
                context,
                MyApplication.CHANNEL_ID)
                .setContentTitle(context.getResources().getString(R.string.Full_Battery_Alert_is_running))
//                        .setContentText(getString(R.string.Tap_for_more_infomation))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentIntent(pendingIntent) // tạo hành động khi click lên Notification:
                // Khi click lên, sẽ chuyển tới activity
                // Main_Demo_ForegroundService bằng intent
                .setCustomContentView(remoteViews) // Sử dụng Custom layout
//                        .setColor(getResources().getColor(R.color.white))
//                        .setColorized(true)
//                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle()) // show content title in setContentTitle() above
                .build();
        // END - Notification ====================================================


        // (2) - Cách 2 - Sử dụng NotificationManagerCompat
        // Khi sử dụng cách này thì ta không cần phải check "null" như cách 1
        // vì trong class NotificationManagerCompat đã xử lý check null rồi
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(MyService.MY_NOTIFICATION_ID, notification);

    }
}
