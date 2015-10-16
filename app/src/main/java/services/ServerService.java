package services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.teachervirus.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import ui.ConsoleActivity;
import ui.SettingActivity;
import utils.Utils;


public class ServerService extends Service {

    private static final String TAG = ServerService.class.getSimpleName();
    private final IBinder mBinder = new ServerBinder();
    private PowerManager.WakeLock wakeLock = null;

    private SharedPreferences preferences;
    private Handler handler = new Handler(Looper.getMainLooper());

    private static String htdocs = Environment.getExternalStorageDirectory() + File.separator + "htdocs";

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(mConnectionReceiver,
                mIntentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();
        return (START_NOT_STICKY);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mConnectionReceiver);
        destroyService();
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    protected void initialize() {

        Context context = getApplicationContext();
        NotificationManager noti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("DroidPHP service started")
                        .setContentText("Web Service started");

        Intent notificationIntent = new Intent(context, ConsoleActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(), 143, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);
        startForeground(143, mBuilder.build());
        noti.notify(143, mBuilder.build());

        if (preferences.getBoolean("enable_screen_on", false)) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DPScreenLock");
            wakeLock.acquire();
        }

        if (preferences.getBoolean("enable_lock_wifi", false)) {
            /*  not implemented */
        }
    }

    protected void destroyService() {
        NotificationManager notify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notify.cancel(143);
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public class ServerBinder extends Binder {

        ServerService getService() {
            return ServerService.this;
        }
    }


  private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {

          Log.e(TAG,"Ip Written from service");
          checkHtdocsOK();
          String ipAddress = Utils.getIPAddress(true);
          if(ipAddress.trim().isEmpty()){
              writeIpAddress("http://"+"localhost"+":8080");
          }else{
              writeIpAddress("http://"+ Utils.getIPAddress(true)+":8080");
          }
      }
  };
    public boolean checkHtdocsOK() {
        File file = new File(htdocs);
        if (file.exists()) {
            return true;
        }
        return file.mkdirs();

    } // END checkHtdocsOK

    private void writeIpAddress(String ipAddress){

        File ipFile = new File(htdocs,"IP.txt");
        try {
            FileOutputStream f = new FileOutputStream(ipFile);
            PrintWriter pw = new PrintWriter(f);
            pw.print(ipAddress);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}